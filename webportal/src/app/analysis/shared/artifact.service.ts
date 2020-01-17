import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ArtifactResult } from '../models/results/artifact-result.model';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Artifact } from '../models/artifact.model';
import { ArtifactConfiguration } from '../models/artifact-configuration.model';
import { ArtifactUIField } from '../models/artifact-ui-field.model';

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type':  'application/json'
  })
};

@Injectable({
  providedIn: 'root'
})
export class ArtifactService {

  constructor(
    private http: HttpClient
  ) { }

  getArtifacts(): Observable<Artifact[]> {
    return this.http.get<Artifact[]>(environment.serviceUrl + '/artifacts');
  }

  getArtifactFields(artifact: string): Observable<ArtifactUIField[]> {
    return this.http.get<ArtifactUIField[]>(environment.serviceUrl + '/artifacts/ui?artifact=' + artifact);
  }

  getArtifactConfiguration(logName: string): Observable<ArtifactConfiguration[]> {
    return this.http.get<ArtifactConfiguration[]>(environment.serviceUrl + '/artifacts/configuration?logName=' + logName);
  }

  getArtifactResult(logName: string): Observable<ArtifactResult[]> {
    return this.http.get<ArtifactResult[]>(environment.serviceUrl + '/artifacts/evaluate?logName=' + logName);
  }

  save(logName: string, configuration: ArtifactConfiguration) {
    return this.http.post(
      environment.serviceUrl + '/artifacts/configuration?logName=' + logName, {
        id: configuration.id,
        type: configuration.type,
        activated: configuration.activated,
        configuration: configuration.configuration
      });
  }

  // tslint:disable-next-line:ban-types
  delete(artifact: ArtifactConfiguration): Observable<Object> {
    return this.http.delete(environment.serviceUrl + '/artifacts/configuration?id=' + artifact.id);
  }
}
