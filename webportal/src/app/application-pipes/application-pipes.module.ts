import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { GetChartDataPipe } from './get-chart-data.pipe';
import { GetChartLabelsPipe } from './get-chart-labels.pipe';

@NgModule({
  declarations: [
    GetChartDataPipe,
    GetChartLabelsPipe
  ],
  imports: [
    CommonModule
  ],
  exports: [
    GetChartDataPipe,
    GetChartLabelsPipe
  ]
})
export class ApplicationPipesModule { }
