import { Component, Input } from '@angular/core';
import { ConditionComponent } from '../../condition.component';
import { EventLogStatistics } from 'src/app/log/models/eventlog-statistics.model';
import { ConditionSingleComponent } from '../../condition-single/condition-single.component';

@Component({
  selector: 'app-duration-condition',
  templateUrl: './duration-condition.component.html',
  styleUrls: ['./duration-condition.component.scss']
})
export class DurationConditionComponent implements ConditionComponent {
  @Input() data: any;
  @Input() context: EventLogStatistics;
  @Input() parent: ConditionSingleComponent;

  doDelete() {
    this.parent.delete.emit(this.data);
  }
}
