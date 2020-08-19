import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AnalysisComponent } from './modules/process-mining/analysis/analysis.component';
import { LogComponent } from './modules/general/log/log.component';
import { LogAnnotationComponent } from './modules/general/log-annotation/log-annotation.component';
import { LogUploadComponent } from './modules/general/log-upload/log-upload.component';
import { ArtifactsComponent } from './modules/process-mining/artifacts/artifacts.component';
import { ArtifactSettingsComponent } from './modules/process-mining/artifact-settings/artifact-settings.component';
import { PredictionModelListComponent as PredictionModelListComponent } from './modules/predictions/model-list/model-list.component';
import { TrainModelComponent } from './modules/predictions/train-model/train-model.component';
import { ModelDetailComponent } from './modules/predictions/model-detail/model-detail.component';
import { OpenCasesListComponent } from './modules/predictions/open-cases-list/open-cases-list.component';

const routes: Routes = [
  { path: '', component: LogComponent },

  { path: 'analysis', component: AnalysisComponent },

  { path: 'artifacts', component: ArtifactsComponent },
  { path: 'artifacts/settings/:logName', component: ArtifactSettingsComponent },

  { path: 'logs', component: LogComponent },
  { path: 'logs/upload', component: LogUploadComponent },
  { path: 'logs/annotations/:logName', component: LogAnnotationComponent },

  { path: 'models', component: PredictionModelListComponent },
  { path: 'logs/:logName/models', component: PredictionModelListComponent },
  { path: 'logs/:logName/train', component: TrainModelComponent },
  { path: 'models/:id', component: ModelDetailComponent },

  { path: 'cases/open', component: OpenCasesListComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
