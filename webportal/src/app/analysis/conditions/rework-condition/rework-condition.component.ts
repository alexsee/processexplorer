import { Component, OnInit, Input } from '@angular/core';
import { ConditionComponent } from '../../condition.component';
import { QueryService } from '../../shared/query.service';
import { EventLogStatistics } from 'src/app/log/models/eventlog-statistics.model';
import { ConditionSingleComponent } from '../../condition-single/condition-single.component';

@Component({
  selector: 'app-rework-condition',
  templateUrl: './rework-condition.component.html',
  styleUrls: ['./rework-condition.component.scss']
})
export class ReworkConditionComponent implements OnInit, ConditionComponent {
  @Input() public data: any;
  @Input() public context: EventLogStatistics;
  @Input() public parent: ConditionSingleComponent;

  constructor() { }

  ngOnInit() {
  }

  doDelete() {
    this.parent.delete.emit(this.data);
  }
}
