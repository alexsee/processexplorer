import { Component, OnInit, Input } from '@angular/core';
import { ConditionComponent } from '../condition.component';
import { Log } from 'src/app/entities/log';

@Component({
  selector: 'app-attribute-condition',
  templateUrl: './attribute-condition.component.html',
  styleUrls: ['./attribute-condition.component.scss']
})
export class AttributeConditionComponent implements ConditionComponent {
  @Input() data: any;
  @Input() context: Log;
}
