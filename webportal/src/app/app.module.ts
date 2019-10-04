import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AnalysisComponent } from './modules/analysis/analysis.component';

import { MatToolbarModule } from '@angular/material/toolbar';
import { MatTableModule } from '@angular/material/table';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatIconModule } from '@angular/material/icon';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatListModule } from '@angular/material/list';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import { ChartsModule } from 'ng2-charts';

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
    InsightListComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    BrowserAnimationsModule,

    MatToolbarModule,
    MatTableModule,
    MatSelectModule,
    MatInputModule,
    MatCardModule,
    MatButtonModule,
    MatMenuModule,
    MatIconModule,
    MatExpansionModule,
    MatChipsModule,
    MatProgressBarModule,
    MatListModule,
    MatProgressSpinnerModule,

    ChartsModule,

    FormsModule
  ],
  providers: [],
  entryComponents: [ AttributeConditionComponent, PathConditionComponent, VariantConditionComponent, ClusterConditionComponent ],
  bootstrap: [AppComponent]
})
export class AppModule { }
