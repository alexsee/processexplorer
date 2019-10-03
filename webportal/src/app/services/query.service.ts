import { Injectable } from '@angular/core';

import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/internal/Observable';
import { ProcessMap } from '../entities/processmap';
import { Condition } from '../entities/conditions/condition';
import { Log } from '../entities/log';
import { environment } from 'src/environments/environment';
import { Insight } from '../entities/insight';
import { PathConditionComponent } from '../query/pathcondition/pathcondition.component';
import { AttributeConditionComponent } from '../query/attribute-condition/attribute-condition.component';
import { VariantConditionComponent } from '../query/variant-condition/variant-condition.component';
import { ClusterConditionComponent } from '../query/cluster-condition/cluster-condition.component';

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

  convertToQuery(conditions: Condition[]) {
    const query = [];

    for (const cond of conditions) {
      query.push({ type: cond.component.name.toLocaleLowerCase().replace('conditioncomponent', ''), ...cond.data});
    }

    return query;
  }

  convertFromQuery(query: any[]): Condition[] {
    const conditions = [];

    if (!query) {
      return conditions;
    }

    for (const qry of query) {
      if (qry.type === 'path') {
        conditions.push(new Condition(PathConditionComponent, qry));
      } else if (qry.type === 'attribute') {
        conditions.push(new Condition(AttributeConditionComponent, qry));
      } else if (qry.type === 'variant') {
        conditions.push(new Condition(VariantConditionComponent, qry));
      } else if (qry.type === 'cluster') {
        conditions.push(new Condition(ClusterConditionComponent, qry));
      }
    }

    return conditions;
  }
}
