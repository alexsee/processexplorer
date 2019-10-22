package de.tk.processmining.data.analysis.itemsets;

import de.tk.processmining.data.analysis.clustering.FieldValue;
import org.springframework.stereotype.Service;
import smile.association.FPGrowth;
import smile.association.ItemSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alexander Seeliger on 22.10.2019.
 */
@Service
public class FrequentItemsetMiner {

    public List<ItemSet> getItemsets(List<Map<String, Object>> cases, Map<FieldValue, Integer> itemsetValues, double support) {
        var transactions = getTransactions(itemsetValues, cases);

        var growth = new FPGrowth(transactions, support);
        return growth.learn();
    }

    public List<ItemSet> getClosedItemsets(List<Map<String, Object>> cases, Map<FieldValue, Integer> itemsetValues, double support) {
        var transactions = getTransactions(itemsetValues, cases);

        var growth = new FPGrowth(transactions, support);
        var itemsets = growth.learn();
        itemsets = getClosedSets(itemsets);
        return itemsets;
    }

    public List<ItemSet> getClosedSets(List<ItemSet> itemSets) {
        var result = new ArrayList<ItemSet>();

        for (int i = 0; i < itemSets.size(); i++) {
            var itemset = itemSets.get(i);

            // check if we have an itemset containing the items with a higher support
            boolean isSuper = true;
            for (int j = 0; j < itemSets.size(); j++) {
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

    private int[][] getTransactions(Map<FieldValue, Integer> itemsetValues, List<Map<String, Object>> cases) {
        var transactions = new int[cases.size()][];

        int i = 0;
        for (var c : cases) {
            var transaction = new int[c.size()];

            int j = 0;
            for (var attr : c.entrySet()) {
                var value = new FieldValue(attr.getKey(), attr.getValue());

                Integer itemsetValue = itemsetValues.get(value);
                if (itemsetValue == null) {
                    itemsetValue = itemsetValues.size() + 1;
                    itemsetValues.put(value, itemsetValue);
                }

                transaction[j] = itemsetValue;
                j++;
            }

            transactions[i] = transaction;
            i++;
        }

        return transactions;
    }

}
