import { Component, Input } from '@angular/core';
import { ConditionComponent } from '../../condition.component';
import { EventLogStatistics } from 'src/app/log/models/eventlog-statistics.model';
import { ConditionSingleComponent } from '../../condition-single/condition-single.component';

@Component({
  selector: 'app-variant-condition',
  templateUrl: './variant-condition.component.html',
  styleUrls: ['./variant-condition.component.scss']
})
export class VariantConditionComponent implements ConditionComponent {
  @Input() data: any;
  @Input() context: EventLogStatistics;
  @Input() parent: ConditionSingleComponent;

  doDelete() {
    this.parent.delete.emit(this.data);
  }
}
