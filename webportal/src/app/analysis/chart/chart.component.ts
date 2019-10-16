import { Component, OnInit, Input, OnChanges, ViewChild, ElementRef } from '@angular/core';

import { QueryService } from '../shared/query.service';
import { ChartComponentOptions } from '../models/chart-options.model';
import { Condition } from '../models/condition.model';
import { DrillDownResult } from '../models/results/drill-down-result.model';
import { EventLogStatistics } from 'src/app/log/models/eventlog-statistics.model';

import * as c3 from 'c3';

@Component({
  selector: 'app-chart',
  templateUrl: './chart.component.html',
  styleUrls: ['./chart.component.scss']
})
export class ChartComponent implements OnInit, OnChanges {
  @Input() options: ChartComponentOptions;

  @Input() context: EventLogStatistics;
  @Input() conditions: Condition[];

  @ViewChild('chart', {static: true}) private chartContainer: ElementRef;

  private chartData: any;
  private chartLabels: any;

  constructor(private queryService: QueryService) { }

  ngOnInit() {
  }

  ngOnChanges() {
    if (!this.context || !this.options) {
      return;
    }

    this.generateChart();
  }

  generateChart() {
    const selections = [...this.options.dimensions, ...this.options.kpis];

    this.queryService.getDrillDown(this.context.logName, selections, this.conditions)
      .subscribe(result => this.updateChart(result));
  }

  updateChart(drillDown: DrillDownResult) {
    // prepare x-axis
    const xAxis = [];
    for (const axis of this.options.axis0) {
      if (xAxis[0] === undefined) {
        xAxis.push(axis.text);
      } else {
        xAxis[0] += ', ' + axis.text;
      }

      const idx = drillDown.metaData.map(x => x.alias).indexOf(axis.alias);
      let j = 1;
      for (const data of drillDown.data) {
        if (xAxis[j] === undefined) {
          xAxis.push(data[idx]);
        } else {
          xAxis[j] += ', ' + data[idx];
        }
        j++;
      }
    }

    // prepare y-axis
    const yAxes = [];
    for (const axis of this.options.axis2) {
      const yAxis = [];
      yAxis.push(axis.text);

      const idx = drillDown.metaData.map(x => x.alias).indexOf(axis.alias);
      for (const data of drillDown.data) {
        yAxis.push(data[idx]);
      }

      yAxes.push(yAxis);
    }

    // set config
    const config = {
      bindto: this.chartContainer.nativeElement,
      data: {
        x: xAxis[0],
        columns: [xAxis, ...yAxes],
        type: this.options.type,
        axes: {}
      },
      axis: {
          x: this.options.x,
          y: this.options.y,
          y2: this.options.y2
      },
      legend: {
        show: this.options.legendShow,
        position: this.options.legendPosition
      },
      tooltip: {
        show: this.options.tooltipsShow
      }
    };

    for (const axis of this.options.axis2) {
      config.data.axes[axis.text] = (axis.secondaryAxis === undefined || axis.secondaryAxis.valueOf() === false) ? 'y' : 'y2';
    }

    // generate chart in container
    c3.generate(config);
  }
}
