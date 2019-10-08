import { Component, OnInit } from '@angular/core';
import { LogService } from 'src/app/services/log.service';
import { ActivatedRoute } from '@angular/router';
import { QueryService } from 'src/app/services/query.service';
import { Log } from 'src/app/entities/log';
import { EventLogAnnotation } from 'src/app/entities/eventlog-annotation';

@Component({
  selector: 'app-log-annotation',
  templateUrl: './log-annotation.component.html',
  styleUrls: ['./log-annotation.component.scss']
})
export class LogAnnotationComponent implements OnInit {

  logName: string;
  context: Log;

  annotations: Map<string, string[]>;
  annotationsDb: EventLogAnnotation[];

  options: string[] = [
    'CASES',
    'ACTIVITY_INSTANCES',
    'ACTIVITY',
    'SUBPROCESS',
    'ITEM',
    'EXTERNAL_PARTNERS',
    'ORGANIZATIONAL_ENTITIES',
    'CONTROL_FLOW',
    'CONFORMANCE',
    'EXECUTION_PATTERNS',
    'RESPONSIBILITIES',
    'ORGANIZATIONAL_HIERARCHY',
    'WORK_PRACTICES',
    'DURATIONS',
    'TIME_POINTS',
    'EXECUTION_STATUS',
    'DRIFT_SCORES'
  ];

  constructor(
    private route: ActivatedRoute,
    private logService: LogService,
    private queryService: QueryService) { }

  ngOnInit() {
    this.logName = this.route.snapshot.paramMap.get('logName');
    this.queryService.getStatistics(this.logName).subscribe(statistics => this.context = statistics);

    this.logService.getAnnotations(this.logName).subscribe(annotations => {
      this.annotationsDb = annotations;

      this.annotations = new Map();
      for (const annotation of annotations) {
        this.annotations[annotation.columnName] = annotation.annotation;
      }
    });
  }

  doSave() {
    const items = [];

    for (const attribute in this.annotations) {
      for (const value of this.annotations[attribute]) {
        const item = new EventLogAnnotation();
        item.logName = this.logName;
        item.columnName = attribute;
        item.annotation = value;
        
        items.push(item);
      }
    }

    console.info(items);

    this.logService.saveAnnotations(items);
  }

}
