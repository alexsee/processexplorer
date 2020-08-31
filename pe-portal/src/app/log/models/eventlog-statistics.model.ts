import { ColumnMetaData } from './column-meta-data.model';
import { Activity } from './activity.model';

export class EventLogStatistics {
    logName: string;
    numTraces: number;
    numEvents: number;
    numActivities: number;
    activities: Activity[];
    resources: string[];
    caseAttributes: ColumnMetaData[];
    eventAttributes: ColumnMetaData[];
}
