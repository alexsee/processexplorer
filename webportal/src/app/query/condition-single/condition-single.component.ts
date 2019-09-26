import { Component, OnInit, Input, ViewChild, ComponentFactoryResolver, OnChanges } from '@angular/core';
import { ConditionDirective } from '../condition.directive';
import { Condition } from 'src/app/entities/conditions/condition';
import { Log } from 'src/app/entities/log';
import { ConditionComponent } from '../condition.component';

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
  @Input() context: Log;
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
