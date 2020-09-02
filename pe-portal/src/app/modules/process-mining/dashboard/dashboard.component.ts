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

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  @ViewChild('chart', {static: true}) public chartContainer: ElementRef;

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
    this.selections = [];

    // push dimensions
    this.dimensions.forEach(dim => this.selections.push({
      type: 'case_attribute',
      attributeName: dim.attributeName
    }));

    // push measures
    this.measures.forEach(measure => this.selections.push({
      type: measure.type
    }));

    this.queryService.getDrillDown(this.logName, this.selections, this.conditions).subscribe(result => {
      const series = [];
      const yAxis = [];

      const labCols = [];
      const labels = [];

      // fetch non-grouped columns
      for (let i = 0; i < this.selections.length; i++) {
        const column = result.metaData[i];

        if (column.group) {
          const measure = this.measures[i - this.dimensions.length];

          // create new series
          const dataset = [];
          result.data.forEach(data => {
            dataset.push(data[result.metaData.indexOf(column)]);
          });

          series.push({
            data: dataset,
            name: measure.title ? measure.title : column.columnName,
            type: measure.chartType ? measure.chartType : this.options.type,
            showInLegend: true,
            yAxis: measure.yAxis ? (measure.yAxis === 'primary' ? 0 : 1) : 0
          });
        } else {
          labCols.push(column);
        }
      }

      // fetch result
      result.data.forEach(item => {
        let label = '';
        labCols.forEach(column => label += item[result.metaData.indexOf(column)]);

        labels.push(label);
      });

      // generate yAxis
      let yAxis0 = null;
      let yAxis1 = null;

      for (const serie of series) {
        if (serie.yAxis === 0 && yAxis0 === null) {
          yAxis0 = {
            title: {
              text: serie.name
            }
          };
        } else if (serie.yAxis === 0) {
          yAxis0.title.text += '|' + serie.name;
        } else if (serie.yAxis === 1 && yAxis1 === null) {
          yAxis1 = {
            title: {
              text: serie.name
            },
            opposite: true
          };
        } else if (serie.yAxis === 1) {
          yAxis1.title.text += '|' + serie.name;
        }
      }

      if (yAxis0) { yAxis.push(yAxis0); }
      if (yAxis1) { yAxis.push(yAxis1); }

      // generate chart in container
      this.chartOptions = {
        title: {
          text: this.options.title
        },
        chart: {
          type: this.options.type,
        },
        series,
        xAxis: {
          categories: labels
        },
        yAxis
      };
      this.updateFromInput = true;
    });
  }

}
