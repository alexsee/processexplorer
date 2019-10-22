package de.tk.processmining.data.model;

import de.tk.processmining.data.query.condition.Condition;

import java.util.List;

/**
 * @author Alexander Seeliger on 22.10.2019.
 */
public class Recommendation {

    private Double score;

    private List<Condition> conditions;

    public Recommendation() { }

    public Recommendation(Double score, List<Condition> conditions) {
        this.score = score;
        this.conditions = conditions;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }
}
