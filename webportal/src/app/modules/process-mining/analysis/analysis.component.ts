import { Component, OnInit, ViewChild } from '@angular/core';
import { InsightListComponent } from 'src/app/analysis/insight-list/insight-list.component';
import { ProcessMapComponent } from 'src/app/analysis/process-map/processmap.component';
import { EventLogStatistics } from 'src/app/log/models/eventlog-statistics.model';
import { ActivatedRoute } from '@angular/router';
import { QueryService } from 'src/app/analysis/shared/query.service';
import { QueryConvertService } from 'src/app/analysis/shared/query-convert.service';
import { LocalStorageService } from 'src/app/shared/storage.service';
import { PathConditionComponent } from 'src/app/analysis/conditions/path-condition/path-condition.component';
import { AttributeConditionComponent } from 'src/app/analysis/conditions/attribute-condition/attribute-condition.component';
import { VariantConditionComponent } from 'src/app/analysis/conditions/variant-condition/variant-condition.component';
import { ClusterConditionComponent } from 'src/app/analysis/conditions/cluster-condition/cluster-condition.component';
import { Condition } from 'src/app/analysis/models/condition.model';
import { Recommendation } from 'src/app/analysis/models/recommendation';
import { ReworkConditionComponent } from 'src/app/analysis/conditions/rework-condition/rework-condition.component';
import { DurationConditionComponent } from 'src/app/analysis/conditions/duration-condition/duration-condition.component';

@Component({
  selector: 'app-analysis-module',
  templateUrl: './analysis.component.html',
  styleUrls: ['./analysis.component.scss']
})
export class AnalysisComponent implements OnInit {
  @ViewChild(InsightListComponent) private insightListComponent: InsightListComponent;
  @ViewChild(ProcessMapComponent) private processMapComponent: ProcessMapComponent;

  logName: string;
  context: EventLogStatistics;

  conditions: Condition[];

  constructor(
    private route: ActivatedRoute,
    private queryService: QueryService,
    private queryConvertService: QueryConvertService,
    private storageService: LocalStorageService
  ) {
  }

  ngOnInit() {
    this.logName = this.route.snapshot.paramMap.get('logName');

    // load queries from local storage
    if (window.history.state !== undefined && window.history.state.conditions !== undefined) {
      this.conditions = this.queryConvertService.convertFromQuery(window.history.state.conditions);
    } else {
      const query = this.storageService.readQueryConditions(this.logName);
      this.conditions = this.queryConvertService.convertFromQuery(query);
    }

    // update view components
    this.onUpdate();
  }

  onUpdate() {
    this.queryService.getStatistics(this.logName, this.queryConvertService.convertToQuery(this.conditions))
      .subscribe(statistics => this.context = statistics);

    // store queries to local storage
    this.storageService.writeQueryConditions(this.logName, this.queryConvertService.convertToQuery(this.conditions));

    if (this.insightListComponent !== undefined) {
      this.insightListComponent.update();
    }
  }

  onAddCondition(conditionType: string) {
    switch (conditionType) {
      case 'path':
        this.conditions.push(new Condition(PathConditionComponent, { }));
        break;
      case 'attribute':
        this.conditions.push(new Condition(AttributeConditionComponent, { }));
        break;
      case 'variant':
        this.conditions.push(new Condition(VariantConditionComponent, { }));
        break;
      case 'cluster':
        this.conditions.push(new Condition(ClusterConditionComponent, { }));
        break;
      case 'rework':
        this.conditions.push(new Condition(ReworkConditionComponent, { }));
        break;
      case 'duration':
        this.conditions.push(new Condition(DurationConditionComponent, { unit: 'days' }));
        break;
    }
  }

  onApplyRecommendation(recommendation: Recommendation) {
    this.conditions = this.queryConvertService.convertFromQuery(recommendation.conditions);

    this.queryService.getStatistics(this.logName, this.queryConvertService.convertToQuery(this.conditions))
      .subscribe(statistics => this.context = statistics);
  }
}
