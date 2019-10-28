package de.tk.processmining.data.analysis.clustering;

import com.healthmarketscience.sqlbuilder.AlterTableQuery;
import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.QueryPreparer;
import com.healthmarketscience.sqlbuilder.UpdateQuery;
import de.tk.processmining.data.DatabaseModel;
import de.tk.processmining.data.analysis.clustering.model.EventLogClusters;
import de.tk.processmining.data.analysis.clustering.model.Itemset;
import de.tk.processmining.data.analysis.clustering.model.ItemsetComparator;
import de.tk.processmining.data.analysis.clustering.model.VariantCluster;
import de.tk.processmining.data.analysis.itemsets.FrequentItemsetMiner;
import de.tk.processmining.data.analysis.metrics.EvaluationUtils;
import de.tk.processmining.data.analysis.metrics.ItemsetSimilarity;
import de.tk.processmining.data.analysis.metrics.SequenceSimilarity;
import de.tk.processmining.data.analysis.miner.FodinaProcessModel;
import de.tk.processmining.data.model.Log;
import de.tk.processmining.data.model.Variant;
import de.tk.processmining.data.query.CasesQuery;
import de.tk.processmining.data.query.QueryService;
import de.tk.processmining.data.query.condition.ComboCondition;
import de.tk.processmining.data.query.condition.ComboType;
import de.tk.processmining.data.query.condition.Condition;
import de.tk.processmining.data.query.condition.VariantCondition;
import net.metaopt.swarm.FitnessFunction;
import net.metaopt.swarm.pso.Particle;
import net.metaopt.swarm.pso.Swarm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import smile.clustering.HierarchicalClustering;
import smile.clustering.linkage.WardLinkage;

import java.util.*;

/**
 * @author Alexander Seeliger on 27.09.2019.
 */
@Service
public class MultiPerspectiveTraceClustering {

    private static Logger logger = LoggerFactory.getLogger(MultiPerspectiveTraceClustering.class);

    private QueryService queryService;
    private JdbcTemplate jdbcTemplate;
    private FrequentItemsetMiner itemsetMiner;

    private int numParticles = 10;
    private int numIterations = 5;

    private String logName;
    private Log log;
    private List<Variant> variants;
    private List<String> categoricalAttributes;

    @Autowired
    public MultiPerspectiveTraceClustering(FrequentItemsetMiner itemsetMiner,
                                           QueryService queryService,
                                           JdbcTemplate jdbcTemplate) {
        this.itemsetMiner = itemsetMiner;
        this.queryService = queryService;
        this.jdbcTemplate = jdbcTemplate;
    }

    public EventLogClusters cluster(String logName) {
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
        swarm.setMinPosition(new double[]{0.1, 5, 0});
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

            return clusters;
        }

        return null;
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
        minSupport = Math.round(minSupport * 1000d) / 1000d;

        var frequentItemsetListMap = new HashMap<Itemset, List<Variant>>();
        var frequentItemsetSupport = new HashMap<Itemset, Double>();

        // now we need to extract frequent patterns for each variant
        for (var variant : variants) {
            // ignore all variants with less than two cases
            if (variant.getOccurrence() <= 2)
                continue;

            var itemsetValues = new HashMap<FieldValue, Integer>();
            var valueMap = new HashMap<Integer, FieldValue>();

            // query for cases
            var conditions = new ArrayList<Condition>();
            conditions.add(new VariantCondition(variant.getId()));

            var cases = queryService.getCases(new CasesQuery(logName, conditions, categoricalAttributes));

            // extract itemsets
            var closedItemSets = itemsetMiner.getClosedItemsets(cases, itemsetValues, minSupport);

            // generate reverse map
            for (var keyValue : itemsetValues.entrySet()) {
                valueMap.put(keyValue.getValue(), keyValue.getKey());
            }

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

        List<VariantCluster> cls = new ArrayList<>(clusters.values());
        while (compactClusterByMoveStrategy(cls)) {
            // do nothing
        }

        // assign traces to clusters
        logger.info("Assign traces to clusters...");
        var eventLogClusters = assignTracesToClusters(clusters, minSupport);

        var silhouette = EvaluationUtils.silhouetteCoefficient(distanceMatrix, clusterMap);
        eventLogClusters.setSilhouetteCoefficient(silhouette);

        return eventLogClusters;
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
                double variantsDistance = SequenceSimilarity.calculateVariantDistance(v1, v2);
                double itemsetsDistance = (weight == 1) ? 0 : ItemsetSimilarity.calculateItemsetDistance(i1, i2);

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
                            distance = SequenceSimilarity.calculateVariantDistanceToCluster(cluster1.getVariants(), variant);
                        }

                        double distanceOther = SequenceSimilarity
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
     * Assigns the trace to the clusters.
     *
     * @param clusters
     * @param minSupport
     * @return
     */
    private EventLogClusters assignTracesToClusters(Map<Integer, VariantCluster> clusters, double minSupport) {
        EventLogClusters result = new EventLogClusters(new ArrayList<>(clusters.values()));
        result.getClusters().removeIf(x -> x.getVariants().size() == 0);
        result.setMinSupport(minSupport);

        return result;
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
        double minSupport = Math.round(support * 1000d) / 1000d;
        logger.debug("PSO values: [{}, {}, {}]", minSupport, (int) numClusters, weighting);

        // now generate the clusters for the given minSupport
        EventLogClusters clusters = mine(minSupport, (int) numClusters, weighting);
        if (clusters == null)
            return 0;

        // now we need to evaluate the clusters to calculate the weighted fitness
        double fitness = 0.0D;
        int numTraces = 0;

        for (VariantCluster cluster : clusters.getClusters()) {
            // generate selection
            var conditions = new ArrayList<Condition>();
            cluster.getVariants().forEach(x -> conditions.add(new VariantCondition(x.getId())));

            var conds = new ArrayList<Condition>();
            conds.add(new ComboCondition(ComboType.OR, conditions));

            // mine heuristic net
            var model = FodinaProcessModel.createInstance(queryService, logName, conds);
            numTraces += model.getSize();

            double currentFitness = model.getFitness();
            fitness += currentFitness * model.getSize();
        }

        // calc fitness
        fitness /= numTraces;

        return (fitness +
                ((double) numTraces / log.getNumTraces()) +
                clusters.getSilhouetteCoefficient() +
                (1 - (numClusters / 50))) / 4; // 50 is the max number of clusters to generate
    }
}
