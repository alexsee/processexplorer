import { Component, OnInit, ViewChild } from '@angular/core';

import { QueryService } from '../../services/query.service';
import { ProcessMap } from '../../entities/processmap';
import { Condition } from '../../entities/conditions/condition';
import { PathConditionComponent } from '../../query/pathcondition/pathcondition.component';
import { Log } from '../../entities/log';
import { AttributeConditionComponent } from '../../query/attribute-condition/attribute-condition.component';
import { LocalStorageService } from '../../services/storage.service';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { VariantConditionComponent } from 'src/app/query/variant-condition/variant-condition.component';
import { InsightListComponent } from 'src/app/processmining/insight-list/insight-list.component';

@Component({
  selector: 'app-analysis-module',
  templateUrl: './analysis.component.html',
  styleUrls: ['./analysis.component.scss']
})
export class AnalysisComponent implements OnInit {
  @ViewChild(InsightListComponent, {static: false}) private insightListComponent: InsightListComponent;

  processMap: ProcessMap = {edges: []};

  logName: string;
  context: Log;

  conditions: Condition[];

  constructor(
    private route: ActivatedRoute,
    private queryService: QueryService,
    private storageService: LocalStorageService
  ) {
  }

  ngOnInit() {
    this.logName = this.route.snapshot.paramMap.get('logName');
    this.queryService.getStatistics(this.logName).subscribe(statistics => this.context = statistics);

    // load queries from local storage
    const query = this.storageService.readQueryConditions(this.logName);
    this.conditions = this.queryService.convertFromQuery(query);

    // update view components
    this.onUpdate();
  }

  onUpdate() {
    // store queries to local storage
    this.storageService.writeQueryConditions(this.logName, this.queryService.convertToQuery(this.conditions));
    const query = this.queryService.convertToQuery(this.conditions);

    // query process map
    this.queryService.getProcessMap(this.logName, query)
      .subscribe(processMap => this.processMap = processMap);

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
    }
  }
}
