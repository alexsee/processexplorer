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

@Component({
  selector: 'app-analysis-module',
  templateUrl: './analysis.component.html',
  styleUrls: ['./analysis.component.scss']
})
export class AnalysisComponent implements OnInit {
  @ViewChild(InsightListComponent, {static: false}) private insightListComponent: InsightListComponent;
  @ViewChild(ProcessMapComponent, {static: false}) private processMapComponent: ProcessMapComponent;

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
    this.queryService.getStatistics(this.logName).subscribe(statistics => this.context = statistics);

    // load queries from local storage
    const query = this.storageService.readQueryConditions(this.logName);
    this.conditions = this.queryConvertService.convertFromQuery(query);

    // update view components
    this.onUpdate();
  }

  onUpdate() {
    // store queries to local storage
    this.storageService.writeQueryConditions(this.logName, this.queryConvertService.convertToQuery(this.conditions));

    if (this.insightListComponent !== undefined) {
      this.insightListComponent.update();
    }

    if (this.processMapComponent !== undefined) {
      this.processMapComponent.update();
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
    }
  }
}
