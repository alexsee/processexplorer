package de.tk.processmining.data.analysis.clustering;

import com.healthmarketscience.sqlbuilder.AlterTableQuery;
import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.QueryPreparer;
import com.healthmarketscience.sqlbuilder.UpdateQuery;
import de.tk.processmining.data.DatabaseModel;
import de.tk.processmining.data.analysis.metrics.SequenceMetrics;
import de.tk.processmining.data.query.QueryService;
import de.tk.processmining.utils.ClusterUtils;
import de.tk.processmining.webservice.database.EventLogRepository;
import de.tk.processmining.webservice.database.entities.EventLog;
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
    private SimpMessagingTemplate messagingTemplate;
    private QueryService queryService;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public SimpleTraceClustering(EventLogRepository eventLogRepository,
                                 SimpMessagingTemplate messagingTemplate,
                                 QueryService queryService,
                                 JdbcTemplate jdbcTemplate) {
        this.eventLogRepository = eventLogRepository;
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

                var distance = 1 - SequenceMetrics.getLevenshteinDistance(variant_a, variant_b);

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

        // report processing
        messagingTemplate.convertAndSend("/notifications/logs/analysis_finished", eventLog);

        return new AsyncResult<>(eventLog);
    }

}
