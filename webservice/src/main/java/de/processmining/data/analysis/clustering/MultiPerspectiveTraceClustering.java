/*
 * ProcessExplorer
 * Copyright (C) 2019  Alexander Seeliger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.processmining.data.analysis.clustering;

import com.healthmarketscience.sqlbuilder.AlterTableQuery;
import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.QueryPreparer;
import com.healthmarketscience.sqlbuilder.UpdateQuery;
import de.processmining.data.DatabaseModel;
import de.processmining.data.analysis.clustering.model.*;
import de.processmining.data.analysis.itemsets.FrequentItemsetMiner;
import de.processmining.data.analysis.itemsets.Itemset;
import de.processmining.data.analysis.metrics.EvaluationUtils;
import de.processmining.data.analysis.metrics.ItemsetMetrics;
import de.processmining.data.analysis.metrics.SequenceMetrics;
import de.processmining.data.analysis.miner.FodinaProcessModel;
import de.processmining.data.model.FieldValue;
import de.processmining.data.model.Log;
import de.processmining.data.model.Variant;
import de.processmining.data.query.CasesQuery;
import de.processmining.data.query.QueryService;
import de.processmining.data.query.condition.ComboCondition;
import de.processmining.data.query.condition.ComboType;
import de.processmining.data.query.condition.Condition;
import de.processmining.data.query.condition.VariantCondition;
import de.processmining.webservice.database.EventLogFeatureRepository;
import de.processmining.webservice.database.EventLogRepository;
import de.processmining.webservice.database.entities.EventLog;
import de.processmining.webservice.database.entities.EventLogFeature;
import net.metaopt.swarm.FitnessFunction;
import net.metaopt.swarm.pso.Particle;
import net.metaopt.swarm.pso.Swarm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import smile.clustering.HierarchicalClustering;
import smile.clustering.linkage.WardLinkage;

import java.util.*;
import java.util.concurrent.Future;

/**
 * @author Alexander Seeliger on 27.09.2019.
 */
@Service
public class MultiPerspectiveTraceClustering {

    private static Logger logger = LoggerFactory.getLogger(MultiPerspectiveTraceClustering.class);

    private QueryService queryService;
    private JdbcTemplate jdbcTemplate;
    private EventLogRepository eventLogRepository;
    private EventLogFeatureRepository eventLogFeatureRepository;
    private SimpMessagingTemplate messagingTemplate;
    private FrequentItemsetMiner itemsetMiner;

    private int numParticles = 10;
    private int numIterations = 5;

    private String logName;
    private Log log;
    private List<Variant> variants;
    private List<String> categoricalAttributes;

