import { Component, OnInit, OnDestroy } from '@angular/core';

import { NzMessageService } from 'ng-zorro-antd/message';
import { AnalysisService } from 'src/app/analysis/shared/analysis.service';
import { EventLog } from 'src/app/log/models/eventlog.model';
import { LogService } from 'src/app/log/shared/log.service';
import { RxStompService } from '@stomp/ng2-stompjs';
import { Subscription } from 'rxjs';
import { Message } from '@stomp/stompjs';

@Component({
  selector: 'app-log',
  templateUrl: './log.component.html',
  styleUrls: ['./log.component.scss']
})
export class LogComponent implements OnInit, OnDestroy {
  public logs: EventLog[];
  public displayedColumns = ['logName', 'creationDate', 'status', 'actions'];

  private subscription: Subscription;

  constructor(
    private nzMessageService: NzMessageService,
    private logService: LogService,
    private analysisService: AnalysisService,
    private rxStompService: RxStompService
  ) { }

  ngOnInit() {
    this.loadList();

    this.subscription = this.rxStompService.watch('/notifications/logs/**').subscribe((message) => this.handleNotifications(message));
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  handleNotifications(message: Message) {
    // finished?
    if ((message.headers as any).destination === '/notifications/logs/analysis_finished') {
      const log = JSON.parse(message.body);
      this.nzMessageService.success('Event log <b>' + log.logName + '</b> analysis completed successfully.');
    } else if ((message.headers as any).destination === '/notifications/logs/import_finished') {
      const log = JSON.parse(message.body);
      if (log.errorMessage) {
        this.nzMessageService.error('Event log <b>' + log.logName + '</b> could not be imported.');
      } else {
        this.nzMessageService.success('Event log <b>' + log.logName + '</b> imported successfully.');
      }
    } else if ((message.headers as any).destination === '/notifications/logs/processing_finished') {
      const log = JSON.parse(message.body);
      this.nzMessageService.success('Event log <b>' + log.logName + '</b> processed successfully.');
    }

    this.loadList();
  }

  loadList() {
    this.logService.list().subscribe(x => {
      this.logs = x;
      this.logs.sort((one, two) => (one.fileName > two.fileName ? 1 : -1));
    });
  }

  doProcess(log: EventLog) {
    this.logService.process(log.logName).subscribe();
  }

  doImport(log: EventLog) {
    this.logService.import(log.logName).subscribe();
  }

  doDelete(log: EventLog) {
    this.logService.delete(log.logName)
      .subscribe(x => {
        this.loadList();
        this.nzMessageService.success('Event log <b>' + log.logName + '</b> deleted successfully.');
      }, error => {
        this.nzMessageService.error('Could not delete <b>' + log.logName + '</b>.');
      });
  }

  doClustering(log: EventLog) {
    log.processing = true;
    this.analysisService.executeTraceClustering(log.logName)
      .subscribe(x => { }, error => {
        this.nzMessageService.error('An error occurred during the clustering of <b>' + log.logName + '</b>.');
        log.processing = false;
      });
  }

  doMultiClustering(log: EventLog) {
    log.processing = true;
    this.analysisService.executeMultiPerspectiveTraceClustering(log.logName)
      .subscribe(x => { }, error => {
        this.nzMessageService.error('An error occurred during the clustering of <b>' + log.logName + '</b>.');
        log.processing = false;
      });
  }
}
