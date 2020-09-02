import { Component, OnInit, Input, ViewChild, ComponentFactoryResolver, OnChanges } from '@angular/core';
import { Condition } from '../models/condition.model';
import { EventLogStatistics } from 'src/app/log/models/eventlog-statistics.model';
import { PathConditionComponent } from '../conditions/path-condition/path-condition.component';
import { ResourcePathConditionComponent } from '../conditions/resource-path-condition/resource-path-condition.component';
import { AttributeConditionComponent } from '../conditions/attribute-condition/attribute-condition.component';
import { VariantConditionComponent } from '../conditions/variant-condition/variant-condition.component';
import { ClusterConditionComponent } from '../conditions/cluster-condition/cluster-condition.component';
import { ReworkConditionComponent } from '../conditions/rework-condition/rework-condition.component';
import { DurationConditionComponent } from '../conditions/duration-condition/duration-condition.component';

@Component({
  selector: 'app-condition-list',
  template: `
              <div class="condition-query-list">
                <div class="condition-query-item" *ngFor="let condition of conditions">
                  <app-condition-single
                    [condition]="condition"
                    [context]="context"
                    (delete)="onDelete(condition)">
                  </app-condition-single>
                </div>
                <div class="filter-buttons">
                  <button nz-button nz-dropdown [nzDropdownMenu]="menu">Add Selection</button>
                  <nz-dropdown-menu #menu="nzDropdownMenu">
                    <ul nz-menu>
                      <li nz-menu-item (click)="onAddCondition('attribute')">Attribute selection</li>
                      <li nz-menu-item (click)="onAddCondition('path')">Process flow selection</li>
                      <li nz-menu-item (click)="onAddCondition('resourcepath')">Resource flow selection</li>
                      <li nz-menu-item (click)="onAddCondition('variant')">Variant selection</li>
                      <li nz-menu-item (click)="onAddCondition('cluster')">Cluster selection</li>
                      <li nz-menu-item (click)="onAddCondition('rework')">Rework selection</li>
                      <li nz-menu-item (click)="onAddCondition('duration')">Duration selection</li>
                    </ul>
                  </nz-dropdown-menu>
                </div>
              </div>
            `,
  styleUrls: ['./condition-list.component.scss']
})
export class ConditionListComponent implements OnInit {
  @Input() conditions: Condition[];
  @Input() context: EventLogStatistics;

  constructor(private componentFactoryResolver: ComponentFactoryResolver) { }

  ngOnInit() {
  }

  onDelete(condition: Condition) {
    setTimeout(x => this.conditions.splice(this.conditions.indexOf(condition), 1), 1);
  }

  onAddCondition(conditionType: string) {
    switch (conditionType) {
      case 'path':
        this.conditions.push(new Condition(PathConditionComponent, { }));
        break;
      case 'resourcepath':
        this.conditions.push(new Condition(ResourcePathConditionComponent, { }));
        break;
      case 'attribute':
        this.conditions.push(new Condition(AttributeConditionComponent, { }));
        break;
      case 'variant':
        this.conditions.push(new Condition(VariantConditionComponent, { }));
        break;
      case 'cluster':
        this.conditions.push(new Condition(ClusterConditionComponent, { }));
        break;
      case 'rework':
        this.conditions.push(new Condition(ReworkConditionComponent, { }));
        break;
      case 'duration':
        this.conditions.push(new Condition(DurationConditionComponent, { unit: 'days' }));
        break;
    }
  }
}
