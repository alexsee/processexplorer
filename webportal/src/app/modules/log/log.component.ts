import { Component, OnInit } from '@angular/core';

import { NzMessageService } from 'ng-zorro-antd/message';
import { AnalysisService } from 'src/app/analysis/shared/analysis.service';
import { EventLog } from 'src/app/log/models/eventlog.model';
import { LogService } from 'src/app/log/shared/log.service';

@Component({
  selector: 'app-log',
  templateUrl: './log.component.html',
  styleUrls: ['./log.component.scss']
})
export class LogComponent implements OnInit {
  public logs: EventLog[];
  public displayedColumns = ['logName', 'creationDate', 'status', 'actions'];

  constructor(
    private nzMessageService: NzMessageService,
    private logService: LogService,
    private analysisService: AnalysisService
  ) { }

  ngOnInit() {
    this.loadList();
  }

  loadList() {
    this.logService.list()
      .subscribe(x => this.logs = x);
  }

  doProcess(log: EventLog) {
    this.logService.process(log.logName)
      .subscribe(x => log.processing = true);
  }

  doImport(log: EventLog) {
    this.logService.import(log.logName)
      .subscribe(x => log.processing = true);
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
      .subscribe(x => {
        this.nzMessageService.success('Event log <b>' + log.logName + '</b> clustered successfully.');
        log.processing = false;
      }, error => {
        this.nzMessageService.error('An error occurred during the clustering of <b>' + log.logName + '</b>.');
        log.processing = false;
      });
  }
}
