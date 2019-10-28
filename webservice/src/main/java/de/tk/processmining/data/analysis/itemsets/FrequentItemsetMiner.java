package de.tk.processmining.data.analysis.itemsets;

import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemset;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemsets;
import de.tk.processmining.data.analysis.clustering.FieldValue;
import de.tk.processmining.data.analysis.itemsets.spmf.frequentpatterns.AlgoFPClose;
import org.springframework.stereotype.Service;
import smile.association.FPGrowth;
import smile.association.ItemSet;

import java.util.*;

/**
 * @author Alexander Seeliger on 22.10.2019.
 */
@Service
public class FrequentItemsetMiner {

    //    public List<ItemSet> getItemsets(List<Map<String, Object>> cases, Map<FieldValue, Integer> itemsetValues, double support) {
    //        var transactions = getTransactions(itemsetValues, cases);
    //
    //        var growth = new FPGrowth(transactions, support);
    //        return growth.learn();
    //    }

    public Set<FrequentItemset> getClosedItemsets(List<Map<String, Object>> cases, Map<FieldValue, Integer> itemsetValues, double support) {
        var transactions = getTransactions(itemsetValues, cases);

        var growth = fpClose(transactions, support);
        return growth;
    }

    public List<ItemSet> getClosedSets(List<ItemSet> itemSets) {
        var result = new ArrayList<ItemSet>();

        for (int i = 0; i < itemSets.size(); i++) {
            var itemset = itemSets.get(i);

            // check if we have an itemset containing the items with a higher support
            boolean isSuper = true;
            for (int j = i; j < itemSets.size(); j++) {
                if (i == j)
                    continue;

                var other = itemSets.get(j);
                if (contains(itemset.items, other.items) && other.support >= itemset.support) {
                    isSuper = false;
                    break;
                }
            }

            if (isSuper) {
                result.add(itemset);
            }
        }

        return result;
    }

    public Map<Integer, FieldValue> getReversed(Map<FieldValue, Integer> itemsetValues) {
        var vals = new HashMap<Integer, FieldValue>();
        for (var keyValue : itemsetValues.entrySet()) {
            vals.put(keyValue.getValue(), keyValue.getKey());
        }
        return vals;
    }

    private boolean contains(int[] list1, int[] list2) {
        for (int i = 0; i < list1.length; i++) {
            boolean contains = false;
            for (int j = 0; j < list2.length; j++) {
                if (list1[i] == list2[j]) {
                    contains = true;
                    break;
                }
            }

            if (!contains)
                return false;
        }

        return true;
    }

    private List<List<Integer>> getTransactions(Map<FieldValue, Integer> itemsetValues, List<Map<String, Object>> cases) {
        var transactions = new ArrayList<List<Integer>>();

        for (var c : cases) {
            var transaction = new ArrayList<Integer>();

            for (var attr : c.entrySet()) {
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
