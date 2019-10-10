import { ColumnMetaData } from '../query/results/drill-down-result';

export class Log {
    logName: string;
    numTraces: number;
    numEvents: number;
    numActivities: number;
    activities: string[];
    caseAttributes: ColumnMetaData[];
    eventAttributes: ColumnMetaData[];
}
