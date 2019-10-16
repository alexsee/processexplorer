import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { HttpHeaders, HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Insight } from '../models/insight.model';
import { Condition } from '../models/condition.model';

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type':  'application/json'
  })
};

@Injectable({
  providedIn: 'root'
})
export class AnalysisService {

  constructor(
    private http: HttpClient
  ) { }

  executeTraceClustering(logName: string) {
    return this.http.post(environment.serviceUrl + '/analysis/simple_trace_clustering?logName=' + logName, null, httpOptions);
  }

  getInsights(logName: string, conditions: Condition[]): Observable<Insight[]> {
    return this.http.post<Insight[]>(environment.serviceUrl + '/analysis/insights?logName=' + logName, conditions, httpOptions);
  }

}
