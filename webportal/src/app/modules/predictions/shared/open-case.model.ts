export class OpenCase {
    caseId: number;
    timestampStart: Date;
    timestampEnd;
    numEvents: number;
    numResources: number;
    assignedTo: string;
    state: number;
    currentActivity: string;
    currentResource: string;
    nextActivity: string;
    nextResource: string;
    attributes: string;
}
