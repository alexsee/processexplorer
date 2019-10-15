import { Injectable } from '@angular/core';

import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/internal/Observable';
import { Condition } from '../entities/conditions/condition';
import { Log } from '../entities/log';
import { environment } from 'src/environments/environment';
import { Insight } from '../entities/insight';

import { CaseAttributeValueResult } from '../query/results/case-attribute-value-result';
import { ProcessMapResult } from '../query/results/process-map-result';
import { DrillDownResult } from '../query/results/drill-down-result';

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

  getStatistics(logName: string): Observable<Log> {
    return this.http.get<Log>(environment.serviceUrl + '/query/statistics?logName=' + logName);
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
