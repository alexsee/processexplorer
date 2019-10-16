import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { QueryService } from 'src/app/analysis/shared/query.service';
import { EventLogAnnotation } from 'src/app/log/models/eventlog-annotation.model';
import { EventLogStatistics } from 'src/app/log/models/eventlog-statistics.model';
import { LogService } from 'src/app/log/shared/log.service';

@Component({
  selector: 'app-log-annotation',
  templateUrl: './log-annotation.component.html',
  styleUrls: ['./log-annotation.component.scss']
})
export class LogAnnotationComponent implements OnInit {

  logName: string;
  context: EventLogStatistics;

  displayedColumns: string[] = ['columnType', 'columnName', 'categorization', 'actions'];

  annotations: EventLogAnnotation[];
  addAnnotation: EventLogAnnotation = { };

  codes: string[] = [
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

    this.loadAnnotations();
  }

  loadAnnotations() {
    this.logService.getAnnotations(this.logName).subscribe(annotations => this.annotations = annotations);
  }

  doAddAnnotation() {
    this.addAnnotation.logName = this.logName;

    this.logService.saveAnnotation(this.addAnnotation).subscribe(x => {
      this.addAnnotation.code = null;
      this.loadAnnotations();
    });
  }

  doDeleteAnnotation(id: number) {
    this.logService.deleteAnnotation(id).subscribe(x => this.loadAnnotations());
  }

}
