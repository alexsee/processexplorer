package de.tk.processmining.data.analysis.clustering;

import de.tk.processmining.data.analysis.itemsets.FrequentItemsetMiner;
import de.tk.processmining.data.query.CasesQuery;
import de.tk.processmining.data.query.QueryService;
import de.tk.processmining.data.query.condition.ClusterCondition;
import de.tk.processmining.data.query.condition.Condition;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import smile.association.FPGrowth;
import smile.association.ItemSet;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Alexander Seeliger on 27.09.2019.
 */
@Service
public class MultiPerspectiveTraceClustering {

    private QueryService queryService;
    private FrequentItemsetMiner itemsetMiner;

    public MultiPerspectiveTraceClustering(FrequentItemsetMiner itemsetMiner, QueryService queryService) {
        this.itemsetMiner = itemsetMiner;
        this.queryService = queryService;
    }

    public void generateCaseAttributeDb(String logName) {
        var categoricalAttributes = queryService.getCategoricalCaseAttributes(logName);
        var variants = queryService.getAllPaths(logName, new ArrayList<>());

        var itemsetValues = new HashMap<FieldValue, Integer>();

        // get cases for each variant
        var itemsetMap = new HashMap<Integer, List<ItemSet>>();

        for (int i = 0; i < 32; i++) {
            var conditions = new ArrayList<Condition>();
            conditions.add(new ClusterCondition((long) i));

            var cases = queryService.getCases(new CasesQuery(logName, conditions, categoricalAttributes));
            var itemsets = itemsetMiner.getItemsets(cases, itemsetValues, 0.7);
            itemsetMap.put(i, itemsets);
        }

        var vals = new HashMap<Integer, FieldValue>();
        for (var keyValue : itemsetValues.entrySet()) {
            vals.put(keyValue.getValue(), keyValue.getKey());
        }

        // find most interesting ones
        var interesting = new HashMap<Integer, List<ItemSet>>();

        for (var key1 : itemsetMap.keySet()) {
            var itemsets = itemsetMap.get(key1);
            var res = new ArrayList<ItemSet>();

            for (var itemset : itemsets) {
                boolean contained = false;
                for (var key2 : itemsetMap.keySet()) {
                    if (key1.equals(key2)) {
                        continue;
                    }

                    for (var itemset2 : itemsetMap.get(key2)) {
                        if (Arrays.equals(itemset2.items, itemset.items)) {
                            contained = true;
                            break;
                        }
                    }
                }

                if (!contained) {
                    res.add(itemset);
                }
            }

            interesting.put(key1, res);
        }

        for (var key1 : interesting.keySet()) {
            var closed = itemsetMiner.getClosedSets(interesting.get(key1));
            if (closed.size() > 0) {
                System.out.println("Cluster: " + key1);
                closed.forEach(y -> System.out.println(y.support + " :: " + Arrays.stream(y.items).mapToObj(vals::get).collect(Collectors.toList())));
                System.out.println();
            }
        }

        // get overall frequent patterns
        {
            var cases = queryService.getCases(new CasesQuery(logName, new ArrayList<Condition>(), categoricalAttributes));
            var itemsets = itemsetMiner.getClosedItemsets(cases, itemsetValues, 0.7);

            System.out.println("All");
            itemsets.forEach(y -> System.out.println(y.support + " :: " + Arrays.stream(y.items).mapToObj(vals::get).collect(Collectors.toList())));
            System.out.println();
        }
    }
}
