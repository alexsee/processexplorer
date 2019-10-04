import { Component, OnInit, Input } from '@angular/core';
import { ConditionComponent } from '../condition.component';
import { Log } from 'src/app/entities/log';

@Component({
  selector: 'app-path-condition',
  templateUrl: './path-condition.component.html',
  styleUrls: ['./path-condition.component.scss']
})
export class PathConditionComponent implements ConditionComponent {
  @Input() data: any;
  @Input() context: Log;
}
