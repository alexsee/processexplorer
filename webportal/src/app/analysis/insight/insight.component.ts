import { Component, Input, OnChanges, ViewChild, ElementRef, AfterViewInit } from '@angular/core';

import HumanizeDuration from 'humanize-duration';
import * as Highcharts from 'highcharts';

import { Insight } from '../models/insight.model';

@Component({
  selector: 'app-insight',
  templateUrl: './insight.component.html',
  styleUrls: ['./insight.component.scss']
})
export class InsightComponent implements OnChanges, AfterViewInit {
  @ViewChild('chart', {static: true}) public chartContainer: ElementRef;
  @Input() public insight: Insight;

  Highcharts: typeof Highcharts = Highcharts;
  chartOptions: Highcharts.Options = {};

  public icon: string;
  public color: string;

  constructor() { }

  ngOnChanges() {
    if (this.insight === undefined) {
      return;
    }

    // calculate the interesting chart distribution
    if (this.insight.format === 'DISTRIBUTION') {
      const dataset: any[] = [];
      const labels: string[] = [];
      let otherCount = 0;
      let c = 0;

      // zip together
      const values = [];
      for (let i = 0; i < this.insight.labels.length; i++) {
        values.push({ label: this.insight.labels[i], within: this.insight.within[i], without: this.insight.without[i] });
      }

      values.sort((a, b) => {
        return ((a.within < b.within) ? -1 : ((a.within === b.within) ? 0 : 1)) * -1;
      });

      for (const value of values) {
        const n_vt = value.within;
        const n_vNt = this.insight.within.reduce((a, b) => a + b, 0) - n_vt;
        const n_v = n_vt + n_vNt;

        const n_t = n_vt + value.without;
        const n_s = this.insight.within.reduce((a, b) => a + b, 0) + this.insight.without.reduce((a, b) => a + b, 0);

        // if (this.unusualness(n_vt, n_v, n_t, n_s) > 0 && c < 10) {
        if (c < 10) {
          if (value.within > 0) {
            dataset.push(value.within);
            labels.push(value.label === '' ? '-NULL-' : value.label);
          }
          c++;
        } else {
          otherCount += value.within;
        }
      }

      if (otherCount > 0) {
        dataset.push(otherCount);
        labels.push('-OTHER-');
      }

      // generate chart in container
      this.chartOptions = {
        title: null,
        chart: {
          type: 'column'
        },
        series: [{
          data: dataset,
          type: 'column',
          name: 'Case count',
          showInLegend: false
        }],
        xAxis: {
          categories: labels
        },
        yAxis: {
          title: {
            text: 'Case count'
          }
        }
      };
    }

    // set icon
    if (Math.abs(this.insight.effectSize) > 0.8) {
      this.icon = 'exclamation-circle';
      this.color = 'red';
    } else if (Math.abs(this.insight.effectSize) > 0.5) {
      this.icon = 'warning';
      this.color = 'orange';
    } else {
      this.icon = 'info-circle';
      this.color = 'green';
    }

    // format fix
    if (this.insight.subTitle) {
      this.insight.subTitle = this.insight.subTitle.replace('-->', '&#8594;');
    }
  }

  ngAfterViewInit() {
    // TODO: temporary fix for Highcharts
    window.dispatchEvent(new Event('resize'));
  }

  unusualness(n_vt: number, n_v: number, n_t: number, n_s: number) {
    return (n_v / n_s) * (n_vt / n_v - n_t / n_s);
  }

  getChartData(insight: Insight) {
    return [{data: insight.within, label: 'within'}, {data: insight.without, label: 'without'}];
  }

  getChartLabels(insight: Insight) {
    return insight.labels;
  }

  humanizeDuration(duration: number) {
    return HumanizeDuration(duration * 1000, { largest: 1, round: true });
  }
}
