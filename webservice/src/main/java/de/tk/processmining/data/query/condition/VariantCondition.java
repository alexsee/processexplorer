package de.tk.processmining.data.query.condition;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import de.tk.processmining.data.DatabaseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Seeliger on 27.09.2019.
 */
public class VariantCondition extends Condition {

    private Long variantId;

    private Long clusterIndex;

    public VariantCondition() {
    }

    public VariantCondition(Long variantId) {
        this.variantId = variantId;
    }

    @Override
    public List<com.healthmarketscience.sqlbuilder.Condition> getCondition(DatabaseModel db) {
        var conditions = new ArrayList<com.healthmarketscience.sqlbuilder.Condition>();

        if (variantId != null) {
            conditions.add(BinaryCondition.equalTo(db.variantsIdCol, variantId));
        }

        if (clusterIndex != null) {
            conditions.add(BinaryCondition.equalTo(db.variantsTable.addColumn("cluster_index"), clusterIndex));
        }

        return conditions;
    }

    public void setVariantId(long variantId) {
        this.variantId = variantId;
    }

    public void setClusterIndex(Long clusterIndex) {
        this.clusterIndex = clusterIndex;
    }
}
