import { Directive, ViewContainerRef } from '@angular/core';

@Directive({
    selector: '[condition-host]'
})
export class ConditionDirective {
    constructor(public viewContainerRef: ViewContainerRef) { }
}
