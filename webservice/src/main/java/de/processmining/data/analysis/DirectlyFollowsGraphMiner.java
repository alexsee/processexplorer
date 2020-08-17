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

import de.processmining.data.DatabaseModel;
import de.processmining.utils.OutputBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Alexander Seeliger on 23.09.2019.
 */
public class DirectlyFollowsGraphMiner {

    private static Logger logger = LoggerFactory.getLogger(DirectlyFollowsGraphMiner.class);

    private JdbcTemplate jdbcTemplate;

    public DirectlyFollowsGraphMiner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void mine(String logName) {
        logger.info("Begin generating directly-follows graph for \"{}\"", logName);

        var db = new DatabaseModel(logName);


        logger.info("Finished generating directly-follows graph for \"{}\"", logName);
    }


}
