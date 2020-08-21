import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PredictionService } from '../prediction.service';
import { NzMessageService } from 'ng-zorro-antd';
import { TrainingConfiguration } from '../shared/training-configuration.model';

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

  constructor(private route: ActivatedRoute,
              private router: Router,
              private nzMessageService: NzMessageService,
              private predictionService: PredictionService) { }

  ngOnInit(): void {
    this.trainingConfiguration.logName = this.route.snapshot.paramMap.get('logName');
    this.trainingConfiguration.modelName = this.trainingConfiguration.logName + 
      ' Model (' + new Date().toLocaleDateString() + ' ' + new Date().toLocaleTimeString() + ')';
  }

  doTrain(): void {
    this.predictionService.train(this.trainingConfiguration).subscribe(x => {
      this.nzMessageService.success('Training for event log <b>' + this.trainingConfiguration.logName + '</b> started successfully.');
      this.router.navigate(['/models']);
    });
  }

}
