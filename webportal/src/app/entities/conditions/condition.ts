import { Type } from '@angular/core';
import { ConditionComponent } from 'src/app/query/condition.component';

export class Condition {
    constructor(public component: Type<ConditionComponent>, public data: any) { }

    toQuery(): string {
        return '';
    }
}
