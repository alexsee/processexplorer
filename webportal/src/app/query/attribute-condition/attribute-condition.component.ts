import { Component, OnInit, Input } from '@angular/core';
import { ConditionComponent } from '../condition.component';
import { Log } from 'src/app/entities/log';
import { QueryService } from 'src/app/services/query.service';
import { Observable } from 'rxjs';
import { CaseAttributeValueResult } from '../results/case-attribute-value-result';

@Component({
  selector: 'app-attribute-condition',
  templateUrl: './attribute-condition.component.html',
  styleUrls: ['./attribute-condition.component.scss']
})
export class AttributeConditionComponent implements OnInit, ConditionComponent {
  @Input() data: any;
  @Input() context: Log;

  private options: CaseAttributeValueResult = {
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
    this.queryService.getCaseAttributeValues(this.context.logName, this.data.attribute, [])
      .subscribe(response => this.options = response);
  }
}
