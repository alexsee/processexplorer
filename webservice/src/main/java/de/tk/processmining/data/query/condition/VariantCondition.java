package de.tk.processmining.data.query.condition;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import de.tk.processmining.data.DatabaseModel;

/**
 * @author Alexander Seeliger on 27.09.2019.
 */
public class VariantCondition extends Condition {

    private Long variantId;

    public VariantCondition() {
    }

    public VariantCondition(Long variantId) {
        this.variantId = variantId;
    }

    @Override
    public com.healthmarketscience.sqlbuilder.Condition getCondition(DatabaseModel db) {
        if (variantId != null) {
            return BinaryCondition.equalTo(db.variantsIdCol, variantId);
        }
        return null;
    }

    public void setVariantId(long variantId) {
        this.variantId = variantId;
    }
}
