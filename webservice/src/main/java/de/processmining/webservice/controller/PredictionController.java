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

package de.processmining.webservice.controller;

import de.processmining.data.prediction.TrainingConfiguration;
import de.processmining.webservice.database.entities.EventLogModel;
import de.processmining.webservice.services.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Alexander Seeliger on 10.08.2020.
 */
@RestController()
@RequestMapping("/prediction")
public class PredictionController {

    private PredictionService predictionService;

    @Autowired
    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @RequestMapping("/models")
    public ResponseEntity<Iterable<EventLogModel>> getByEventLog(@RequestParam(name = "logName", required = false) String logName) {
        if (logName == null) {
            return ResponseEntity.ok(predictionService.getAll());
        } else {
            return ResponseEntity.ok(predictionService.getModelsByEventLog(logName));
        }
    }

    @RequestMapping("/train")
    public ResponseEntity trainModel(@RequestBody TrainingConfiguration configuration) {
        predictionService.train(configuration);
        return ResponseEntity.ok().build();
    }

}
