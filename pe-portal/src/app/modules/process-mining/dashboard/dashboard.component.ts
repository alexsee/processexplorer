import { Component, OnInit, ViewChild, ElementRef, TemplateRef, QueryList, ViewChildren, ViewContainerRef } from '@angular/core';
import { LogService } from 'src/app/log/shared/log.service';
import { QueryService } from 'src/app/analysis/shared/query.service';
import { QueryConvertService } from 'src/app/analysis/shared/query-convert.service';
import { LocalStorageService } from 'src/app/shared/storage.service';
import { EventLogStatistics } from 'src/app/log/models/eventlog-statistics.model';
import { Condition } from 'src/app/analysis/models/condition.model';
import { WidgetChartComponent } from 'src/app/analysis/components/chart/chart.component';
import { GridsterConfig, GridsterItem, GridType, CompactType } from 'angular-gridster2';
import { WidgetHostComponent } from 'src/app/analysis/components/widget/widget-host.component';
import { DashboardService } from 'src/app/analysis/shared/dashboard.service';
import { EventLogDashboard } from 'src/app/log/models/eventlog-dashboard.model';
import { WidgetProcessMapComponent } from 'src/app/analysis/components/process-map/process-map.component';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  @ViewChildren('widget', { read: WidgetHostComponent }) public chartContainer: QueryList<WidgetHostComponent>;

  public logName: string;
  public context: EventLogStatistics;
  public statistics: EventLogStatistics;

  public selections: any[] = [];
  public conditions: Condition[] = [];

  public gridOptions: GridsterConfig;
  public dashboard: Array<GridsterItem>;

  public optionsTemplate: TemplateRef<any>;
  public editMode = false;

  private dashboardOptions: EventLogDashboard;

  constructor(
    private logService: LogService,
    private queryService: QueryService,
    private queryConvertService: QueryConvertService,
    private dashboardService: DashboardService,
    private storageService: LocalStorageService) { }

  ngOnInit(): void {
    const that = this;

    this.gridOptions = {
      gridType: GridType.Fit,
      compactType: CompactType.None,
      margin: 10,
      draggable: {
        enabled: false,
      },
      resizable: {
        enabled: false,
      },
      itemResizeCallback: (item, itemComponent) => {
        if (that.chartContainer) {
          setTimeout(x => {
            that.chartContainer.forEach(chart => chart.doResize());
          }, 100);
        }
      },
    };

    // load context
    this.logService.currentLog.subscribe(eventLog => {
      if (eventLog === null) {
        return;
      }

      this.logName = eventLog.logName;

      // load queries from local storage
      if (window.history.state !== undefined && window.history.state.conditions !== undefined) {
        this.conditions = this.queryConvertService.convertFromQuery(window.history.state.conditions);
      } else {
        const query = this.storageService.readQueryConditions(this.logName);
        this.conditions = this.queryConvertService.convertFromQuery(query);
      }

      this.queryService.getStatistics(this.logName, this.queryConvertService.convertToQuery(this.conditions))
        .subscribe(statistics => {
          this.context = statistics;
          this.statistics = statistics;
        });

      // load dashboard pages
      this.dashboard = [];

      this.dashboardService.getDashboards(this.logName).subscribe(x => {
        // load first
        if (x.length === 0) {
          this.dashboardOptions = {
            id: 0,
            content: null,
            creationDate: new Date(),
            modifiedDate: new Date(),
            logName: this.logName,
            page: 1
          };
          return;
        }

        this.dashboardService.getDashboard(x[0]).subscribe(dashboard => {
          this.dashboardOptions = dashboard;
          this.dashboard = this.dashboardService.parseDashboard(dashboard.content);

          this.chartContainer.forEach(chart => chart.doUpdate());
        });
      });
    });
  }

  doEdit(item): void {
    this.optionsTemplate = this.chartContainer.toArray()[item].getOptionsTemplate();
    this.gridOptions.api.resize();
  }

  doDelete(item): void {
    this.dashboard.splice(this.dashboard.indexOf(item), 1);
    this.gridOptions.api.resize();
  }

  doSensitivityAnalysis(item): void {
    this.optionsTemplate = this.chartContainer.toArray()[item].getSensitivityTemplate();
    this.gridOptions.api.resize();
  }

  doEditModeChange(): void {
    this.gridOptions.api.resize();

    this.gridOptions.resizable.enabled = this.editMode;
    this.gridOptions.draggable.enabled = this.editMode;
    this.gridOptions.api.optionsChanged();
  }

  doUpdate(): void {
    this.queryService.getStatistics(this.logName, this.queryConvertService.convertToQuery(this.conditions))
      .subscribe(statistics => this.statistics = statistics);

    this.chartContainer.forEach(chart => chart.doUpdate());

    // save dashboard
    this.dashboardOptions.content = this.dashboardService.stringifyDashboard(this.dashboard);
    this.dashboardService.save(this.dashboardOptions).subscribe(x => this.dashboardOptions = x);

    // store queries to local storage
    this.storageService.writeQueryConditions(this.logName, this.queryConvertService.convertToQuery(this.conditions));
  }

  doAddWidget(type: string): void {
    if (type === 'process_map') {
      this.dashboard.push({cols: 2, rows: 1, x: 0, y: 0, widget: {
        type: WidgetProcessMapComponent, options: { options: {}, dimensions: [], measures: []}}
      });
    } else if (type === 'chart') {
      this.dashboard.push({cols: 2, rows: 1, x: 0, y: 0, widget: {
        type: WidgetChartComponent, options: { options: {}, dimensions: [], measures: []}}
      });
    }
  }

}
