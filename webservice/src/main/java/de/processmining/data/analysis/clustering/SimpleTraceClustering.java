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
import de.processmining.data.analysis.metrics.SequenceMetrics;
import de.processmining.data.query.QueryService;
import de.processmining.utils.ClusterUtils;
import de.processmining.webservice.database.EventLogFeatureRepository;
import de.processmining.webservice.database.EventLogRepository;
import de.processmining.webservice.database.entities.EventLog;
import de.processmining.webservice.database.entities.EventLogFeature;
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

import java.util.ArrayList;
import java.util.concurrent.Future;

/**
 * @author Alexander Seeliger on 27.09.2019.
 */
@Service
public class SimpleTraceClustering {

    private static Logger logger = LoggerFactory.getLogger(SimpleTraceClustering.class);

    private EventLogRepository eventLogRepository;
    private EventLogFeatureRepository eventLogFeatureRepository;
    private SimpMessagingTemplate messagingTemplate;
    private QueryService queryService;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public SimpleTraceClustering(EventLogRepository eventLogRepository,
                                 EventLogFeatureRepository eventLogFeatureRepository,
                                 SimpMessagingTemplate messagingTemplate,
                                 QueryService queryService,
                                 JdbcTemplate jdbcTemplate) {
        this.eventLogRepository = eventLogRepository;
        this.eventLogFeatureRepository = eventLogFeatureRepository;
        this.messagingTemplate = messagingTemplate;
        this.queryService = queryService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Async
    public Future<EventLog> cluster(String logName) {
        logger.info("Begin simple trace clustering for \"{}\"", logName);

        // get event log
        var eventLog = eventLogRepository.findByLogName(logName);
        eventLog.setProcessing(true);
        eventLog = eventLogRepository.save(eventLog);

        // report processing
        messagingTemplate.convertAndSend("/notifications/logs/analysis_started", eventLog);

        // compute distance matrix
        var variants = queryService.getAllPaths(logName, new ArrayList<>());
        double[][] distanceMatrix = new double[variants.size()][variants.size()];

        for (int i = 0; i < variants.size(); i++) {
            for (int j = 0; j < i; j++) {
                var variant_a = variants.get(i).getPathIndex();
                var variant_b = variants.get(j).getPathIndex();

                var distance = SequenceMetrics.getLevenshteinDistance(variant_a, variant_b);

                distanceMatrix[i][j] = distance;
                distanceMatrix[j][i] = distance;
            }
        }

        // compute clusters
        var algorithm = new HierarchicalClustering(new WardLinkage(distanceMatrix));
        var result = ClusterUtils.calculateOptimalClusters(algorithm, distanceMatrix);

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
        for (int i = 0; i < variants.size(); i++) {
            batch.add(new Object[]{result[i], variants.get(i).getId()});
        }
        jdbcTemplate.batchUpdate(sql, batch);

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

}
