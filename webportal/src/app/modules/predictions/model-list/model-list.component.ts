import { Component, OnInit } from '@angular/core';
import { EventLogModel } from 'src/app/log/models/eventlog-model.model';
import { PredictionService } from '../prediction.service';
import { ActivatedRoute } from '@angular/router';
import { RxStompService } from '@stomp/ng2-stompjs';
import { Subscription } from 'rxjs';
import { Message } from 'stompjs';
import { NzMessageService } from 'ng-zorro-antd';

@Component({
  selector: 'app-prediction-model-list',
  templateUrl: './model-list.component.html',
  styleUrls: ['./model-list.component.scss']
})
export class PredictionModelListComponent implements OnInit {
  private logName: string;
  private subscription: Subscription;

  public models: EventLogModel[];

  constructor(private predicationService: PredictionService,
              private route: ActivatedRoute,
              private nzMessageService: NzMessageService,
              private rxStompService: RxStompService) { }

  ngOnInit(): void {
    this.logName = this.route.snapshot.paramMap.get('logName');
    this.loadList(this.logName);

    this.subscription = this.rxStompService.watch('/notifications/predictions/**')
      .subscribe((message) => this.handleNotifications(message));
  }

  loadList(logName: string): void {
    if (logName !== null) {
      this.predicationService.getByLogName(logName).subscribe(x => this.models = x);
    } else {
      this.predicationService.getAll().subscribe(x => this.models = x);
    }
  }

  handleNotifications(message: Message) {
    // finished?
    if ((message.headers as any).destination === '/notifications/predictions/training_finished') {
      const log = JSON.parse(message.body);
      this.nzMessageService.success('Training model for event log <b>' + log.logName + '</b> completed successfully.');

      this.loadList(this.logName);
    }
  }

}
