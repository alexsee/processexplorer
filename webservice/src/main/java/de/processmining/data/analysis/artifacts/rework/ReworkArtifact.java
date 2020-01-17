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

package de.processmining.data.analysis.artifacts.rework;

import de.processmining.data.analysis.artifacts.ArtifactBase;
import de.processmining.data.analysis.artifacts.ArtifactDescription;
import de.processmining.data.analysis.artifacts.ArtifactResult;
import de.processmining.data.query.QueryService;
import de.processmining.data.query.condition.Condition;
import de.processmining.data.query.condition.ReworkCondition;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Seeliger on 10.12.2019.
 */
@ArtifactDescription(
        name = "Rework activities",
        description = "Searches for cases that contain rework activities."
)
public class ReworkArtifact extends ArtifactBase<ReworkArtifactConfiguration> {

    public ReworkArtifact(QueryService queryService,
                          JdbcTemplate jdbcTemplate) {
        super(queryService, jdbcTemplate);
    }

    public List<ArtifactResult> run(String logName) {
        var results = new ArrayList<ArtifactResult>();

        for (var rework : configuration.getReworkActivities()) {
            var conditions = new ArrayList<Condition>();
            conditions.add(new ReworkCondition(rework.getActivity(), rework.getMin(), rework.getMax()));

            var log = queryService.getLogStatistics(logName, conditions);
            if (log.getNumTraces() > 0) {
                var result = new ArtifactResult();
                result.setName("Rework activity: " + rework.getActivity());
                result.setType(ReworkArtifact.class.getCanonicalName());
                result.setNumAffectedCases(log.getNumTraces());
                result.setConditions(conditions);

                results.add(result);
            }
        }

        return results;
    }

    public void setConfiguration(ReworkArtifactConfiguration configuration) {
        this.configuration = configuration;
    }
}
