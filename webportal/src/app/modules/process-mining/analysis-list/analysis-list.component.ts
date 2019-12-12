import { Component, OnInit } from '@angular/core';
import { EventLog } from 'src/app/log/models/eventlog.model';
import { LogService } from 'src/app/log/shared/log.service';

@Component({
  selector: 'app-analysis-list',
  templateUrl: './analysis-list.component.html',
  styleUrls: ['./analysis-list.component.scss']
})
export class AnalysisListComponent implements OnInit {
  public logs: EventLog[];
  public displayedColumns = ['logName', 'creationDate', 'status'];

  constructor(
    private logService: LogService
  ) { }

  ngOnInit() {
    this.loadList();
  }

  loadList() {
    this.logService.list().subscribe(x => this.logs = x);
  }
}
