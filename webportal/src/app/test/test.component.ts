import {Component, OnInit} from '@angular/core';

import {QueryService} from '../services/query.service';
import {ProcessMap} from '../entities/processmap';
import {PathCondition, PathConditionType} from '../entities/conditions/pathcondition';

@Component({
  selector: 'app-test',
  templateUrl: './test.component.html',
  styleUrls: ['./test.component.scss']
})
export class TestComponent implements OnInit {
  processMap: ProcessMap = {edges: []};
  displayedColumns: string[] = ['sourceEvent', 'targetEvent', 'occurrence'];

  constructor(
    private queryService: QueryService
  ) {
  }

  ngOnInit() {
    var conditions = [
      new PathCondition(PathConditionType.StartEnd, '13', '14')
    ];

    this.queryService.getProcessMap('bpi2019', conditions)
      .subscribe(processMap => this.processMap = processMap);
  }

}
