import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/internal/Observable';
import { environment } from 'src/environments/environment';
import { EventLog } from '../models/eventlog.model';
import { EventLogStatistics } from '../models/eventlog-statistics.model';
import { EventLogAnnotation } from '../models/eventlog-annotation.model';
import { map } from 'rxjs/operators';
import { BehaviorSubject, ReplaySubject } from 'rxjs';
import { AuthenticationService } from 'src/app/shared/authentication.service';


@Injectable({
  providedIn: 'root'
})
export class LogService {
  private currentLogSubject = new ReplaySubject<EventLog>(1);
  private currentLogsSubject = new ReplaySubject<EventLog[]>(1);

  public currentLog = this.currentLogSubject.asObservable();
  public currentEventLogs = this.currentLogsSubject.asObservable();

  constructor(
    private http: HttpClient,
    private authenticationService: AuthenticationService
  ) {
    this.authenticationService.loginState.subscribe(isLoggedIn => {
      if (!isLoggedIn) {
        return;
      }

      this.list().subscribe(logs => this.currentLogsSubject.next(logs));

      this.currentEventLogs.subscribe(log => {
        // use current selected
        if (localStorage.getItem('currentLog') !== undefined) {
          this.currentLogSubject.next(log.filter(y => y.logName === localStorage.getItem('currentLog'))[0]);
        }
      });
    });
  }

  setCurrentLog(eventLog: string) {
    localStorage.setItem('currentLog', eventLog);
    this.currentEventLogs.subscribe(x => this.currentLogSubject.next(x.filter(y => y.logName === eventLog)[0]));
  }

  upload(logName: string, file: File) {
    const formData: FormData = new FormData();
    formData.append('file', file, file.name);
    formData.append('logName', logName);

    return this.http.post(environment.serviceUrl + '/logs/upload', formData);
  }

  list(): Observable<EventLog[]> {
    return this.http.get<EventLog[]>(environment.serviceUrl + '/logs');
  }

  import(logName: string) {
    return this.http.get(environment.serviceUrl + '/logs/import?logName=' + logName);
  }

  process(logName: string) {
    return this.http.get(environment.serviceUrl + '/logs/process?logName=' + logName);
  }

  delete(logName: string) {
    return this.http.delete(environment.serviceUrl + '/logs?logName=' + logName);
  }

  getAllLogs(): Observable<EventLogStatistics[]> {
    return this.http.get<EventLogStatistics[]>(environment.serviceUrl + '/logs/all_statistics');
  }

  getAnnotations(logName: string): Observable<EventLogAnnotation[]> {
    return this.http.get<EventLogAnnotation[]>(environment.serviceUrl + '/logs/annotations?logName=' + logName);
  }

  saveAnnotation(annotation: EventLogAnnotation) {
    return this.http.post(environment.serviceUrl + '/logs/annotation', annotation);
  }

  deleteAnnotation(id: number) {
    return this.http.delete(environment.serviceUrl + '/logs/annotation?id=' + id);
  }
}
