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

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ProcessmapComponent } from './processmap/processmap.component';
import { ConditionListComponent } from './query/condition-list/condition-list.component';
import { ConditionDirective } from './query/condition.directive';
import { PathConditionComponent } from './query/pathcondition/pathcondition.component';
import { ConditionSingleComponent } from './query/condition-single/condition-single.component';
import { AttributeConditionComponent } from './query/attribute-condition/attribute-condition.component';
import { FormsModule } from '@angular/forms';
import { LogComponent } from './modules/log/log.component';
import { VariantConditionComponent } from './query/variant-condition/variant-condition.component';

@NgModule({
  declarations: [
    AppComponent,
    AnalysisComponent,
    ProcessmapComponent,
    ConditionListComponent,
    PathConditionComponent,
    ConditionDirective,
    ConditionSingleComponent,
    AttributeConditionComponent,
    LogComponent,
    VariantConditionComponent
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

    FormsModule
  ],
  providers: [],
  entryComponents: [ AttributeConditionComponent, PathConditionComponent, VariantConditionComponent ],
  bootstrap: [AppComponent]
})
export class AppModule { }
