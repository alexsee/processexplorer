package org.processexplorer.server.analysis.ml.simulation;

import org.processexplorer.server.analysis.query.QueryService;
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

    public Map<String, SensitivityResult> simulate(DrillDownQuery q) {
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
        var result = new HashMap<String, SensitivityResult>();

        for (var selection : selections) {
            if (!selection.isGroup())
                continue;

            result.put(selection.getName(), variateCondition(selection, query.getConditions().get(0), query, currentResult, idxs, currentKeysR));
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

    private SensitivityResult variateCondition(Selection selection, Condition condition, DrillDownQuery query, DrillDownResult currentResult, List<Integer> idxs, List<String> currentKeysR) {
        if (condition instanceof DurationCondition) {
            return variateDurationCondition(selection, (DurationCondition) condition, query, currentResult, idxs, currentKeysR);
        } else if (condition instanceof ReworkCondition) {
            return variateReworkCondition(selection, (ReworkCondition) condition, query, currentResult, idxs, currentKeysR);
        }

        return null;
    }

    private SensitivityResult variateDurationCondition(Selection selection, DurationCondition condition, DrillDownQuery query, DrillDownResult currentResult, List<Integer> idxs, List<String> currentKeysR) {
        var result = new ArrayList<SensitivityValue>();

        var beforeMinDuration = condition.getMinDuration();
        var beforeMaxDuration = condition.getMaxDuration();

        var diff = (condition.getMaxDuration() != null && condition.getMinDuration() != null) ?
                (condition.getMaxDuration() - condition.getMinDuration()) :
                (condition.getMaxDuration() == null ? condition.getMinDuration() : condition.getMaxDuration());
        var start = Math.max(1, condition.getMaxDuration() == null ? condition.getMinDuration() - diff : condition.getMaxDuration() - diff);
        var end = Math.max(1, condition.getMaxDuration() == null ? condition.getMinDuration() + diff : condition.getMaxDuration() + diff);

        for (long i = start; i < end; i++) {
            condition.setMinDuration(Math.max(0, i - diff));
            condition.setMaxDuration(i);

            // store values
            var value = new SensitivityValue();
            value.setDistance(evaluate(selection, query, currentResult, idxs, currentKeysR));
            value.setVariation("Duration - start: " + condition.getMinDuration() + "; end: " + condition.getMaxDuration());

            result.add(value);
        }

        condition.setMinDuration(beforeMinDuration);
        condition.setMaxDuration(beforeMaxDuration);

        return getResult(result);
    }

    private SensitivityResult variateReworkCondition(Selection selection, ReworkCondition condition, DrillDownQuery query, DrillDownResult currentResult, List<Integer> idxs, List<String> currentKeysR) {
        var result = new ArrayList<SensitivityValue>();

        var diff = (condition.getMax() - condition.getMin());
        var start = condition.getMin() - diff;
        var end = condition.getMax() + diff;

        for (int i = start; i < end; i++) {
            condition.setMin(Math.max(2, i - diff));
            condition.setMax(i);

            // store values
            var value = new SensitivityValue();
            value.setDistance(evaluate(selection, query, currentResult, idxs, currentKeysR));
            value.setVariation("Rework - start: " + condition.getMin() + "; end: " + condition.getMax());

            result.add(value);
        }

        return getResult(result);
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
