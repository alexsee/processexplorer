import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { LogService } from 'src/app/log/shared/log.service';
import { PredictionService } from '../prediction.service';
import { AutomationJob } from '../shared/automation-job.model';

@Component({
  selector: 'app-job-list',
  templateUrl: './job-list.component.html',
  styleUrls: ['./job-list.component.scss']
})
export class JobListComponent implements OnInit {
  public logName: string;
  public automationJobs: AutomationJob[];

  constructor(private logService: LogService,
              private predictionService: PredictionService) { }

  ngOnInit(): void {
    // load open cases for current log
    this.logService.currentLog.subscribe(eventLog => {
      this.logName = eventLog.logName;
      this.loadList();
    });
  }

  loadList(): void {
    this.predictionService.getAllAutomationJobs(this.logName).subscribe(jobs => {
      this.automationJobs = jobs;
    });
  }

}
