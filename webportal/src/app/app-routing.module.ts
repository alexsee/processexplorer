import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AnalysisComponent } from './modules/analysis/analysis.component';
import { LogComponent } from './modules/log/log.component';
import { TestComponent } from './modules/test/test.component';
import { LogAnnotationComponent } from './modules/log-annotation/log-annotation.component';
import { LogUploadComponent } from './modules/log-upload/log-upload.component';

const routes: Routes = [
  { path: 'analysis', component: LogComponent },
  { path: 'analysis/:logName', component: AnalysisComponent },

  { path: 'logs', component: LogComponent },
  { path: 'logs/upload', component: LogUploadComponent },
  { path: 'logs/annotations/:logName', component: LogAnnotationComponent },

  { path: 'test', component: TestComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
