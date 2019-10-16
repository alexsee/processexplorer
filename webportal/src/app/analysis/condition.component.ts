import { EventLogStatistics } from '../log/models/eventlog-statistics.model';
import { ConditionSingleComponent } from './condition-single/condition-single.component';

export interface ConditionComponent {
    data: any;
    context: EventLogStatistics;
    parent: ConditionSingleComponent;
}
