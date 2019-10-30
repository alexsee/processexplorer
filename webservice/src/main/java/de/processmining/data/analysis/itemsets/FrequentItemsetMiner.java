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

package de.processmining.data.analysis.itemsets;

import de.processmining.data.analysis.itemsets.spmf.patterns.itemset_array_integers_with_count.Itemset;
import de.processmining.data.analysis.itemsets.spmf.patterns.itemset_array_integers_with_count.Itemsets;
import de.processmining.data.model.FieldValue;
import de.processmining.data.analysis.itemsets.spmf.frequentpatterns.AlgoFPClose;
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
