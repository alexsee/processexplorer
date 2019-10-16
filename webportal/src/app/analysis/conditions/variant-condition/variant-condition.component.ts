import { Component, OnInit, Input } from '@angular/core';
import { ConditionComponent } from '../../condition.component';
import { EventLogStatistics } from 'src/app/log/models/eventlog-statistics.model';

@Component({
  selector: 'app-variant-condition',
  templateUrl: './variant-condition.component.html',
  styleUrls: ['./variant-condition.component.scss']
})
export class VariantConditionComponent implements ConditionComponent {
  @Input() data: any;
  @Input() context: EventLogStatistics;
}
