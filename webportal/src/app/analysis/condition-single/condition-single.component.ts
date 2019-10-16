import { Component, OnInit, Input, ViewChild, ComponentFactoryResolver, OnChanges } from '@angular/core';
import { Condition } from '../models/condition.model';
import { ConditionDirective } from '../condition.directive';
import { ConditionComponent } from '../condition.component';
import { EventLogStatistics } from 'src/app/log/models/eventlog-statistics.model';

@Component({
  selector: 'app-condition-single',
  template: `
    <div class="condition-query">
      <ng-template condition-host></ng-template>
    </div>
  `
})
export class ConditionSingleComponent implements OnInit, OnChanges {
  @Input() condition: Condition;
  @Input() context: EventLogStatistics;
  @ViewChild(ConditionDirective, {static: true}) conditionHost: ConditionDirective;

  constructor(private componentFactoryResolver: ComponentFactoryResolver) { }

  ngOnInit() {
    if (this.condition && this.context) {
      this.loadComponent();
    }
  }

  ngOnChanges() {
    if (this.condition && this.context) {
      this.loadComponent();
    }
  }

  loadComponent() {
    const componentFactory = this.componentFactoryResolver.resolveComponentFactory(this.condition.component);

    const viewContainerRef = this.conditionHost.viewContainerRef;
    viewContainerRef.clear();

    const componentRef = viewContainerRef.createComponent(componentFactory);
    (componentRef.instance as ConditionComponent).data = this.condition.data;
    (componentRef.instance as ConditionComponent).context = this.context;
  }

}
