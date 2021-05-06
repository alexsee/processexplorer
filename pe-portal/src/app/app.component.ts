import { Component } from '@angular/core';
import { LogService } from './log/shared/log.service';
import { EventLog } from './log/models/eventlog.model';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'webportal';

  logs: EventLog[];
  selectedEventLog: EventLog;

  public isLoggedIn = true;

  constructor(
    private logService: LogService
  ) {
    this.logService.currentEventLogs.subscribe(logs => this.logs = logs);
    this.logService.currentLog.subscribe(x => this.selectedEventLog = x);
  }

  onSelectedEventLogChange() {
    this.logService.setCurrentLog(this.selectedEventLog.logName);
  }

  trackByLogName(index, item) {
    return item.logName;
  }

  compareByLogName(o1: EventLog, o2: EventLog) {
    if (o1 === null || o2 === null || o1 === undefined || o2 === undefined) {
      return false;
    }
    return o1.logName === o2.logName;
  }
}
