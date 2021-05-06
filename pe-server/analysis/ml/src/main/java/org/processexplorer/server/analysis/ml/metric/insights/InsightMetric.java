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

package org.processexplorer.server.analysis.ml.metric.insights;

import org.processexplorer.server.analysis.query.condition.Condition;
import org.processexplorer.server.analysis.query.model.Insight;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public interface InsightMetric {

    void setJdbcTemplate(JdbcTemplate jdbcTemplate);

    List<Insight> getInsights(List<Condition> conditions);

}
