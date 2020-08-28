import { Component } from '@angular/core';
import { LogService } from './log/shared/log.service';
import { EventLog } from './log/models/eventlog.model';
import { AuthenticationService } from './shared/authentication.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'webportal';

  logs: EventLog[];
  selectedEventLog: EventLog;

  public isLoggedIn = false;

  constructor(
    private logService: LogService,
    private authenticationService: AuthenticationService
  ) {
    this.logService.currentEventLogs.subscribe(logs => this.logs = logs);
    this.logService.currentLog.subscribe(x => this.selectedEventLog = x);

    authenticationService.loginState.subscribe(x => this.isLoggedIn = x);
  }

  onSelectedEventLogChange() {
    this.logService.setCurrentLog(this.selectedEventLog.logName);
  }

  trackByLogName(index, item) {
    return item.logName;
  }

  compareByLogName(o1: EventLog, o2: EventLog) {
    if (o1 === null || o2 === null) {
      return false;
    }
    return o1.logName === o2.logName;
  }

  doLogout(): void {
    this.authenticationService.logOut();
  }
}
