import { Injectable } from '@angular/core';

import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/internal/Observable';
import { ProcessMap } from '../entities/processmap';
import { Condition } from '../entities/conditions/condition';
import { Log } from '../entities/log';
import { environment } from 'src/environments/environment';
import { Insight } from '../entities/insight';

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type':  'application/json'
  })
};

@Injectable({
  providedIn: 'root'
})
export class QueryService {

  constructor(
    private http: HttpClient
  ) { }

  getProcessMap(logName: string, conditions: Condition[]): Observable<ProcessMap> {
    return this.http.post<ProcessMap>('http://localhost:8080/getprocessmap?logName=' + logName, conditions, httpOptions);
  }

  getStatistics(logName: string): Observable<Log> {
    return this.http.get<Log>('http://localhost:8080/statistics?logName=' + logName);
  }

  getInsights(logName: string, conditions: Condition[]): Observable<Insight[]> {
    return this.http.post<Insight[]>(environment.serviceUrl + '/analysis/insights?logName=' + logName, conditions, httpOptions);
  }
}
