package de.tk.processmining.data.analysis.metrics.insights;

import com.healthmarketscience.sqlbuilder.*;
import de.tk.processmining.data.analysis.categorization.AnalysisTargetCodes;
import de.tk.processmining.data.analysis.categorization.DomainCodes;
import de.tk.processmining.data.analysis.categorization.EventAttributeCodes;
import de.tk.processmining.data.analysis.categorization.VisualizationCodes;
import de.tk.processmining.data.model.Insight;
import de.tk.processmining.data.model.InsightValueFormat;
import de.tk.processmining.webservice.ApplicationContextProvider;
import de.tk.processmining.webservice.database.EventLogAnnotationRepository;
import de.tk.processmining.webservice.database.entities.EventLogAnnotation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Alexander Seeliger on 01.10.2019.
 */
public class CaseAttributeMetric extends ClusterMetric {

    private final String attributeName;

    public CaseAttributeMetric(String logName, String attributeName) {
        super(logName);
        this.attributeName = attributeName;
    }

    @Override
    protected Insight generateInsight(double effectSize, List<? extends Measure> labels, double[] with, double[] without) {
        var insight = new Insight();
        insight.setEffectSize(effectSize);

        insight.setLabels(labels.stream().map(Measure::getAttributeValue).collect(Collectors.toList()));
        insight.setWithin(with);
        insight.setWithout(without);

        insight.setFormat(InsightValueFormat.DISTRIBUTION);
        insight.setTitle("Case Attribute");
        insight.setSubTitle(attributeName);

        insight.setAnalysisTargetCodes(Arrays.asList(AnalysisTargetCodes.OUTLIERS, AnalysisTargetCodes.EXTREMES));
        insight.setDomainCodes(Arrays.asList(DomainCodes.CASE_PERSPECTIVE));
        insight.setVisualizationCodes(Arrays.asList(VisualizationCodes.TABLE, VisualizationCodes.BAR_CHART));
        insight.setEventAttributeCodes(getEventAttributeCodes());
        return insight;
    }

    private List<EventAttributeCodes> getEventAttributeCodes() {
        var context = ApplicationContextProvider.getApplicationContext();
        var eventLogAnnotationRepository = context.getBean(EventLogAnnotationRepository.class);

        var annotations = eventLogAnnotationRepository.findByLogNameAndColumnTypeAndColumnName(logName, "case_attribute", attributeName);
        return annotations.stream().map(EventLogAnnotation::getCode).map(EventAttributeCodes::valueOf).collect(Collectors.toList());
    }

    @Override
    protected Map<ClusterMetric.Measure, Double> computeDifference(Object calculation, Condition conditions) {
        var inner_sql = new SelectQuery()
                .addAliasedColumn(FunctionCall.countAll(), "occurrence")
                .addAliasedColumn(calculation, "attr")
                .addFromTable(db.caseTable)
                .addJoins(SelectQuery.JoinType.INNER, db.caseVariantJoin, db.caseCaseAttributeJoin)
                .addCondition(conditions)
                .addCustomGroupings(calculation);

        var result = jdbcTemplate.queryForList(inner_sql.validate().toString());
        var measures = new HashMap<ClusterMetric.Measure, Double>();

        for (var item : result) {
            var value = item.get("attr");

            var measure = new ClusterMetric.Measure(attributeName, value == null ? "" : value.toString());
            measures.put(measure, Double.parseDouble(item.get("occurrence").toString()));
        }

        return measures;
    }

    @Override
    protected Object getExpression() {
        return db.caseAttributeTable.addColumn("\"" + attributeName + "\"");
    }

}
