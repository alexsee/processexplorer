import { Component, OnInit, Input, OnChanges, OnDestroy, ViewChild, ElementRef } from '@angular/core';
import { Insight } from 'src/app/entities/insight';

import * as moment from 'moment';
import * as c3 from 'c3';

@Component({
  selector: 'app-insight',
  templateUrl: './insight.component.html',
  styleUrls: ['./insight.component.scss']
})
export class InsightComponent implements OnChanges {
  @ViewChild('chart', {static: true}) public chartContainer: ElementRef;
  @Input() public insight: Insight;

  public icon: string;
  public color: string;

  constructor() { }

  ngOnChanges() {
    if (this.insight === undefined) {
      return;
    }

    // calculate the interesting chart distribution
    if (this.insight.format === 'DISTRIBUTION') {
      const dataset: any[] = ['count'];
      const labels: string[] = ['x'];
      let otherCount = 0;

      for (let i = 0; i < this.insight.labels.length; i++) {
        const n_vt = this.insight.within[i];
        const n_vNt = this.insight.within.reduce((a, b) => a + b, 0) - n_vt;
        const n_v = n_vt + n_vNt;

        const n_t = n_vt + this.insight.without[i];
        const n_s = this.insight.within.reduce((a, b) => a + b, 0) + this.insight.without.reduce((a, b) => a + b, 0);

        if (this.unusualness(n_vt, n_v, n_t, n_s) > 0) {
          if (this.insight.within[i] > 0) {
            dataset.push(this.insight.within[i]);
            labels.push(this.insight.labels[i]);
          }
        } else {
          otherCount += this.insight.within[i];
        }
      }

      if (otherCount > 0) {
        dataset.push(otherCount);
        labels.push('Other');
      }

      // generate chart in container
      c3.generate({
        bindto: this.chartContainer.nativeElement,
        data: {
          x: 'x',
          columns: [labels, dataset],
          type: 'bar'
        },
        axis: {
            x: {
                type: 'category' // this needed to load string x value
            }
        },
        legend: {
          hide: true
        },
        size: {
          width: 346
        }
      });
    }

    // set icon
    if (Math.abs(this.insight.effectSize) > 0.8) {
      this.icon = 'error';
      this.color = 'red';
    } else if (Math.abs(this.insight.effectSize) > 0.5) {
      this.icon = 'warning';
      this.color = 'yellow';
    } else {
      this.icon = 'info';
      this.color = 'green';
    }

    // format fix
    if (this.insight.subTitle) {
      this.insight.subTitle = this.insight.subTitle.replace('-->', '&#8594;');
    }
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
    return moment.duration(duration, 'seconds').humanize();
  }
}
