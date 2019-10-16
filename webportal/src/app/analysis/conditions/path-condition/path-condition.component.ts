import { Component, Input } from '@angular/core';
import { ConditionComponent } from '../../condition.component';
import { EventLogStatistics } from 'src/app/log/models/eventlog-statistics.model';

@Component({
  selector: 'app-path-condition',
  templateUrl: './path-condition.component.html',
  styleUrls: ['./path-condition.component.scss']
})
export class PathConditionComponent implements ConditionComponent {
  @Input() data: any;
  @Input() context: EventLogStatistics;
}
