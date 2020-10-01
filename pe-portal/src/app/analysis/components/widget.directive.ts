import { Directive, ViewContainerRef } from '@angular/core';

@Directive({
  selector: '[widget-host]'
})
export class WidgetDirective {

  constructor(public viewContainerRef: ViewContainerRef) { }

}
