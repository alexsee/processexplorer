import { Component, OnInit } from '@angular/core';
import { switchMap } from 'rxjs/operators';

import { QueryService } from '../../services/query.service';
import { ProcessMap } from '../../entities/processmap';
import { Condition } from '../../entities/conditions/condition';
import { PathConditionComponent } from '../../query/pathcondition/pathcondition.component';
import { Log } from '../../entities/log';
import { AttributeConditionComponent } from '../../query/attribute-condition/attribute-condition.component';
import { LocalStorageService } from '../../services/storage.service';
import { ActivatedRoute, ParamMap } from '@angular/router';

@Component({
  selector: 'app-analysis-module',
  templateUrl: './analysis.component.html',
  styleUrls: ['./analysis.component.scss']
})
export class AnalysisComponent implements OnInit {
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
    this.conditions = this.convertFromQuery(query);

    // update view components
    this.onUpdate();
  }

  onUpdate() {
    // store queries to local storage
    this.storageService.writeQueryConditions(this.logName, this.convertToQuery(this.conditions));
    const query = this.convertToQuery(this.conditions);

    // query process map
    this.queryService.getProcessMap(this.logName, query)
      .subscribe(processMap => this.processMap = processMap);
  }

  onAddCondition(conditionType: string) {
    switch (conditionType) {
      case 'path':
        this.conditions.push(new Condition(PathConditionComponent, { }));
        break;
      case 'attribute':
        this.conditions.push(new Condition(AttributeConditionComponent, { }));
        break;
    }
  }

  convertToQuery(conditions: Condition[]) {
    const query = [];

    for (const cond of conditions) {
      if (cond.component === PathConditionComponent) {
        query.push({ type: 'path', ...cond.data});
      } else if (cond.component === AttributeConditionComponent) {
        query.push({ type: 'attribute', ...cond.data });
      }
    }

    return query;
  }

  convertFromQuery(query: any[]): Condition[] {
    const conditions = [];

    if (!query) {
      return conditions;
    }

    for (const qry of query) {
      if (qry.type === 'path') {
        conditions.push(new Condition(PathConditionComponent, qry));
      } else if (qry.type === 'attribute') {
        conditions.push(new Condition(AttributeConditionComponent, qry));
      }
    }

    return conditions;
  }
}
