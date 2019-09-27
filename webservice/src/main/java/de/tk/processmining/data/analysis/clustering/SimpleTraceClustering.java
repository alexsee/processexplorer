package de.tk.processmining.data.analysis.clustering;

import com.healthmarketscience.sqlbuilder.AlterTableQuery;
import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.QueryPreparer;
import com.healthmarketscience.sqlbuilder.UpdateQuery;
import de.tk.processmining.data.DatabaseModel;
import de.tk.processmining.data.analysis.metrics.SequenceMetrics;
import de.tk.processmining.data.query.QueryManager;
import de.tk.processmining.utils.ClusterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import smile.clustering.HierarchicalClustering;
import smile.clustering.linkage.WardLinkage;

import java.util.ArrayList;

/**
 * @author Alexander Seeliger on 27.09.2019.
 */
@Service
public class SimpleTraceClustering {

    private QueryManager queryManager;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public SimpleTraceClustering(QueryManager queryManager, JdbcTemplate jdbcTemplate) {
        this.queryManager = queryManager;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void cluster(String logName) {
        var variants = queryManager.getAllPaths(logName);

        // compute distance matrix
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
    }

}
