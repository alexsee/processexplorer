package org.processexplorer.server.analysis.ml.simulation;

import org.processexplorer.server.analysis.query.QueryService;
import org.processexplorer.server.analysis.query.condition.AttributeCondition;
import org.processexplorer.server.analysis.query.condition.Condition;
import org.processexplorer.server.analysis.query.condition.DurationCondition;
import org.processexplorer.server.analysis.query.condition.ReworkCondition;
import org.processexplorer.server.analysis.query.request.DrillDownQuery;
import org.processexplorer.server.analysis.query.result.DrillDownResult;
import org.processexplorer.server.analysis.query.selection.Selection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.processexplorer.server.analysis.ml.metric.StatisticMetrics.norm;
import static smile.math.MathEx.JensenShannonDivergence;

/**
 * @author Alexander Seeliger on 14.10.2020.
 */
@Service
public class SensitivityAnalysis {

    private final JdbcTemplate jdbcTemplate;
    private QueryService queryService;

    public SensitivityAnalysis(JdbcTemplate jdbcTemplate, QueryService queryService) {
        this.jdbcTemplate = jdbcTemplate;
        this.queryService = queryService;
    }

    public Map<String, List<SensitivityValue>> simulate(DrillDownQuery q) {
        var selections = new ArrayList<Selection>();
        selections.addAll(q.getSelections());

        var conditions = new ArrayList<Condition>();
        conditions.addAll(q.getConditions());

        var query = new DrillDownQuery(q.getLogName(), selections);
        query.setConditions(conditions);

        // obtain groups
        var idxs = new ArrayList<Integer>();
        for (var selection : selections) {
            if (!selection.isGroup()) {
                idxs.add(selections.indexOf(selection));
            }
        }

        var currentResult = queryService.getDrillDown(query);
        var currentKeysR = getStringArray(currentResult.getData(), idxs);

        // sensitivity
        var result = new HashMap<String, List<SensitivityValue>>();

        for (var selection : selections) {
            if (!selection.isGroup())
                continue;

            result.putAll(variateCondition(selection, query.getConditions().get(0), query, currentResult, idxs, currentKeysR));
        }

        return result;
    }

    private double evaluate(Selection selection, DrillDownQuery query, DrillDownResult currentResult, List<Integer> idxs, List<String> currentKeysR) {
        var result = queryService.getDrillDown(query);

        var keys = new TreeSet<>(currentKeysR);
        keys.addAll(getStringArray(result.getData(), idxs));

        var currentDoubleR = getDoubleArray(currentResult.getData(), keys, idxs, query.getSelections().indexOf(selection));
        var doubleR = getDoubleArray(result.getData(), keys, idxs, query.getSelections().indexOf(selection));

        return JensenShannonDivergence(norm(currentDoubleR), norm(doubleR));
    }

    private Map<String, List<SensitivityValue>> variateCondition(Selection selection, Condition condition, DrillDownQuery query, DrillDownResult currentResult, List<Integer> idxs, List<String> currentKeysR) {
        var result = new HashMap<String, List<SensitivityValue>>();

        if (condition instanceof DurationCondition) {
            var cond = (DurationCondition) condition;

            if (cond.getMinDuration() != null)
                result.put(selection.getName() + " (start)", variateDurationMinCondition(selection, cond, query, currentResult, idxs, currentKeysR));
            if (cond.getMaxDuration() != null)
                result.put(selection.getName() + " (end)", variateDurationMaxCondition(selection, cond, query, currentResult, idxs, currentKeysR));
        } else if (condition instanceof ReworkCondition) {
            result.put(selection.getName(), variateReworkMinCondition(selection, (ReworkCondition) condition, query, currentResult, idxs, currentKeysR));
            result.put(selection.getName(), variateReworkMaxCondition(selection, (ReworkCondition) condition, query, currentResult, idxs, currentKeysR));
        } else if (condition instanceof AttributeCondition) {
            var cond = (AttributeCondition) condition;

            if (cond.getBinaryType().equals(AttributeCondition.BinaryType.INTERVAL_RANGE)) {
                result.put(selection.getName() + " (from)", variateAttributeFromCondition(selection, cond, query, currentResult, idxs, currentKeysR));
                result.put(selection.getName() + " (to)", variateAttributeToCondition(selection, cond, query, currentResult, idxs, currentKeysR));
            }
        }

        return result;
    }

    private List<SensitivityValue> variateAttributeFromCondition(Selection selection, AttributeCondition condition, DrillDownQuery query, DrillDownResult currentResult, List<Integer> idxs, List<String> currentKeysR) {
        var result = new ArrayList<SensitivityValue>();
        var beforeFrom = condition.getFrom();

        // vary start
        var start = Math.max(0, condition.getFrom() - 10);
        for (long i = start; i < Math.min(start + 20, (condition.getTo() == null ? Integer.MAX_VALUE : condition.getTo())); i++) {
            condition.setFrom(i);

            // store values
            var value = new SensitivityValue();
            value.setDistance(evaluate(selection, query, currentResult, idxs, currentKeysR));
            value.setVariation("Attribute - start: " + condition.getFrom() + "; end: " + condition.getTo());

            result.add(value);
        }

        condition.setFrom(beforeFrom);
        return result;
    }

