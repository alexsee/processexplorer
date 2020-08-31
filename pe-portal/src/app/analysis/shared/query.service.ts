import { Injectable } from '@angular/core';

import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/internal/Observable';
import { environment } from 'src/environments/environment';

import { ProcessMapResult } from '../models/results/process-map-result.model';
import { Condition } from '../models/condition.model';
import { CaseAttributeValueResult } from '../models/results/case-attribute-value-result.model';
import { DrillDownResult } from '../models/results/drill-down-result.model';
import { EventLogStatistics } from 'src/app/log/models/eventlog-statistics.model';
import { SocialNetworkResult } from '../models/results/social-network-result.model';
import { Case } from '../models/case.model';

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

  getProcessMap(logName: string, conditions: Condition[], activityFilter: number[]): Observable<ProcessMapResult> {
    return this.http.post<ProcessMapResult>(
      environment.serviceUrl + '/query/process_map',
      { logName, conditions, activityFilter },
      httpOptions
    );
  }

  getSocialNetworkGraph(logName: string, conditions: Condition[]): Observable<SocialNetworkResult> {
    return this.http.post<SocialNetworkResult>(
      environment.serviceUrl + '/query/social_network',
      { logName, conditions },
      httpOptions
    );
  }

  getStatistics(logName: string, conditions?: Condition[]): Observable<EventLogStatistics> {
    if (conditions) {
      return this.http.post<EventLogStatistics>(
        environment.serviceUrl + '/query/statistics?logName=' + logName,
        conditions);
    } else {
      return this.http.get<EventLogStatistics>(environment.serviceUrl + '/query/statistics?logName=' + logName);
    }
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

  getSingleCase(logName: string, id: number): Observable<Case> {
    return this.http.get<Case>(environment.serviceUrl + '/query/case?logName=' + logName + '&id=' + id);
  }
}
