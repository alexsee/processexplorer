import { Component, OnInit, Input, ViewChild, ComponentFactoryResolver, OnChanges } from '@angular/core';
import { Condition } from 'src/app/entities/conditions/condition';
import { Log } from 'src/app/entities/log';

@Component({
  selector: 'app-condition-list',
  template: `
              <div class="condition-query-list">
                <div class="condition-query-item" *ngFor="let condition of conditions">
                  <app-condition-single
                    [condition]="condition"
                    [context]="context">
                  </app-condition-single>
                  <button mat-icon-button color="warn" (click)="onDelete(condition)"><mat-icon>delete</mat-icon></button>
                </div>
              </div>
            `
})
export class ConditionListComponent implements OnInit {
  @Input() conditions: Condition[];
  @Input() context: Log;

  constructor(private componentFactoryResolver: ComponentFactoryResolver) { }

  ngOnInit() {
  }

  onDelete(condition: Condition) {
    this.conditions.splice(this.conditions.indexOf(condition), 1);
  }
}