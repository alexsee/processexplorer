import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EventLogModel } from 'src/app/log/models/eventlog-model.model';
import { environment } from 'src/environments/environment';
import { TrainingConfiguration } from './models/training-configuration.model';

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
}
