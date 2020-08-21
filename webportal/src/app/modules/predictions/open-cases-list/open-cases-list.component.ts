import { Component, OnInit, OnDestroy } from '@angular/core';
import { PredictionService } from '../prediction.service';
import { OpenCase } from '../shared/open-case.model';
import { LogService } from 'src/app/log/shared/log.service';
import { QueryService } from 'src/app/analysis/shared/query.service';
import { Case } from 'src/app/analysis/models/case.model';
import { NzMessageService } from 'ng-zorro-antd';
import { RxStompService } from '@stomp/ng2-stompjs';
import { Subscription } from 'rxjs';
import { Message } from 'stompjs';

@Component({
  selector: 'app-open-cases-list',
  templateUrl: './open-cases-list.component.html',
  styleUrls: ['./open-cases-list.component.scss']
})
export class OpenCasesListComponent implements OnInit, OnDestroy {
  public logName: string;
  public openCases: OpenCase[];
  public case: Case;

  private subscription: Subscription;

  constructor(private logService: LogService,
              private predictionService: PredictionService,
              private queryService: QueryService,
              private nzMessageService: NzMessageService,
              private rxStompService: RxStompService) { }

  ngOnInit(): void {
    // register for subscription service
    this.subscription = this.rxStompService.watch('/notifications/predictions/prediction_finished')
      .subscribe((message) => this.handleNotifications(message));

    // load open cases for current log
    this.logService.currentLog.subscribe(eventLog => {
      this.logName = eventLog.logName;
      this.loadList();
    });
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  handleNotifications(message: Message) {
    if ((message.headers as any).destination === '/notifications/predictions/prediction_finished') {
      const log = JSON.parse(message.body);
      if (log.logName === this.logName) {
        this.loadList();
      }
    }
  }

  loadList(): void {
    this.predictionService.getOpenCases(this.logName).subscribe(cases => {
      this.openCases = cases;
    });
  }

  showSingleCase(caseId: number) {
    this.queryService.getSingleCase(this.logName, caseId).subscribe(c => this.case = c);
  }

  doRefresh(): void {
    this.predictionService.predictOpenCases(this.logName).subscribe(x => {
      this.nzMessageService.success('Prediction for event log <b>' + this.logName + '</b> in background started successfully.');
    });
  }

}
