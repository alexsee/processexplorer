package de.tk.processmining.data.query.condition;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import de.tk.processmining.data.DatabaseModel;

import java.util.ArrayList;
import java.util.List;

public class ClusterCondition extends Condition {

    private Long clusterIndex;

    @Override
    public List<com.healthmarketscience.sqlbuilder.Condition> getCondition(DatabaseModel db) {
        var conditions = new ArrayList<com.healthmarketscience.sqlbuilder.Condition>();
        if (clusterIndex != null) {
            conditions.add(BinaryCondition.equalTo(db.variantsTable.addColumn("cluster_index"), clusterIndex));
        }
        return conditions;
    }

    public void setClusterIndex(Long clusterIndex) {
        this.clusterIndex = clusterIndex;
    }
}
