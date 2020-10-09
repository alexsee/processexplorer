import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { InjectableRxStompConfig, RxStompService, rxStompServiceFactory } from '@stomp/ng2-stompjs';

import { RxStompConfig } from './rx-stomp.config';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AnalysisComponent } from './modules/process-mining/analysis/analysis.component';

import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzTableModule } from 'ng-zorro-antd/table';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzSelectModule } from 'ng-zorro-antd/select';
import { NzDividerModule } from 'ng-zorro-antd/divider';
import { NzDropDownModule } from 'ng-zorro-antd/dropdown';
import { NzSpinModule } from 'ng-zorro-antd/spin';
import { NzTagModule } from 'ng-zorro-antd/tag';
import { NzLayoutModule } from 'ng-zorro-antd/layout';
import { NzSliderModule } from 'ng-zorro-antd/slider';
import { NzBreadCrumbModule } from 'ng-zorro-antd/breadcrumb';
import { NzFormModule } from 'ng-zorro-antd/form';
import { NzUploadModule } from 'ng-zorro-antd/upload';
import { NzPageHeaderModule } from 'ng-zorro-antd/page-header';
import { NzListModule } from 'ng-zorro-antd/list';
import { NzProgressModule } from 'ng-zorro-antd/progress';
import { NzAvatarModule } from 'ng-zorro-antd/avatar';
import { NzStatisticModule } from 'ng-zorro-antd/statistic';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzDrawerModule } from 'ng-zorro-antd/drawer';
import { NzCollapseModule } from 'ng-zorro-antd/collapse';
import { NzModalModule } from 'ng-zorro-antd/modal';
import { NzPopoverModule } from 'ng-zorro-antd/popover';
import { NzRadioModule } from 'ng-zorro-antd/radio';
import { NzInputNumberModule } from 'ng-zorro-antd/input-number';
import { NzEmptyModule } from 'ng-zorro-antd/empty';
import { NzMessageModule } from 'ng-zorro-antd/message';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzSwitchModule } from 'ng-zorro-antd/switch';
import { NzTimelineModule } from 'ng-zorro-antd/timeline';
import { NzDescriptionsModule } from 'ng-zorro-antd/descriptions';
import { NzPopconfirmModule } from 'ng-zorro-antd/popconfirm';

import { NZ_I18N, en_US } from 'ng-zorro-antd/i18n';

import { HighchartsChartModule } from 'highcharts-angular';
import { GridsterModule } from 'angular-gridster2';

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

import { WidgetChartComponent } from './analysis/components/chart/chart.component';
import { WidgetHostComponent } from './analysis/components/widget/widget-host.component';
import { WidgetDirective } from './analysis/components/widget.directive';
import { WidgetProcessMapComponent } from './analysis/components/process-map/process-map.component';
import { TrainModelComponent } from './modules/predictions/train-model/train-model.component';
import { PredictionModelListComponent } from './modules/predictions/model-list/model-list.component';
import { OpenCasesListComponent } from './modules/predictions/open-cases-list/open-cases-list.component';
import { ModelDetailComponent } from './modules/predictions/model-detail/model-detail.component';
import { PredictionNavigationComponent } from './modules/predictions/prediction-navigation/prediction-navigation.component';

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

    WidgetHostComponent,
    WidgetDirective,
    WidgetChartComponent,
    WidgetProcessMapComponent,

    ModelDetailComponent,
    TrainModelComponent,
    PredictionModelListComponent,
    OpenCasesListComponent,
    PredictionNavigationComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    BrowserAnimationsModule,

    NzIconModule,
    NzButtonModule,
    NzTableModule,
    NzInputModule,
    NzSelectModule,
    NzDividerModule,
    NzDropDownModule,
    NzSpinModule,
    NzTagModule,
    NzLayoutModule,
    NzSliderModule,
    NzBreadCrumbModule,
    NzFormModule,
    NzUploadModule,
    NzPageHeaderModule,
    NzListModule,
    NzProgressModule,
    NzAvatarModule,
    NzStatisticModule,
    NzCardModule,
    NzDrawerModule,
    NzCollapseModule,
    NzModalModule,
    NzPopoverModule,
    NzRadioModule,
    NzInputNumberModule,
    NzEmptyModule,
    NzMessageModule,
    NzSwitchModule,
    NzTimelineModule,
    NzDescriptionsModule,
    NzPopconfirmModule,

    HighchartsChartModule,

    FormsModule,
    GridsterModule
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
