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

  getAllLogs(): Observable<Log[]> {
    return this.http.get<Log[]>(environment.serviceUrl + '/logs/all_statistics');
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
