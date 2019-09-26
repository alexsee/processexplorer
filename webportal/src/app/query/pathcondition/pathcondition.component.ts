import { Component, OnInit, Input } from '@angular/core';
import { ConditionComponent } from '../condition.component';
import { Log } from 'src/app/entities/log';

@Component({
  selector: 'app-pathcondition',
  templateUrl: './pathcondition.component.html',
  styleUrls: ['./pathcondition.component.scss']
})
export class PathConditionComponent implements ConditionComponent {
  @Input() data: any;
  @Input() context: Log;
}
