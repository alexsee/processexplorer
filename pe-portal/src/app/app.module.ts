import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { InjectableRxStompConfig, RxStompService, rxStompServiceFactory } from '@stomp/ng2-stompjs';

import { RxStompConfig } from './rx-stomp.config';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AnalysisComponent } from './modules/process-mining/analysis/analysis.component';

import { NgZorroAntdModule, NZ_I18N, en_US } from 'ng-zorro-antd';

import { HighchartsChartModule } from 'highcharts-angular';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule } from '@angular/forms';
import { LogComponent } from './modules/general/log/log.component';
import { LogAnnotationComponent } from './modules/general/log-annotation/log-annotation.component';
import { LogUploadComponent } from './modules/general/log-upload/log-upload.component';
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
import { RecommendationListComponent } from './analysis/recommendation-list/recommendation-list.component';
import { ReworkConditionComponent } from './analysis/conditions/rework-condition/rework-condition.component';
import { ArtifactsComponent } from './modules/process-mining/artifacts/artifacts.component';
import { ArtifactSettingsComponent } from './modules/process-mining/artifact-settings/artifact-settings.component';
import { ArtifactSettingsFieldComponent } from './modules/process-mining/artifact-settings/artifact-settings-field.component';
import { DurationConditionComponent } from './analysis/conditions/duration-condition/duration-condition.component';
import { ResourcePathConditionComponent } from './analysis/conditions/resource-path-condition/resource-path-condition.component';
import { DashboardComponent } from './modules/process-mining/dashboard/dashboard.component';

@NgModule({
  declarations: [
    AppComponent,
    AnalysisComponent,
    ProcessMapComponent,

    ConditionListComponent,
    PathConditionComponent,
    ResourcePathConditionComponent,
    VariantConditionComponent,
    ClusterConditionComponent,
    ReworkConditionComponent,
    DurationConditionComponent,

    ConditionDirective,
    ConditionSingleComponent,
    AttributeConditionComponent,
    LogComponent,

    InsightComponent,
    InsightListComponent,
    LogAnnotationComponent,
    LogUploadComponent,
    RecommendationListComponent,

    ArtifactsComponent,
    ArtifactSettingsComponent,
    ArtifactSettingsFieldComponent,
    DashboardComponent,
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    BrowserAnimationsModule,

    NgZorroAntdModule,
    HighchartsChartModule,

    FormsModule
  ],
  providers: [
    {
      provide: NZ_I18N,
      useValue: en_US
    }, {
      provide: InjectableRxStompConfig,
      useClass: RxStompConfig,
      deps: []
    }, {
      provide: RxStompService,
      useFactory: rxStompServiceFactory,
      deps: [InjectableRxStompConfig]
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
