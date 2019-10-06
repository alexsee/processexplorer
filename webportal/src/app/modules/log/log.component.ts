import { Component, OnInit } from '@angular/core';
import { LogService } from 'src/app/services/log.service';
import { Log } from 'src/app/entities/log';
import { EventLog } from 'src/app/entities/eventlog';

@Component({
  selector: 'app-log',
  templateUrl: './log.component.html',
  styleUrls: ['./log.component.scss']
})
export class LogComponent implements OnInit {
  public logs: EventLog[];
  public displayedColumns = ['logName', 'creationDate', 'status', 'actions'];

  constructor(
    private logService: LogService
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
}
