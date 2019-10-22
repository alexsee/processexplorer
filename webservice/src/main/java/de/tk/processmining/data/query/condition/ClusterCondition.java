package de.tk.processmining.data.query.condition;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import de.tk.processmining.data.DatabaseModel;

public class ClusterCondition extends Condition {

    private Long clusterIndex;

    public ClusterCondition() {
    }

    public ClusterCondition(Long clusterIndex) {
        this.clusterIndex = clusterIndex;
    }

    @Override
    public com.healthmarketscience.sqlbuilder.Condition getCondition(DatabaseModel db) {
        if (clusterIndex != null) {
            return BinaryCondition.equalTo(db.variantsTable.addColumn("cluster_index"), clusterIndex);
        }
        return null;
    }

    public Long getClusterIndex() {
        return clusterIndex;
    }

    public void setClusterIndex(Long clusterIndex) {
        this.clusterIndex = clusterIndex;
    }
}
