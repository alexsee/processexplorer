import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { PredictionService } from '../prediction.service';
import { NzMessageService } from 'ng-zorro-antd/message';
import { TrainingConfiguration } from '../shared/training-configuration.model';
import { LogService } from 'src/app/log/shared/log.service';

@Component({
  selector: 'app-train-model',
  templateUrl: './train-model.component.html',
  styleUrls: ['./train-model.component.scss']
})
export class TrainModelComponent implements OnInit {
  public trainingConfiguration: TrainingConfiguration = {
    logName: null,
    modelName: null,
    epochs: 10,
    batchSize: 500,
    whereCondition: ''
  };

  constructor(private logService: LogService,
              private router: Router,
              private nzMessageService: NzMessageService,
              private predictionService: PredictionService) { }

  ngOnInit(): void {
    // load open cases for current log
    this.logService.currentLog.subscribe(eventLog => {
      this.trainingConfiguration.logName = eventLog.logName;
      this.trainingConfiguration.modelName = this.trainingConfiguration.logName +
        ' Model (' + new Date().toLocaleDateString() + ' ' + new Date().toLocaleTimeString() + ')';
    });
  }

  doTrain(): void {
    this.predictionService.train(this.trainingConfiguration).subscribe(x => {
      this.nzMessageService.success('Training for event log <b>' + this.trainingConfiguration.logName + '</b> started successfully.');
      this.router.navigate(['/models']);
    });
  }

}
