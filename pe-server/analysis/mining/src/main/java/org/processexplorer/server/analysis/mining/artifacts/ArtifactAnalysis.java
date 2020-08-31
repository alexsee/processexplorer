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

package org.processexplorer.server.analysis.mining.artifacts;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.processexplorer.server.analysis.query.QueryService;
import org.processexplorer.server.common.persistence.repository.EventLogArtifactRepository;
import org.processexplorer.server.common.persistence.entity.EventLogArtifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
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

    /**
     * Executes the artifacts that correspond to the event log.
     *
     * @param logName
     * @return
     */
    public List<ArtifactResult> run(String logName) {
        var artifacts = new ArrayList<ArtifactBase<?>>();
        var results = new ArrayList<ArtifactResult>();

        // get all artifacts by log name
        var db_artifacts = artifactRepository.findByLogName(logName);

        for (var artifact : db_artifacts) {
            if (!artifact.isActivated())
                continue;

            try {
                // instantiate artifact class
                var instance = (ArtifactBase) Class.forName(artifact.getType())
                        .getDeclaredConstructor(QueryService.class, JdbcTemplate.class)
                        .newInstance(queryService, jdbcTemplate);

                var configurationType = ((ParameterizedType) instance.getClass().getGenericSuperclass()).getActualTypeArguments()[0];

                // read configuration object from database
                var objectMapper = new ObjectMapper();
                var configuration = objectMapper
                        .readValue(artifact.getConfiguration(), (Class) configurationType);

                instance.setConfiguration((ArtifactConfiguration) configuration);

                artifacts.add(instance);
            } catch (Exception ex) {
                logger.error("Artifact could not be initialized: " + artifact.getType(), ex);
            }
        }

        for (var artifact : artifacts) {
            results.addAll(artifact.run(logName));
        }

        return results;
    }

    /**
     * Returns all artifact configurations for a given log name.
     *
     * @param logName
     * @return
     */
    public List<ArtifactUIConfiguration> getArtifactConfigurations(String logName) {
        var results = new ArrayList<ArtifactUIConfiguration>();

        // get all artifacts by log name
        var db_artifacts = artifactRepository.findByLogName(logName);

        for (var artifact : db_artifacts) {
            var config = new ArtifactUIConfiguration();
            config.setId(artifact.getId());
            config.setType(artifact.getType());
            config.setConfiguration(artifact.getConfiguration());
            config.setActivated(artifact.isActivated());

            results.add(config);
        }

        return results;
    }

    /**
     * Returns a list of all available artifact classes.
     *
     * @return
     */
    public List<ArtifactUI> getArtifactClasses() {
        var result = new ArrayList<ArtifactUI>();

        var scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(ArtifactDescription.class));

        for (var artifact : scanner.findCandidateComponents("org.processexplorer.server.analysis.mining.artifacts")) {
            var ui = new ArtifactUI();
            ui.setType(artifact.getBeanClassName());

            try {
                var type = Class.forName(artifact.getBeanClassName()).getAnnotationsByType(ArtifactDescription.class);
                ui.setName(type[0].name());
                ui.setDescription(type[0].description());
            } catch (Exception ex) {
                // should not happen
            }

            result.add(ui);
        }

        return result;
    }

    /**
     * Returns the user interface components required for the given artifact.
     *
     * @param artifactClass
     * @return
     */
    public List<ArtifactUIField> getConfigurationDescription(Class<? extends ArtifactBase> artifactClass) {
        var result = new ArrayList<ArtifactUIField>();

        try {
            Class configurationClass = (Class) ((ParameterizedType) artifactClass.getGenericSuperclass()).getActualTypeArguments()[0];

            var fields = configurationClass.getDeclaredFields();

            for (var field : fields) {
                var fieldUi = getFieldInfo(field);
                if (fieldUi != null) {
                    result.add(fieldUi);
                }
            }
        } catch (Exception ex) {
            logger.error("Could not find configuration annotations for given class: " + artifactClass.getName(), ex);
        }

        return result;
    }

    /**
     * Returns a ArtifactUIField for a given field that is annotated with ArtifactFieldDescription.
     *
     * @param field
     * @return
     */
    private ArtifactUIField getFieldInfo(Field field) {
        var result = new ArtifactUIField();
        var annotations = field.getAnnotationsByType(ArtifactFieldDescription.class);

        if (annotations.length == 0) {
            return null;
        }

        result.setFieldName(field.getName());
        result.setName(annotations[0].name());
        result.setDescription(annotations[0].description());
        result.setType(annotations[0].type().name());

        if (annotations[0].type() == ArtifactFieldType.MULTI_COLUMN) {
            var fields = field.getType().getComponentType().getDeclaredFields();
            var subFields = new ArrayList<ArtifactUIField>();

            for (var subField : fields) {
                subFields.add(getFieldInfo(subField));
            }

            result.setChilds(subFields);
        }

        return result;
    }

    /**
     * Stores an artifact configuration to the database.
     *
     * @param logName
     * @param configuration
     * @return
     */
    public EventLogArtifact save(String logName, ArtifactUIConfiguration configuration) {
        var config = artifactRepository.findById(configuration.getId());
        var entity = config.orElse(new EventLogArtifact());

        entity.setLogName(logName);
        entity.setType(configuration.getType());
        entity.setActivated(configuration.isActivated());
        entity.setConfiguration(configuration.getConfiguration());

        return artifactRepository.save(entity);
    }

    /**
     * Removes an artifact configuration from the database.
     *
     * @param id
     */
    public void delete(Long id) {
        artifactRepository.deleteById(id);
    }
}
