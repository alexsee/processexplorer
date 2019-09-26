import { Type } from '@angular/core';

export class Condition {
    constructor(public component: Type<any>, public data: any) { }

    toQuery(): string {
        return '';
    }
}
