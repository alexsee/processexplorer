import { Component, OnInit, ViewChild, ElementRef, TemplateRef, QueryList, ViewChildren } from '@angular/core';
import { LogService } from 'src/app/log/shared/log.service';
import { QueryService } from 'src/app/analysis/shared/query.service';
import { QueryConvertService } from 'src/app/analysis/shared/query-convert.service';
import { LocalStorageService } from 'src/app/shared/storage.service';
import * as Highcharts from 'highcharts';
import { EventLogStatistics } from 'src/app/log/models/eventlog-statistics.model';
import { Condition } from 'src/app/analysis/models/condition.model';
import { ChartComponent } from 'src/app/analysis/components/chart/chart.component';
import { GridsterConfig, GridsterItem, GridType, CompactType } from 'angular-gridster2';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  @ViewChildren('chart') public chartContainer: QueryList<ChartComponent>;

  public logName: string;
  public context: EventLogStatistics;
  public selections: any[] = [];
  public conditions: Condition[] = [];

  public gridOptions: GridsterConfig;
  public dashboard: Array<GridsterItem>;

  public optionsTemplate: TemplateRef<any>;

  constructor(
    private logService: LogService,
    private queryService: QueryService,
    private queryConvertService: QueryConvertService,
    private storageService: LocalStorageService) { }

  itemChange(item, itemComponent) {
    console.info('itemChanged', item, itemComponent);
  }

  itemResize(item, itemComponent) {
    console.info('itemResized', item, itemComponent);

    if (this.chartContainer) {
      this.chartContainer.forEach(chart => chart.doResize());
    }
  }

  ngOnInit(): void {
    this.gridOptions = {
      gridType: GridType.Fit,
      compactType: CompactType.None,
      margin: 10,
      draggable: {
        enabled: true,
      },
      resizable: {
        enabled: true,
      },
      itemChangeCallback: this.itemChange,
      itemResizeCallback: this.itemResize,
    };

    this.dashboard = [
      {cols: 2, rows: 1, y: 0, x: 0, options: {}, dimensions: [], measures: []},
      {cols: 2, rows: 2, y: 0, x: 2, options: {}, dimensions: [], measures: []}
    ];

    this.logService.currentLog.subscribe(eventLog => {
      if (eventLog === null) {
        return;
      }

      this.logName = eventLog.logName;
      this.queryService.getStatistics(this.logName, this.queryConvertService.convertToQuery(this.conditions))
        .subscribe(statistics => this.context = statistics);
    });
  }

  doEdit(item): void {
    this.optionsTemplate = this.chartContainer.toArray()[item].getOptionsTemplate();
  }

  doUpdate(): void {
    this.chartContainer.forEach(chart => chart.doUpdate());
  }

}
