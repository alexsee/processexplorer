import { Component, OnInit, Input } from '@angular/core';
import { Log } from 'src/app/entities/log';
import { ConditionComponent } from '../condition.component';

@Component({
  selector: 'app-cluster-condition',
  templateUrl: './cluster-condition.component.html',
  styleUrls: ['./cluster-condition.component.scss']
})
export class ClusterConditionComponent implements ConditionComponent {
  @Input() data: any;
  @Input() context: Log;
}
