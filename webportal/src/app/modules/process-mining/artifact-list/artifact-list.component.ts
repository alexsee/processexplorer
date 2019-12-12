import { Component, OnInit } from '@angular/core';
import { EventLog } from 'src/app/log/models/eventlog.model';
import { LogService } from 'src/app/log/shared/log.service';

@Component({
  selector: 'app-artifact-list',
  templateUrl: './artifact-list.component.html',
  styleUrls: ['./artifact-list.component.scss']
})
export class ArtifactListComponent implements OnInit {
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
