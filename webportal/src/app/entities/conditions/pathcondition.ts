import { Condition } from './condition';

export enum PathConditionType {
    Response = 'RESPONSE',
    StartEnd = 'START_END'
}

export class PathCondition extends Condition {

    constructor(conditionType: PathConditionType, start: string, end?: string) {
        super();
        this.type = 'path';

        this.conditionType = conditionType;
        this.start = start;
        this.end = end;
    }

    conditionType: PathConditionType;
    start: string;
    end: string;
}
