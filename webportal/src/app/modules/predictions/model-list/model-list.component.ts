import { Component, OnInit, OnDestroy } from '@angular/core';
import { EventLogModel } from 'src/app/log/models/eventlog-model.model';
import { PredictionService } from '../prediction.service';
import { ActivatedRoute } from '@angular/router';
import { RxStompService } from '@stomp/ng2-stompjs';
import { Subscription } from 'rxjs';
import { Message } from 'stompjs';
import { NzMessageService } from 'ng-zorro-antd';
import { LogService } from 'src/app/log/shared/log.service';

@Component({
  selector: 'app-prediction-model-list',
  templateUrl: './model-list.component.html',
  styleUrls: ['./model-list.component.scss']
})
export class PredictionModelListComponent implements OnInit, OnDestroy {
  private logName: string;
  private subscription: Subscription;

  public models: EventLogModel[] = [];

  constructor(private predictionService: PredictionService,
              private logService: LogService,
              private nzMessageService: NzMessageService,
              private rxStompService: RxStompService) { }

  ngOnInit(): void {
    // load open cases for current log
    this.logService.currentLog.subscribe(eventLog => {
      this.logName = eventLog.logName;
      this.loadList(this.logName);
    });

    // subscribe to model changes
    this.subscription = this.rxStompService.watch('/notifications/predictions/**')
      .subscribe((message) => this.handleNotifications(message));
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  loadList(logName: string): void {
    if (logName !== null) {
      this.predictionService.getByLogName(logName).subscribe(x => this.models = x);
    } else {
      this.predictionService.getAll().subscribe(x => this.models = x);
    }
  }

  handleNotifications(message: Message) {
    // finished?
    if ((message.headers as any).destination === '/notifications/predictions/training_finished') {
      const log = JSON.parse(message.body);
      if (log.logName === this.logName) {
        this.nzMessageService.success('Training model for event log <b>' + log.logName + '</b> completed successfully.');
        this.loadList(this.logName);
      }
    }
  }

  doDelete(model: EventLogModel): void {
    this.predictionService.delete(model.id).subscribe(x => {
      this.nzMessageService.success('Model <b>' + model.modelName + '</b> deleted successfully.');
      this.loadList(this.logName);
    });
  }

  doSetDefault(model: EventLogModel): void {
    this.predictionService.setDefault(model.id).subscribe(x => {
      this.nzMessageService.success('Model <b>' + model.modelName + '</b> updated successfully.');
      this.loadList(this.logName);
    });
  }

}
