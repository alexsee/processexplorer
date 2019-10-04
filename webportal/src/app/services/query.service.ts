import { Injectable } from '@angular/core';

import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/internal/Observable';
import { ProcessMap } from '../entities/processmap';
import { Condition } from '../entities/conditions/condition';
import { Log } from '../entities/log';
import { environment } from 'src/environments/environment';
import { Insight } from '../entities/insight';
import { PathConditionComponent } from '../query/path-condition/path-condition.component';
import { AttributeConditionComponent } from '../query/attribute-condition/attribute-condition.component';
import { VariantConditionComponent } from '../query/variant-condition/variant-condition.component';
import { ClusterConditionComponent } from '../query/cluster-condition/cluster-condition.component';
import { CaseAttributeValueResult } from '../query/results/case-attribute-value-result';

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
    return this.http.post<ProcessMap>(environment.serviceUrl + '/getprocessmap?logName=' + logName, conditions, httpOptions);
  }

  getStatistics(logName: string): Observable<Log> {
    return this.http.get<Log>(environment.serviceUrl + '/statistics?logName=' + logName);
  }

  getInsights(logName: string, conditions: Condition[]): Observable<Insight[]> {
    return this.http.post<Insight[]>(environment.serviceUrl + '/analysis/insights?logName=' + logName, conditions, httpOptions);
  }

  getCaseAttributeValues(logName: string, attributeName: string, conditions: Condition[]): Observable<CaseAttributeValueResult> {
    return this.http.post<CaseAttributeValueResult>(
      environment.serviceUrl + '/case_attribute_values',
      { logName, attributeName, conditions }
    );
  }
}
