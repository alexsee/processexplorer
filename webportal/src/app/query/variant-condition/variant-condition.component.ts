import { Component, OnInit, Input } from '@angular/core';
import { Log } from 'src/app/entities/log';
import { ConditionComponent } from '../condition.component';

@Component({
  selector: 'app-variant-condition',
  templateUrl: './variant-condition.component.html',
  styleUrls: ['./variant-condition.component.scss']
})
export class VariantConditionComponent implements ConditionComponent {
  @Input() data: any;
  @Input() context: Log;
}
