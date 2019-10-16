import { ColumnMetaData } from './column-meta-data.model';

export class EventLogStatistics {
    logName: string;
    numTraces: number;
    numEvents: number;
    numActivities: number;
    activities: string[];
    caseAttributes: ColumnMetaData[];
    eventAttributes: ColumnMetaData[];
}
