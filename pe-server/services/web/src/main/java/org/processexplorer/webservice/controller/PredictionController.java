/*
 * ProcessExplorer
 * Copyright (C) 2020  Alexander Seeliger
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

import org.processexplorer.data.action.AutomationAction;
import org.processexplorer.data.prediction.OpenCaseResult;
import org.processexplorer.data.prediction.PredictionConfiguration;
import org.processexplorer.data.prediction.TrainingConfiguration;
import org.processexplorer.server.common.persistence.entity.EventLogAutomationJob;
import org.processexplorer.server.common.persistence.entity.EventLogModel;
import org.processexplorer.webservice.services.AutomationService;
import org.processexplorer.webservice.services.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Alexander Seeliger on 10.08.2020.
 */
@RestController()
@RequestMapping("/prediction")
public class PredictionController {

    private final PredictionService predictionService;

    private final AutomationService automationService;

    @Autowired
    public PredictionController(PredictionService predictionService, AutomationService automationService) {
        this.predictionService = predictionService;
        this.automationService = automationService;
    }

    @GetMapping("/models")
    public ResponseEntity<Iterable<EventLogModel>> getByEventLog(@RequestParam(name = "logName", required = false) String logName) {
        if (logName == null) {
            return ResponseEntity.ok(predictionService.getAll());
        } else {
            return ResponseEntity.ok(predictionService.getModelsByEventLog(logName));
        }
    }

    @PostMapping("/default")
    public ResponseEntity<EventLogModel> setDefault(@RequestParam("modelId") long modelId) {
        return ResponseEntity.ok(predictionService.setDefault(modelId));
    }

    @PostMapping("/train")
    public ResponseEntity<Void> trainModel(@RequestBody TrainingConfiguration configuration) {
        predictionService.train(configuration);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping()
    public ResponseEntity<Void> delete(@RequestParam("id") long id) {
        predictionService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/init_case_management")
    public ResponseEntity<Void> initCaseManagement(@RequestParam(name = "logName") String logName) {
        predictionService.initCaseManagement(logName);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/predict/open_cases")
    public ResponseEntity<Void> predict(@RequestParam(name = "logName") String logName) {
        var configuration = new PredictionConfiguration();
        configuration.setLogName(logName);
        configuration.setWhereCondition("ca.state = 1");

        predictionService.predict(configuration);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/open_cases")
    public ResponseEntity<List<OpenCaseResult>> openCases(@RequestParam(name = "logName") String logName) {
        return ResponseEntity.ok(predictionService.getOpenCases(logName));
    }

    @GetMapping("/automation_actions")
    public ResponseEntity<List<AutomationAction>> automationActions(@RequestParam("logName") String logName,
                                                                    @RequestParam("caseId") long caseId) {
        return ResponseEntity.ok(predictionService.getAutomationActions(logName, caseId));
    }

    @GetMapping("/automation_trigger")
    public ResponseEntity<EventLogAutomationJob> automationTrigger(@RequestParam("logName") String logName,
                                                                   @RequestParam("caseId") long caseId,
                                                                   @RequestParam("actionId") long actionId) {
        return ResponseEntity.ok(automationService.triggerJob(logName, caseId, actionId));
    }

    @GetMapping("/automation_jobs")
    public ResponseEntity<List<EventLogAutomationJob>> automationJobs(@RequestParam("logName") String logName,
                                                                      @RequestParam("caseId") long caseId) {
        return ResponseEntity.ok(automationService.getAutomationJobs(logName, caseId));
    }

    @GetMapping("/automation_jobs_all")
    public ResponseEntity<List<EventLogAutomationJob>> automationJobs(@RequestParam("logName") String logName) {
        return ResponseEntity.ok(automationService.getAutomationJobs(logName));
    }

}