    private List<SensitivityValue> variateAttributeToCondition(Selection selection, AttributeCondition condition, DrillDownQuery query, DrillDownResult currentResult, List<Integer> idxs, List<String> currentKeysR) {
        var result = new ArrayList<SensitivityValue>();
        var beforeMaxDuration = condition.getTo();

        // vary end
        var end = Math.max(1, condition.getTo() - 10);
        for (long i = Math.max(condition.getFrom() == null ? 1 : condition.getFrom(), end); i < end + 20; i++) {
            condition.setTo(i);

            // store values
            var value = new SensitivityValue();
            value.setDistance(evaluate(selection, query, currentResult, idxs, currentKeysR));
            value.setVariation("Attribute - start: " + condition.getFrom() + "; end: " + condition.getTo());

            result.add(value);
        }

        condition.setTo(beforeMaxDuration);

        return result;
    }

    private List<SensitivityValue> variateDurationMinCondition(Selection selection, DurationCondition condition, DrillDownQuery query, DrillDownResult currentResult, List<Integer> idxs, List<String> currentKeysR) {
        var result = new ArrayList<SensitivityValue>();
        var beforeMinDuration = condition.getMinDuration();

        // vary start
        var start = Math.max(0, condition.getMinDuration() - 5);
        for (long i = start; i < Math.min(start + 10, (condition.getMaxDuration() == null ? Integer.MAX_VALUE : condition.getMaxDuration())); i++) {
            condition.setMinDuration(i);

            // store values
            var value = new SensitivityValue();
            value.setDistance(evaluate(selection, query, currentResult, idxs, currentKeysR));
            value.setVariation("Duration - start: " + condition.getMinDuration() + "; end: " + condition.getMaxDuration());

            result.add(value);
        }

        condition.setMinDuration(beforeMinDuration);
        return result;
    }

    private List<SensitivityValue> variateDurationMaxCondition(Selection selection, DurationCondition condition, DrillDownQuery query, DrillDownResult currentResult, List<Integer> idxs, List<String> currentKeysR) {
        var result = new ArrayList<SensitivityValue>();
        var beforeMaxDuration = condition.getMaxDuration();

        // vary end
        var end = Math.max(1, condition.getMaxDuration() - 5);
        for (long i = Math.max(condition.getMinDuration() == null ? 1 : condition.getMinDuration(), end); i < end + 10; i++) {
            condition.setMaxDuration(i);

            // store values
            var value = new SensitivityValue();
            value.setDistance(evaluate(selection, query, currentResult, idxs, currentKeysR));
            value.setVariation("Duration - start: " + condition.getMinDuration() + "; end: " + condition.getMaxDuration());

            result.add(value);
        }

        condition.setMaxDuration(beforeMaxDuration);

        return result;
    }

    private List<SensitivityValue> variateReworkMinCondition(Selection selection, ReworkCondition condition, DrillDownQuery query, DrillDownResult currentResult, List<Integer> idxs, List<String> currentKeysR) {
        var result = new ArrayList<SensitivityValue>();
        var beforeMinDuration = condition.getMin();

        // vary min
        var start = Math.max(0, condition.getMin() - 5);
        for (int i = start; i < Math.min(start + 10, condition.getMax()); i++) {
            condition.setMin(i);

            // store values
            var value = new SensitivityValue();
            value.setDistance(evaluate(selection, query, currentResult, idxs, currentKeysR));
            value.setVariation("Rework - start: " + condition.getMin() + "; end: " + condition.getMax());

            result.add(value);
        }

        condition.setMin(beforeMinDuration);
        return result;
    }

    private List<SensitivityValue> variateReworkMaxCondition(Selection selection, ReworkCondition condition, DrillDownQuery query, DrillDownResult currentResult, List<Integer> idxs, List<String> currentKeysR) {
        var result = new ArrayList<SensitivityValue>();
        var beforeMaxDuration = condition.getMax();

        // vary end
        var end = Math.max(1, condition.getMax() - 5);
        for (int i = Math.max(1, end); i < end + 10; i++) {
            condition.setMax(i);

            // store values
            var value = new SensitivityValue();
            value.setDistance(evaluate(selection, query, currentResult, idxs, currentKeysR));
            value.setVariation("Rework - start: " + condition.getMin() + "; end: " + condition.getMax());

            result.add(value);
        }

        condition.setMax(beforeMaxDuration);
        return result;
    }

    private SensitivityResult getResult(List<SensitivityValue> values) {
        var result = new SensitivityResult();
        for (var sensitivity : values) {
            if (sensitivity.getDistance() < .1 && result.getStart() == null) {
                result.setStart(sensitivity);
            }

            if (sensitivity.getDistance() < .1) {
                result.setEnd(sensitivity);
            }
        }
        return result;
    }

    private double[] getDoubleArray(List<Object> obj, SortedSet<String> keys, List<Integer> idxs, int index) {
        var result = new double[keys.size()];

        for (int i = 0; i < obj.size(); i++) {
            var array = (Object[]) obj.get(i);
            var item = Double.parseDouble(array[index].toString());

            var value = new StringBuilder();
            for (var idx : idxs) {
                value.append(array[idx].toString());
            }

            result[keys.headSet(value.toString()).size()] = item;
        }

        return result;
    }

    private List<String> getStringArray(List<Object> obj, List<Integer> idxs) {
        var result = new ArrayList<String>();

        for (int i = 0; i < obj.size(); i++) {
            var array = (Object[]) obj.get(i);

            var item = new StringBuilder();
            for (var idx : idxs) {
                item.append(array[idx].toString());
            }
            result.add(item.toString());
        }

        return result;
    }

}
