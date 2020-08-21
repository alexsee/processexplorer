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

package org.processexplorer.webservice.controller;

import org.processexplorer.data.analysis.artifacts.*;
import org.processexplorer.server.common.persistence.entity.EventLogArtifact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Alexander Seeliger on 11.12.2019.
 */
@RestController
@RequestMapping("/artifacts")
public class ArtifactController {

    private ArtifactAnalysis artifactAnalysis;

    @Autowired
    public ArtifactController(ArtifactAnalysis artifactAnalysis) {
        this.artifactAnalysis = artifactAnalysis;
    }

    @GetMapping("/evaluate")
    public ResponseEntity<List<ArtifactResult>> evaluate(@RequestParam("logName") String logName) {
        return ResponseEntity.ok(artifactAnalysis.run(logName));
    }

    @GetMapping()
    public ResponseEntity<List<ArtifactUI>> list() {
        var result = artifactAnalysis.getArtifactClasses();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/configuration")
    public ResponseEntity<List<ArtifactUIConfiguration>> list(@RequestParam("logName") String logName) {
        var result = artifactAnalysis.getArtifactConfigurations(logName);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/configuration")
    public ResponseEntity<EventLogArtifact> save(@RequestParam("logName") String logName, @RequestBody ArtifactUIConfiguration configuration) {
        return ResponseEntity.ok(artifactAnalysis.save(logName, configuration));
    }

    @DeleteMapping("/configuration")
    public ResponseEntity delete(@RequestParam("id") Long id) {
        artifactAnalysis.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/ui")
    public ResponseEntity<List<ArtifactUIField>> artifacts(@RequestParam("artifact") String artifact) {
        try {
            var uiFields = artifactAnalysis.getConfigurationDescription((Class<? extends ArtifactBase>) Class.forName(artifact));

            return ResponseEntity.ok(uiFields);
        } catch (Exception ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
