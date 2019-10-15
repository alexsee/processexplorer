package de.tk.processmining.data.analysis.clustering;

import de.tk.processmining.data.query.CasesQuery;
import de.tk.processmining.data.query.QueryService;
import de.tk.processmining.data.query.condition.Condition;
import de.tk.processmining.data.query.condition.VariantCondition;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Alexander Seeliger on 27.09.2019.
 */
public class MultiPerspectiveTraceClustering {

    private QueryService queryService;
    private JdbcTemplate jdbcTemplate;
    private String logName;

    public MultiPerspectiveTraceClustering(JdbcTemplate jdbcTemplate, String logName) {
        this.jdbcTemplate = jdbcTemplate;
        this.logName = logName;

        this.queryService = new QueryService(jdbcTemplate, null);
    }

    public void generateCaseAttributeDb() {
        var categoricalAttributes = queryService.getCategoricalCaseAttributes(logName);
        var variants = queryService.getAllPaths(logName, new ArrayList<>());

        var itemsetValues = new HashMap<FieldValue, Integer>();

        // get cases for each variant
        for (var variant : variants) {
            var conditions = new ArrayList<Condition>();
            conditions.add(new VariantCondition(variant.getId()));

            var cases = queryService.getCases(new CasesQuery(logName, conditions, categoricalAttributes));
            var transactions = new ArrayList<>();

            for (var c : cases) {
                var transaction = new ArrayList<>();

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

        }
    }

}
