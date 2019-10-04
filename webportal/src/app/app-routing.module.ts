import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AnalysisComponent } from './modules/analysis/analysis.component';
import { LogComponent } from './modules/log/log.component';

const routes: Routes = [
  { path: 'analysis', component: LogComponent },
  { path: 'analysis/:logName', component: AnalysisComponent },
  { path: 'logs', component: LogComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
