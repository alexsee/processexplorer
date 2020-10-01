import { Component, ComponentFactoryResolver, ComponentRef, Input, OnChanges, OnInit, TemplateRef, ViewChild } from '@angular/core';

import { EventLogStatistics } from 'src/app/log/models/eventlog-statistics.model';
import { Condition } from '../../models/condition.model';
import { Widget } from '../../models/widget.model';
import { WidgetComponent } from '../widget.component';
import { WidgetDirective } from '../widget.directive';

@Component({
  selector: 'app-widget-host',
  template: `
    <ng-template widget-host></ng-template>
  `
})
export class WidgetHostComponent implements OnInit, OnChanges {
  @Input() conditions: Condition[];
  @Input() context: EventLogStatistics;
  @Input() widget: Widget;

  @ViewChild(WidgetDirective, {static: true}) widgetHost: WidgetDirective;

  private firstTime = true;
  private componentRef: ComponentRef<any>;

  constructor(private componentFactoryResolver: ComponentFactoryResolver) { }

  ngOnInit(): void {
    if (this.context && this.conditions && this.widget) {
      this.loadWidget();
    }
  }

  ngOnChanges(): void {
    if (this.context && this.conditions && this.widget) {
      this.loadWidget();
    }
  }

  loadWidget() {
    const componentFactory = this.componentFactoryResolver.resolveComponentFactory(this.widget.type);

    const viewContainerRef = this.widgetHost.viewContainerRef;
    viewContainerRef.clear();

    this.componentRef = viewContainerRef.createComponent(componentFactory);
    (this.componentRef.instance as WidgetComponent).context = this.context;
    (this.componentRef.instance as WidgetComponent).conditions = this.conditions;
    (this.componentRef.instance as WidgetComponent).widget = this.widget;
    (this.componentRef.instance as WidgetComponent).parent = this;
  }

  doResize(): void {
    if (this.componentRef) {
      (this.componentRef.instance as WidgetComponent).doResize();

      if (this.firstTime) {
        this.firstTime = false;
        this.doUpdate();
      }
    }
  }

  doUpdate(): void {
    if (this.componentRef) {
      (this.componentRef.instance as WidgetComponent).doUpdate();
    }
  }

  getOptionsTemplate(): TemplateRef<any> {
    return (this.componentRef.instance as WidgetComponent).getOptionsTemplate();
  }

}
