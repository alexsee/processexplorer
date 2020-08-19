import { Component, OnInit } from '@angular/core';
import { PredictionService } from '../prediction.service';
import { ActivatedRoute } from '@angular/router';
import { OpenCase } from '../shared/open-case.model';
import { LogService } from 'src/app/log/shared/log.service';
import { QueryService } from 'src/app/analysis/shared/query.service';
import { Case } from 'src/app/analysis/models/case.model';

@Component({
  selector: 'app-open-cases-list',
  templateUrl: './open-cases-list.component.html',
  styleUrls: ['./open-cases-list.component.scss']
})
export class OpenCasesListComponent implements OnInit {
  public logName: string;
  public openCases: OpenCase[];
  public case: Case;

  constructor(private logService: LogService,
              private predictionService: PredictionService,
              private queryService: QueryService) { }

  ngOnInit(): void {
    this.logService.currentLog.subscribe(eventLog => {
      if (eventLog === null) {
        return;
      }

      this.logName = eventLog.logName;
      this.loadList();
    });
  }

  loadList(): void {
    this.predictionService.getOpenCases(this.logName).subscribe(cases => {
      this.openCases = cases;
    });
  }

  showSingleCase(caseId: number) {
    this.queryService.getSingleCase(this.logName, caseId).subscribe(c => this.case = c);
  }

}
