import { Component, OnInit } from '@angular/core';
import { LogService } from './log/shared/log.service';
import { EventLogStatistics } from './log/models/eventlog-statistics.model';
import { EventLog } from './log/models/eventlog.model';
import { ActivatedRoute, Router, NavigationStart } from '@angular/router';
import { Observable } from 'rxjs';
import { filter } from 'rxjs/operators';
import { LocalStorageService } from './shared/storage.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'webportal';

  logs: EventLog[];
  selectedEventLog: EventLog;

  constructor(
    private logService: LogService,
    private storageService: LocalStorageService
  ) { }

  ngOnInit() {
    this.loadList();
  }

  loadList() {
    this.logService.list().subscribe(x => {
      this.logs = x;

      // use current selected
      const currentLogName = this.storageService.getCurrentLog();
      if (currentLogName !== undefined) {
        this.selectedEventLog = x.filter(y => y.logName === currentLogName)[0];
      }
    });
  }

  onSelectedEventLogChange() {
    this.storageService.setCurrentLog(this.selectedEventLog.logName);
  }
}
