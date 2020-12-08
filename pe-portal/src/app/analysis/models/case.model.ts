import { Activity } from 'src/app/log/models/activity.model';

export class Case {
    caseId: number;
    originalCaseId: string;
    timestampStart: Date;
    timestampEnd: Date;
    numEvents: number;
    numResources: number;
    variantId: number;
    events: Event[];
    state: number;
}

export class Event {
    activity: Activity;
    timestamp: Date;
    resource: string;
}
