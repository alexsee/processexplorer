import { Injectable } from '@angular/core';

import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/internal/Observable';
import { environment } from 'src/environments/environment';

import { ProcessMapResult } from '../models/results/process-map-result.model';
import { Condition } from '../models/condition.model';
import { CaseAttributeValueResult } from '../models/results/case-attribute-value-result.model';
import { DrillDownResult } from '../models/results/drill-down-result.model';
import { EventLogStatistics } from 'src/app/log/models/eventlog-statistics.model';

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

  getProcessMap(logName: string, conditions: Condition[]): Observable<ProcessMapResult> {
    return this.http.post<ProcessMapResult>(
      environment.serviceUrl + '/query/process_map',
      { logName, conditions },
      httpOptions
    );
  }

  getStatistics(logName: string): Observable<EventLogStatistics> {
    return this.http.get<EventLogStatistics>(environment.serviceUrl + '/query/statistics?logName=' + logName);
  }

  getCaseAttributeValues(logName: string, attributeName: string, conditions: Condition[]): Observable<CaseAttributeValueResult> {
    return this.http.post<CaseAttributeValueResult>(
      environment.serviceUrl + '/query/case_attribute_values',
      { logName, attributeName, conditions }
    );
  }

  getDrillDown(logName: string, selections: any[], conditions: Condition[]): Observable<DrillDownResult> {
    return this.http.post<DrillDownResult>(
      environment.serviceUrl + '/query/drill_down',
      { logName, selections, conditions }
    );
  }
}
