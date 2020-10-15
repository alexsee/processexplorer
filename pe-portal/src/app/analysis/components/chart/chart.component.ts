import { Component, OnInit, ElementRef, ViewChild, Input, TemplateRef } from '@angular/core';
import { EventLogStatistics } from 'src/app/log/models/eventlog-statistics.model';
import { Condition } from '../../models/condition.model';
import * as Highcharts from 'highcharts';
import { QueryService } from '../../shared/query.service';
import { QueryConvertService } from '../../shared/query-convert.service';
import { WidgetComponent } from '../widget.component';
import { Widget } from '../../models/widget.model';
import { WidgetHostComponent } from '../widget/widget-host.component';
import { SensitivityResult } from '../../models/sensitivity-result.model';

@Component({
  selector: 'app-widget-chart',
  templateUrl: './chart.component.html',
  styleUrls: ['./chart.component.scss']
})
export class WidgetChartComponent implements OnInit, WidgetComponent {
  @ViewChild('chart', {static: true}) public chartContainer: ElementRef;
  @ViewChild('optionsTemplate', {static: true}) public optionsTemplate: TemplateRef<any>;
  @ViewChild('sensitivityTemplate', {static: true}) public sensitivityTemplate: TemplateRef<any>;

  // input parameters
  @Input() public context: EventLogStatistics;
  @Input() public conditions: Condition[];
  @Input() public widget: Widget;
  @Input() public parent: WidgetHostComponent;

  // internal chart variables
  public Highcharts: typeof Highcharts = Highcharts;
  public chartOptions: Highcharts.Options;
  public updateFromInput = false;
  public empty = true;

  public selectedMeasure: any;

  private chart: Highcharts.Chart;

  // sensitivity analysis
  public loadSensitivities = false;
  public sensitivities: Map<string, SensitivityResult>;

  constructor(
    private queryService: QueryService,
    private queryConvertService: QueryConvertService
  ) { }

  updateChartInstance(chart: Highcharts.Chart) {
    this.chart = chart;
  }

  ngOnInit(): void {
  }

  doResize(): void {
    if (this.chart) {
      this.chart.reflow();
    }
  }

  doUpdate(): void {
    const selections = [];

    // push dimensions
    this.widget.options.dimensions.forEach(dim => selections.push({
      type: 'case_attribute',
      attributeName: dim.attributeName
    }));

    // push measures
    this.widget.options.measures.forEach(measure => selections.push(measure));

    this.queryService.getDrillDown(this.context.logName, selections, this.queryConvertService.convertToQuery(this.conditions))
    .subscribe(result => {
      const series = [];
      const yAxis = [];

      const labCols = [];
      const labels = [];

      // empty?
      if (result.data.length === 0) {
        this.empty = true;
        return;
      }

      // fetch non-grouped columns
      for (let i = 0; i < selections.length; i++) {
        const column = result.metaData[i];

        if (column.group) {
          const measure = this.widget.options.measures[i - this.widget.options.dimensions.length];

          // create new series
          const dataset = [];
          result.data.forEach(data => {
            dataset.push(data[result.metaData.indexOf(column)]);
          });

          const serie = {
            data: dataset,
            name: measure.title ? measure.title : column.columnName,
            type: measure.chartType ? measure.chartType : this.widget.options.options.type,
            showInLegend: true,
            yAxis: measure.yAxis ? (measure.yAxis === 'primary' ? 0 : 1) : 0
          };

          series.push(serie);
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
          text: this.widget.options.options.title
        },
        chart: {
          type: this.widget.options.options.type,
        },
        series,
        xAxis: {
          categories: labels
        },
        yAxis
      };
      this.empty = false;
      this.updateFromInput = true;
    }, err => {
      this.empty = true;
    });
  }

  doAddMeasure(): void {
    this.widget.options.measures.push({});
  }

  doDeleteMeasure(measure): void {
    this.widget.options.measures.splice(this.widget.options.measures.indexOf(measure), 1);
  }

  doAddDimension(): void {
    this.widget.options.dimensions.push({});
  }

  doDeleteDimension(dimension): void {
    this.widget.options.dimensions.splice(this.widget.options.dimensions.indexOf(dimension), 1);
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

  getSensitivityTemplate(): TemplateRef<any> {
    this.loadSensitivities = true;

    const selections = [];

    // push dimensions
    this.widget.options.dimensions.forEach(dim => selections.push({
      type: 'case_attribute',
      attributeName: dim.attributeName
    }));

    // push measures
    this.widget.options.measures.forEach(measure => selections.push(measure));

    this.queryService.getSensitivity(this.context.logName, selections, this.queryConvertService.convertToQuery(this.conditions))
    .subscribe(result => {
      this.sensitivities = result;
      this.loadSensitivities = false;
    });

    return this.sensitivityTemplate;
  }

  isNaN(value): boolean {
    return value === 'NaN';
  }

}
