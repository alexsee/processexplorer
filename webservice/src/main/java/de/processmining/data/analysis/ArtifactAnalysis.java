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

package de.processmining.data.analysis;

import de.processmining.data.analysis.artifacts.ArtifactBase;
import de.processmining.data.analysis.artifacts.ArtifactResult;
import de.processmining.data.analysis.artifacts.ReworkArtifact;
import de.processmining.data.query.QueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Seeliger on 10.12.2019.
 */
@Service
public class ArtifactAnalysis {

    private Logger logger = LoggerFactory.getLogger(ArtifactAnalysis.class);

    private QueryService queryService;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public ArtifactAnalysis(QueryService queryService, JdbcTemplate jdbcTemplate) {
        this.queryService = queryService;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ArtifactResult> run(String logName) {
        var artifacts = new ArrayList<ArtifactBase>();
        var results = new ArrayList<ArtifactResult>();

        var rework = new ReworkArtifact(queryService, jdbcTemplate);
        rework.setReworkActivities(new String[]{"Change price", "Change Approval for Purchase Order", "Change Currency", "Change Delivery Indicator", "Change Final Invoice Indicator", "Change Price", "Change Quantity", "Change Storage Location"});
        rework.setMin(new int[]{2, 2, 2, 2, 2, 2, 2, 2});
        rework.setMax(new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE});

        artifacts.add(rework);

        for (var artifact : artifacts) {
            results.addAll(artifact.run(logName));
        }

        return results;
    }

}
