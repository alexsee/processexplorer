import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { LogService } from 'src/app/log/shared/log.service';
import { QueryService } from 'src/app/analysis/shared/query.service';
import { QueryConvertService } from 'src/app/analysis/shared/query-convert.service';
import { LocalStorageService } from 'src/app/shared/storage.service';
import * as Highcharts from 'highcharts';
import { EventLogStatistics } from 'src/app/log/models/eventlog-statistics.model';
import { Condition } from 'src/app/analysis/models/condition.model';
import { ColumnMetaData } from 'src/app/log/models/column-meta-data.model';
import { nullSafeIsEquivalent } from '@angular/compiler/src/output/output_ast';
import { ChartComponent } from 'src/app/analysis/components/chart/chart.component';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  @ViewChild('chart', {static: true}) public chartContainer: ChartComponent;

  public logName: string;
  public context: EventLogStatistics;
  public selections: any[] = [];
  public conditions: Condition[] = [];

  public Highcharts: typeof Highcharts = Highcharts;
  public chartOptions: Highcharts.Options;
  public updateFromInput = false;

  public options: any = {};
  public dimensions: any[] = [];
  public measures: any[] = [];

  public selectedMeasure: any;

  constructor(
    private logService: LogService,
    private queryService: QueryService,
    private queryConvertService: QueryConvertService,
    private storageService: LocalStorageService) { }

  ngOnInit(): void {
    this.logService.currentLog.subscribe(eventLog => {
      if (eventLog === null) {
        return;
      }

      this.logName = eventLog.logName;
      this.queryService.getStatistics(this.logName, this.queryConvertService.convertToQuery(this.conditions))
        .subscribe(statistics => this.context = statistics);
    });
  }

  doAddMeasure(): void {
    this.measures.push({});
  }

  doDeleteMeasure(measure): void {
    this.measures.splice(this.measures.indexOf(measure), 1);
  }

  doAddDimension(): void {
    this.dimensions.push({});
  }

  doDeleteDimension(dimension): void {
    this.dimensions.splice(this.dimensions.indexOf(dimension), 1);
  }

  doOpenMeasureOptions(measure): void {
    this.selectedMeasure = measure;
  }

  doCloseMeasureOptions(): void {
    this.selectedMeasure = null;
  }

  doUpdate(): void {
    this.chartContainer.doUpdate();
  }

}
