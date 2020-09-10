import { Component, OnInit, ElementRef, ViewChild, Input, TemplateRef } from '@angular/core';
import { EventLogStatistics } from 'src/app/log/models/eventlog-statistics.model';
import { Condition } from '../../models/condition.model';
import * as Highcharts from 'highcharts';
import { QueryService } from '../../shared/query.service';
import { QueryConvertService } from '../../shared/query-convert.service';

@Component({
  selector: 'app-chart',
  templateUrl: './chart.component.html',
  styleUrls: ['./chart.component.scss']
})
export class ChartComponent implements OnInit {
  @ViewChild('chart', {static: true}) public chartContainer: ElementRef;
  @ViewChild('optionsTemplate', {static: true}) public optionsTemplate: TemplateRef<any>;

  // input parameters
  @Input() public logName: string;
  @Input() public context: EventLogStatistics;
  @Input() public conditions: Condition[];

  @Input() public options: any = {};
  @Input() public dimensions: any[] = [];
  @Input() public measures: any[] = [];

  // internal chart variables
  public Highcharts: typeof Highcharts = Highcharts;
  public chartOptions: Highcharts.Options;
  public updateFromInput = false;

  public selectedMeasure: any;

  constructor(
    private queryService: QueryService,
    private queryConvertService: QueryConvertService
  ) { }

  ngOnInit(): void {
  }

  doResize(): void {
    this.updateFromInput = true;
  }

  doUpdate(): void {
    const selections = [];

    // push dimensions
    this.dimensions.forEach(dim => selections.push({
      type: 'case_attribute',
      attributeName: dim.attributeName
    }));

    // push measures
    this.measures.forEach(measure => selections.push({
      type: measure.type
    }));

    this.queryService.getDrillDown(this.logName, selections, this.queryConvertService.convertToQuery(this.conditions)).subscribe(result => {
      const series = [];
      const yAxis = [];

      const labCols = [];
      const labels = [];

      // fetch non-grouped columns
      for (let i = 0; i < selections.length; i++) {
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

  getOptionsTemplate(): TemplateRef<any> {
    return this.optionsTemplate;
  }

}
