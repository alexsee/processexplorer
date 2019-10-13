import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AnalysisComponent } from './modules/analysis/analysis.component';

import { NgZorroAntdModule, NZ_I18N, en_US } from 'ng-zorro-antd';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ProcessMapComponent } from './processmining/process-map/processmap.component';
import { ConditionListComponent } from './query/condition-list/condition-list.component';
import { ConditionDirective } from './query/condition.directive';
import { PathConditionComponent } from './query/path-condition/path-condition.component';
import { ConditionSingleComponent } from './query/condition-single/condition-single.component';
import { AttributeConditionComponent } from './query/attribute-condition/attribute-condition.component';
import { FormsModule } from '@angular/forms';
import { LogComponent } from './modules/log/log.component';
import { VariantConditionComponent } from './query/variant-condition/variant-condition.component';
import { InsightComponent } from './processmining/insight/insight.component';
import { InsightListComponent } from './processmining/insight-list/insight-list.component';
import { ClusterConditionComponent } from './query/cluster-condition/cluster-condition.component';
import { ChartComponent } from './dashboard/chart/chart.component';
import { TestComponent } from './modules/test/test.component';
import { LogAnnotationComponent } from './modules/log-annotation/log-annotation.component';
import { LogUploadComponent } from './modules/log-upload/log-upload.component';

@NgModule({
  declarations: [
    AppComponent,
    AnalysisComponent,
    ProcessMapComponent,

    ConditionListComponent,
    PathConditionComponent,
    VariantConditionComponent,
    ClusterConditionComponent,

    ConditionDirective,
    ConditionSingleComponent,
    AttributeConditionComponent,
    LogComponent,

    InsightComponent,
    InsightListComponent,
    ChartComponent,
    TestComponent,
    LogAnnotationComponent,
    LogUploadComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    BrowserAnimationsModule,

    NgZorroAntdModule,

    FormsModule
  ],
  providers: [
    { provide: NZ_I18N, useValue: en_US }
  ],
  entryComponents: [ AttributeConditionComponent, PathConditionComponent, VariantConditionComponent, ClusterConditionComponent ],
  bootstrap: [AppComponent]
})
export class AppModule { }
