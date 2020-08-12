import {Component, Input} from '@angular/core';

@Component({
  selector: 'app-confusion-matrix',
  template: `
    <table class="table table-sm">
      <thead class="thead-light">
      <tr>
        <th class="text-center" scope="colgroup" [colSpan]="numClasses + 1"><ng-content></ng-content></th>
      </tr>
      </thead>
      <tbody>
      <tr>
        <td></td>
        <th *ngFor="let col of matrix['Normal'] | keyvalue">{{col.key}}</th>
      </tr>
      <tr class="text-right" *ngFor="let row of matrix | keyvalue">
        <th scope="row">{{row.key}}</th>
        <td *ngFor="let item of row.value | keyvalue">{{item.value}}</td>
      </tr>
      </tbody>
    </table>
  `
})

export class ConfusionMatrixComponent {
  @Input() matrix: { [key: string]: { [key: string]: number } };
  @Input() numClasses: number;
}
