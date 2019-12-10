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

import com.fasterxml.jackson.databind.ObjectMapper;
import de.processmining.data.analysis.artifacts.*;
import de.processmining.data.query.QueryService;
import de.processmining.webservice.database.EventLogArtifactRepository;
import jdk.jfr.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Seeliger on 10.12.2019.
 */
@Service
public class ArtifactAnalysis {

    private Logger logger = LoggerFactory.getLogger(ArtifactAnalysis.class);

    private EventLogArtifactRepository artifactRepository;

    private QueryService queryService;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public ArtifactAnalysis(QueryService queryService, JdbcTemplate jdbcTemplate, EventLogArtifactRepository eventLogArtifactRepository) {
        this.queryService = queryService;
        this.jdbcTemplate = jdbcTemplate;
        this.artifactRepository = eventLogArtifactRepository;
    }

    public List<ArtifactResult> run(String logName) {
        var artifacts = new ArrayList<ArtifactBase>();
        var results = new ArrayList<ArtifactResult>();

        // get all artifacts by log name
        var db_artifacts = artifactRepository.findByLogName(logName);

        for(var artifact : db_artifacts) {
            try {
                // instantiate artifact class
                var instance = (ArtifactBase)Class.forName(artifact.getType())
                        .getDeclaredConstructor(QueryService.class, JdbcTemplate.class)
                        .newInstance(queryService, jdbcTemplate);

                var configurationType = ((ParameterizedType)instance.getClass().getGenericSuperclass()).getActualTypeArguments()[0];

                var objectMapper = new ObjectMapper();
                var configuration = objectMapper
                        .readValue(artifact.getConfiguration(), (Class) configurationType);

                instance.setConfiguration((ArtifactConfiguration) configuration);

                artifacts.add(instance);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }

//        var rework = new ReworkArtifact(queryService, jdbcTemplate);
//        rework.setReworkActivities(new String[]{"Change price", "Change Approval for Purchase Order", "Change Currency", "Change Delivery Indicator", "Change Final Invoice Indicator", "Change Price", "Change Quantity", "Change Storage Location"});
//        rework.setMin(new int[]{2, 2, 2, 2, 2, 2, 2, 2});
//        rework.setMax(new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE});
//
//        artifacts.add(rework);

        for (var artifact : artifacts) {
            results.addAll(artifact.run(logName));
        }

        return results;
    }

}
