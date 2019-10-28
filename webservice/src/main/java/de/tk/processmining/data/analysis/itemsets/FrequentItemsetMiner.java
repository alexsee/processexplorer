package de.tk.processmining.data.analysis.itemsets;

import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemset;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemsets;
import de.tk.processmining.data.analysis.clustering.FieldValue;
import de.tk.processmining.data.analysis.itemsets.spmf.frequentpatterns.AlgoFPClose;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Alexander Seeliger on 22.10.2019.
 */
@Service
public class FrequentItemsetMiner {

    public Set<FrequentItemset> getClosedItemsets(List<Map<String, Object>> cases, Map<FieldValue, Integer> itemsetValues, double support) {
        var transactions = getTransactions(itemsetValues, cases);
        return fpClose(transactions, support);
    }

    public Map<Integer, FieldValue> getReversed(Map<FieldValue, Integer> itemsetValues) {
        var vals = new HashMap<Integer, FieldValue>();
        for (var keyValue : itemsetValues.entrySet()) {
            vals.put(keyValue.getValue(), keyValue.getKey());
        }
        return vals;
    }

    private List<List<Integer>> getTransactions(Map<FieldValue, Integer> itemsetValues, List<Map<String, Object>> cases) {
        var transactions = new ArrayList<List<Integer>>();

        for (var c : cases) {
            var transaction = new ArrayList<Integer>();

            for (var attr : c.entrySet()) {
                if (attr.getKey().equals("variant_id"))
                    continue;

                var value = new FieldValue(attr.getKey(), attr.getValue());

                Integer itemsetValue = itemsetValues.get(value);
                if (itemsetValue == null) {
                    itemsetValue = itemsetValues.size() + 1;
                    itemsetValues.put(value, itemsetValue);
                }

                transaction.add(itemsetValue);
            }

            transactions.add(transaction);
        }

        return transactions;
    }

    /**
     * Extracts all closed frequent item sets from the given database file.
     *
     * @param minSupport
     * @return
     */
    public static Set<FrequentItemset> fpClose(List<List<Integer>> transactions, double minSupport) {
        Set<FrequentItemset> result = new HashSet<>();
        AlgoFPClose fpClose = new AlgoFPClose();

        Itemsets itemsets = fpClose.runAlgorithm(transactions, minSupport);

        // convert to simple format
        for (var level : itemsets.getLevels()) {
            for (Itemset itemset : level) {
                FrequentItemset items = new FrequentItemset();
                items.setSupport(itemset.getAbsoluteSupport());

                Arrays.stream(itemset.getItems()).forEach(items::add);

                result.add(items);
            }
        }

        return result;
    }

}
