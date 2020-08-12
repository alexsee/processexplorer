import {Component, Input} from '@angular/core';
import { EvaluationResult } from '../shared/interfaces';

@Component({
  selector: 'app-evaluation-table',
  template: `
    <table class="table table-sm" *ngIf="evaluation">
      <thead class="thead-light">
      <tr>
        <th class="text-center" scope="colgroup" colspan="2">
          <ng-content></ng-content>
        </th>
      </tr>
      </thead>
      <tbody>
      <tr class="text-right">
        <th scope="row">Precision</th>
        <td>{{evaluation[axis].precision.toFixed(4)}}</td>
      </tr>
      <tr class="text-right">
        <th scope="row">Recall</th>
        <td>{{evaluation[axis].recall.toFixed(4)}}</td>
      </tr>
      <tr class="text-right">
        <th scope="row">F1</th>
        <td>{{evaluation[axis].f1.toFixed(4)}}</td>
      </tr>
      </tbody>
    </table>
  `
})

export class EvaluationTableComponent {
  @Input() evaluation: EvaluationResult;
  @Input() axis: number;
}
