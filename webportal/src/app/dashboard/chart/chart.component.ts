import { Component, OnInit, Input, OnChanges, ViewChild, ElementRef } from '@angular/core';
import { ChartComponentOptions } from './chart-component-options';
import { Condition } from 'src/app/entities/conditions/condition';
import { Log } from 'src/app/entities/log';
// import { ChartOptions } from 'chart.js';
import { QueryService } from 'src/app/services/query.service';
import { DrillDownResult } from 'src/app/query/results/drill-down-result';

import * as c3 from 'c3';

@Component({
  selector: 'app-chart',
  templateUrl: './chart.component.html',
  styleUrls: ['./chart.component.scss']
})
export class ChartComponent implements OnInit, OnChanges {
  @Input() options: ChartComponentOptions;

  @Input() context: Log;
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
    // build data set
    const dataset = [];

    for (let i = 0; i < drillDown.metaData.length; i++) {
      const d = [];
      d.push(drillDown.metaData[i].columnName);

      for (const data of drillDown.data) {
        d.push(data[i]);
      }

      dataset.push(d);
    }

    console.info(dataset);

    // generate chart in container
    c3.generate({
      bindto: this.chartContainer.nativeElement,
      data: {
        x: '"Item Type"',
        columns: dataset,
        type: 'bar',
        axes: {
          '"Count cases"': 'y',
          '"Count variants"': 'y2'
        }
      },
      axis: {
          x: {
              type: 'category' // this needed to load string x value
          },
          y2: {
            show: true
          }
      }
    });
  }
}
