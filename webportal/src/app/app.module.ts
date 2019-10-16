import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AnalysisComponent } from './modules/analysis/analysis.component';

import { NgZorroAntdModule, NZ_I18N, en_US } from 'ng-zorro-antd';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule } from '@angular/forms';
import { LogComponent } from './modules/log/log.component';
import { TestComponent } from './modules/test/test.component';
import { LogAnnotationComponent } from './modules/log-annotation/log-annotation.component';
import { LogUploadComponent } from './modules/log-upload/log-upload.component';
import { ProcessMapComponent } from './analysis/process-map/processmap.component';
import { ConditionListComponent } from './analysis/condition-list/condition-list.component';
import { PathConditionComponent } from './analysis/conditions/path-condition/path-condition.component';
import { VariantConditionComponent } from './analysis/conditions/variant-condition/variant-condition.component';
import { ClusterConditionComponent } from './analysis/conditions/cluster-condition/cluster-condition.component';
import { ConditionDirective } from './analysis/condition.directive';
import { ConditionSingleComponent } from './analysis/condition-single/condition-single.component';
import { AttributeConditionComponent } from './analysis/conditions/attribute-condition/attribute-condition.component';
import { InsightComponent } from './analysis/insight/insight.component';
import { InsightListComponent } from './analysis/insight-list/insight-list.component';
import { ChartComponent } from './analysis/chart/chart.component';

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
