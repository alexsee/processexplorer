import { Component, OnInit, Input } from '@angular/core';
import { ConditionComponent } from '../../condition.component';
import { CaseAttributeValueResult } from '../../models/results/case-attribute-value-result.model';
import { QueryService } from '../../shared/query.service';
import { EventLogStatistics } from 'src/app/log/models/eventlog-statistics.model';

@Component({
  selector: 'app-attribute-condition',
  templateUrl: './attribute-condition.component.html',
  styleUrls: ['./attribute-condition.component.scss']
})
export class AttributeConditionComponent implements OnInit, ConditionComponent {
  @Input() public data: any;
  @Input() public context: EventLogStatistics;

  public options: CaseAttributeValueResult = {
    attributeName: '',
    categorical: false,
    values: []
  };

  constructor(
    private queryService: QueryService
  ) { }

  ngOnInit() {
    this.onSelectionChange();
  }

  onSelectionChange() {
    if (this.data.attribute) {
      this.queryService.getCaseAttributeValues(this.context.logName, this.data.attribute, [])
        .subscribe(response => this.options = response);
    }
  }
}
