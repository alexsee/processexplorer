import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AnalysisComponent } from './modules/process-mining/analysis/analysis.component';
import { LogComponent } from './modules/general/log/log.component';
import { LogAnnotationComponent } from './modules/general/log-annotation/log-annotation.component';
import { LogUploadComponent } from './modules/general/log-upload/log-upload.component';
import { ArtifactsComponent } from './modules/process-mining/artifacts/artifacts.component';
import { ArtifactSettingsComponent } from './modules/process-mining/artifact-settings/artifact-settings.component';

const routes: Routes = [
  { path: '', component: LogComponent },

  { path: 'analysis/:logName', component: AnalysisComponent },

  { path: 'artifacts/:logName', component: ArtifactsComponent },
  { path: 'artifacts/settings/:logName', component: ArtifactSettingsComponent },

  { path: 'logs', component: LogComponent },
  { path: 'logs/upload', component: LogUploadComponent },
  { path: 'logs/annotations/:logName', component: LogAnnotationComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
