import { Injectable } from '@angular/core';
import { HttpClient, HttpXsrfTokenExtractor } from '@angular/common/http';
import { Log } from '../entities/log';
import { Observable } from 'rxjs/internal/Observable';
import { EventLog } from '../entities/eventlog';
import { environment } from 'src/environments/environment';
import { EventLogAnnotation } from '../entities/eventlog-annotation';

@Injectable({
  providedIn: 'root'
})
export class LogService {

  constructor(
    private http: HttpClient
  ) { }

  list(): Observable<EventLog[]> {
    return this.http.get<EventLog[]>(environment.serviceUrl + '/logs');
  }

  import(logName: string) {
    return this.http.get(environment.serviceUrl + '/logs/import?logName=' + logName);
  }

  process(logName: string) {
    return this.http.get(environment.serviceUrl + '/logs/process?logName=' + logName);
  }

  getAllLogs(): Observable<Log[]> {
    return this.http.get<Log[]>(environment.serviceUrl + '/logs/all_statistics');
  }

  getAnnotations(logName: string): Observable<EventLogAnnotation[]> {
    return this.http.get<EventLogAnnotation[]>(environment.serviceUrl + '/logs/annotations?logName=' + logName);
  }

  saveAnnotations(annotations: EventLogAnnotation[]) {
    return this.http.post(environment.serviceUrl + '/logs/annotations', annotations);
  }
}
