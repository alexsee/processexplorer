import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EventLogModel } from 'src/app/log/models/eventlog-model.model';
import { environment } from 'src/environments/environment';
import { TrainingConfiguration } from './shared/training-configuration.model';
import { OpenCase } from './shared/open-case.model';

@Injectable({
  providedIn: 'root'
})
export class PredictionService {

  constructor(private http: HttpClient) { }

  getAll(): Observable<EventLogModel[]> {
    return this.http.get<EventLogModel[]>(environment.serviceUrl + '/prediction/models');
  }

  getByLogName(logName: string): Observable<EventLogModel[]> {
    return this.http.get<EventLogModel[]>(environment.serviceUrl + '/prediction/models?logName=' + logName);
  }

  train(trainingConfiguration: TrainingConfiguration): Observable<void> {
    return this.http.post<void>(environment.serviceUrl + '/prediction/train', trainingConfiguration);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(environment.serviceUrl + '/prediction?id=' + id);
  }

  enableCaseManagement(logName: string): Observable<void> {
    return this.http.get<void>(environment.serviceUrl + '/prediction/init_case_management?logName=' + logName);
  }

  getOpenCases(logName: string): Observable<OpenCase[]> {
    return this.http.get<OpenCase[]>(environment.serviceUrl + '/prediction/open_cases?logName=' + logName);
  }

  setDefault(modelId: number): Observable<EventLogModel> {
    return this.http.post<EventLogModel>(environment.serviceUrl + '/prediction/default?modelId=' + modelId, null);
  }

  predictOpenCases(logName: string): Observable<void> {
    return this.http.get<void>(environment.serviceUrl + '/prediction/predict/open_cases?logName=' + logName);
  }
}
