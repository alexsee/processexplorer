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
import { AuthGuardService } from './shared/auth-guard.service';
import { LoginComponent } from './auth/login/login.component';

const routes: Routes = [
  { path: 'login', component: LoginComponent },

  { path: '', component: LogComponent, canActivate: [AuthGuardService] },

  { path: 'analysis', component: AnalysisComponent, canActivate: [AuthGuardService] },

  { path: 'artifacts', component: ArtifactsComponent, canActivate: [AuthGuardService] },
  { path: 'artifacts/settings/:logName', component: ArtifactSettingsComponent, canActivate: [AuthGuardService] },

  { path: 'logs', component: LogComponent, canActivate: [AuthGuardService] },
  { path: 'logs/upload', component: LogUploadComponent, canActivate: [AuthGuardService] },
  { path: 'logs/annotations/:logName', component: LogAnnotationComponent, canActivate: [AuthGuardService] },

  { path: 'models', component: PredictionModelListComponent, canActivate: [AuthGuardService] },
  { path: 'logs/:logName/models', component: PredictionModelListComponent, canActivate: [AuthGuardService] },
  { path: 'logs/:logName/train', component: TrainModelComponent, canActivate: [AuthGuardService] },
  { path: 'models/:id', component: ModelDetailComponent, canActivate: [AuthGuardService] },

  { path: 'cases/open', component: OpenCasesListComponent, canActivate: [AuthGuardService] }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