    @Autowired
    public MultiPerspectiveTraceClustering(EventLogRepository eventLogRepository,
                                           EventLogFeatureRepository eventLogFeatureRepository,
                                           SimpMessagingTemplate messagingTemplate,
                                           FrequentItemsetMiner itemsetMiner,
                                           QueryService queryService,
                                           JdbcTemplate jdbcTemplate) {
        this.eventLogRepository = eventLogRepository;
        this.eventLogFeatureRepository = eventLogFeatureRepository;
        this.messagingTemplate = messagingTemplate;
        this.itemsetMiner = itemsetMiner;
        this.queryService = queryService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Async
    public Future<EventLog> cluster(String logName) {
        logger.info("Begin multi perspective trace clustering for \"{}\"", logName);

        // get event log
        var eventLog = eventLogRepository.findByLogName(logName);
        eventLog.setProcessing(true);
        eventLog = eventLogRepository.save(eventLog);

        // report processing
        messagingTemplate.convertAndSend("/notifications/logs/analysis_started", eventLog);

        this.logName = logName;
        this.log = queryService.getLogStatistics(logName);

        this.categoricalAttributes = queryService.getCategoricalCaseAttributes(logName);
        this.variants = queryService.getAllPaths(logName, new ArrayList<>());

        // use the optimizer for calculating the best support value
        Swarm swarm = new Swarm(this.numParticles, new Particle(3), new FitnessFunction(true) {

            @Override
            public double evaluate(double[] position) {
                return performPSOStep(position[0], position[1], position[2]);
            }

        });

        // set default pso values
        swarm.setInertia(0.729844);
        swarm.setMaxPosition(new double[]{0.8, Math.min(Math.max(2, variants.size()), 50), 1});
        swarm.setMinPosition(new double[]{0.2, 5, 0});
        swarm.setMaxMinVelocity(0.2);
        swarm.setGlobalIncrement(1.49618);
        swarm.setParticleIncrement(1.49618);

        // perform iterations
        for (int i = 0; i < this.numIterations; i++) {
            swarm.evolve();

            logger.debug("Iteration: {}", i);
            logger.debug("Best fitness: {}", swarm.getBestFitness());
            logger.debug("Best position: {}", swarm.getBestPosition());
            logger.debug("Numer of evaluations: {}", swarm.getNumberOfEvaluations());
        }

        // if we found a solution, return the best
        if (swarm.getBestPosition() != null) {
            EventLogClusters clusters = mine(swarm.getBestPosition()[0], (int) swarm.getBestPosition()[1],
                    swarm.getBestPosition()[2]);

            if (clusters == null) {
                return null;
            }

            clusters.setWeighting(swarm.getBestPosition()[2]);

            // update database
            updateDatabase(clusters);
        }

        logger.info("Finished simple trace clustering for \"{}\"", logName);

        eventLog.setProcessing(false);
        eventLog = eventLogRepository.save(eventLog);

        // store clustering feature
        var feature = eventLogFeatureRepository.findByEventLogLogNameAndFeature(logName, "clustering");
        if (feature == null) {
            feature = new EventLogFeature();
            feature.setEventLog(eventLog);
            feature.setFeature("clustering");
            feature.setValues("simple_trace_clustering");

            feature = eventLogFeatureRepository.save(feature);
        }

        // report processing
        messagingTemplate.convertAndSend("/notifications/logs/analysis_finished", eventLog);

        return new AsyncResult<>(eventLog);
    }

    private void updateDatabase(EventLogClusters clusters) {
        // add new column
        var db = new DatabaseModel(logName);
        var variantClusterIndexCol = db.variantsTable.addColumn("cluster_index", "integer", null);

        var sql = new AlterTableQuery(db.variantsTable)
                .setAddColumn(variantClusterIndexCol)
                .validate().toString();

        jdbcTemplate.execute("ALTER TABLE " + db.variantsTable.getTableNameSQL() + " DROP COLUMN IF EXISTS " + variantClusterIndexCol.getColumnNameSQL());
        jdbcTemplate.execute(sql);

        // update variants
        var preparer = new QueryPreparer();
        QueryPreparer.PlaceHolder clusterIndexParam = preparer.getNewPlaceHolder();
        QueryPreparer.PlaceHolder variantIdParam = preparer.getNewPlaceHolder();

        sql = new UpdateQuery(db.variantsTable)
                .addSetClause(variantClusterIndexCol, preparer.addStaticPlaceHolder(clusterIndexParam))
                .addCondition(BinaryCondition.equalTo(db.variantsIdCol, preparer.addStaticPlaceHolder(variantIdParam)))
                .validate().toString();

        var batch = new ArrayList<Object[]>();
        for (int i = 0; i < clusters.getClusters().size(); i++) {
            for (Variant variant : clusters.getClusters().get(i).getVariants()) {
                batch.add(new Object[]{i, variant.getId()});
            }
        }
        jdbcTemplate.batchUpdate(sql, batch);
    }

    public EventLogClusters mine(double minSupport, int numClusters, double weight) {
        var frequentItemsetListMap = new HashMap<Itemset, List<Variant>>();
        var frequentItemsetSupport = new HashMap<Itemset, Double>();

        // now we need to extract frequent patterns for each variant
        for (var variant : variants) {
            // ignore all variants with less than two cases
            if (variant.getOccurrence() <= 2)
                continue;

            // query for cases
            var conditions = new ArrayList<Condition>();
            conditions.add(new VariantCondition(new Long[] { variant.getId() }));

            var cases = queryService.getCases(new CasesQuery(logName, conditions, categoricalAttributes));

            // extract itemsets
            var itemsetValues = new HashMap<FieldValue, Integer>();
            var closedItemSets = itemsetMiner.getClosedItemsets(cases, itemsetValues, minSupport);

            // generate reverse map
            var valueMap = itemsetMiner.getReversed(itemsetValues);

            for (var itemset : closedItemSets) {
                var values = new Itemset();
                itemset.forEach(x -> values.add(valueMap.get(x)));

                var vars = frequentItemsetListMap.getOrDefault(values, new ArrayList<Variant>());
                vars.add(variant);

                frequentItemsetListMap.put(values, vars);

                // update support
                var support = frequentItemsetSupport.getOrDefault(values, 0.0D);
                support += itemset.getSupport();

                frequentItemsetSupport.put(values, support);
            }

        }

        // generate separate logs for each cluster
        var itemsets = new ArrayList<>(frequentItemsetListMap.keySet());
        itemsets.sort(new ItemsetComparator(frequentItemsetSupport));

        if (itemsets.size() < numClusters) {
            return null;
        }
        frequentItemsetSupport.clear();

        // distance matrix
        logger.info("Calculating distance matrix for " + itemsets.size() + " itemsets...");
        double[][] distanceMatrix = getDistanceMatrix(weight, itemsets, frequentItemsetListMap);

        // perform clustering
        logger.info("Clustering...");

        var algorithm = new HierarchicalClustering(new WardLinkage(distanceMatrix));
        var clusterMap = algorithm.partition(Math.min(numClusters, distanceMatrix.length));
        var clusters = buildClustersFromHACResult(clusterMap, itemsets, frequentItemsetListMap);

        // resolve overlaps
        logger.info("Resolve overlapping clusters...");

        var cls = new ArrayList<>(clusters.values());
        while (compactClusterByMoveStrategy(cls)) {
            // do nothing
        }

        // assign traces to clusters
        logger.info("Assign traces to clusters...");

        var result = new EventLogClusters(new ArrayList<>(clusters.values()));
        result.getClusters().removeIf(x -> x.getVariants().size() == 0);
        result.setMinSupport(minSupport);
        result.setSilhouetteCoefficient(EvaluationUtils.silhouetteCoefficient(distanceMatrix, clusterMap));

        return result;
    }

    /**
     * Returns the distance matrix for the given itemsets.
     *
     * @param weight
     * @param itemsets
     * @param frequentItemsetListMap
     * @return
     */
    private double[][] getDistanceMatrix(double weight, ArrayList<Itemset> itemsets, HashMap<Itemset, List<Variant>> frequentItemsetListMap) {
        double[][] distanceMatrix = new double[itemsets.size()][itemsets.size()];

        for (int i = 0; i < itemsets.size(); i++) {
            Itemset i1 = itemsets.get(i);
            var v1 = frequentItemsetListMap.get(i1);

            for (int j = 0; j < i; j++) {
                // get variants and itemsets
                Itemset i2 = itemsets.get(j);
                var v2 = frequentItemsetListMap.get(i2);

                // calc distance between variants
                double variantsDistance = SequenceMetrics.calculateVariantDistance(v1, v2);
                double itemsetsDistance = (weight == 1) ? 0 : ItemsetMetrics.calculateItemsetDistance(i1, i2);

                double distance = weight * variantsDistance + (1 - weight) * itemsetsDistance;
                distanceMatrix[i][j] = distance;
                distanceMatrix[j][i] = distance;
            }
        }

        return distanceMatrix;
    }

    /**
     * Builds the clusters using HAC.
     *
     * @param clusterMap
     * @param itemsets
     * @param frequentItemsetListMap
     * @return
     */
    private Map<Integer, VariantCluster> buildClustersFromHACResult(int[] clusterMap,
                                                                    List<Itemset> itemsets,
                                                                    Map<Itemset, List<Variant>> frequentItemsetListMap) {
        Map<Integer, VariantCluster> clusters = new HashMap<>();

        for (int i = 0; i < clusterMap.length; i++) {
            var cluster = clusters.getOrDefault(clusterMap[i], new VariantCluster(new HashSet<>(), new HashSet<>()));
            var variants = frequentItemsetListMap.get(itemsets.get(i));

            cluster.getVariants().addAll(variants);
            clusters.put(clusterMap[i], cluster);
        }

        return clusters;
    }

    /**
     * Compacts overlapping clusters by moving variants to the most similar cluster.
     *
     * @param clusters
     */
    private boolean compactClusterByMoveStrategy(List<VariantCluster> clusters) {
        // find variants in different clusters
        for (int i = 0; i < clusters.size(); i++) {
            var cluster1 = clusters.get(i);

            for (var variant : cluster1.getVariants()) {
                // get distance to current cluster
                double distance = -1;

                for (int j = 0; j < clusters.size(); j++) {
                    if (i == j)
                        continue;

                    var cluster2 = clusters.get(j);

                    if (cluster2.getVariants().contains(variant)) {
                        if (distance == -1) {
                            distance = SequenceMetrics.calculateVariantDistanceToCluster(cluster1.getVariants(), variant);
                        }

                        double distanceOther = SequenceMetrics
                                .calculateVariantDistanceToCluster(cluster2.getVariants(), variant);

                        if (distanceOther >= distance) {
                            cluster2.getVariants().remove(variant);
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Perform a single PSO step with the given optimization parameters and return the optimization value, i.e., the
     * weighted process model fitness.
     *
     * @param support
     * @param numClusters
     * @param weighting
     * @return
     */
    private double performPSOStep(double support, double numClusters, double weighting) {
        logger.debug("PSO values: [{}, {}, {}]", support, (int) numClusters, weighting);

        // now generate the clusters for the given minSupport
        EventLogClusters clusters = mine(support, (int) numClusters, weighting);
        if (clusters == null)
            return 0;

        // now we need to evaluate the clusters to calculate the weighted fitness
        double fitness = 0.0D;
        int traces = 0;

        for (VariantCluster cluster : clusters.getClusters()) {
            // generate selection
            var conditions = new ArrayList<Condition>();
            cluster.getVariants().forEach(x -> conditions.add(new VariantCondition(new Long[] { x.getId() })));

            var comboCondition = new ArrayList<Condition>();
            comboCondition.add(new ComboCondition(ComboType.OR, conditions));

            // mine heuristic net
            var model = FodinaProcessModel.createInstance(queryService, logName, comboCondition);
            traces += model.getSize();
            fitness += model.getFitness() * model.getSize();
        }

        // calc fitness
        fitness /= traces;

        return (fitness +
                ((double) traces / log.getNumTraces()) +
                clusters.getSilhouetteCoefficient() +
                (1 - (numClusters / 50))) / 4; // 50 is the max number of clusters to generate
    }
}
