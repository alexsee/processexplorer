import { Component, OnInit, ViewChild } from '@angular/core';

import { QueryService } from '../../services/query.service';
import { Condition } from '../../entities/conditions/condition';
import { PathConditionComponent } from '../../query/path-condition/path-condition.component';
import { Log } from '../../entities/log';
import { AttributeConditionComponent } from '../../query/attribute-condition/attribute-condition.component';
import { LocalStorageService } from '../../services/storage.service';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { VariantConditionComponent } from 'src/app/query/variant-condition/variant-condition.component';
import { InsightListComponent } from 'src/app/processmining/insight-list/insight-list.component';
import { ProcessMapComponent } from 'src/app/processmining/process-map/processmap.component';
import { ClusterConditionComponent } from 'src/app/query/cluster-condition/cluster-condition.component';
import { QueryConvertService } from 'src/app/services/query-convert.service';

@Component({
  selector: 'app-analysis-module',
  templateUrl: './analysis.component.html',
  styleUrls: ['./analysis.component.scss']
})
export class AnalysisComponent implements OnInit {
  @ViewChild(InsightListComponent, {static: false}) private insightListComponent: InsightListComponent;
  @ViewChild(ProcessMapComponent, {static: false}) private processMapComponent: ProcessMapComponent;

  logName: string;
  context: Log;

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
