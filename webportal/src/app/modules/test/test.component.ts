import { Component, OnInit } from '@angular/core';
import { Log } from 'src/app/entities/log';
import { QueryService } from 'src/app/services/query.service';
import { ChartComponentOptions } from 'src/app/dashboard/chart/chart-component-options';

@Component({
  selector: 'app-test',
  templateUrl: './test.component.html',
  styleUrls: ['./test.component.scss']
})
export class TestComponent implements OnInit {

  public context: Log;
  public options: ChartComponentOptions;

  constructor(private queryService: QueryService) { }

  ngOnInit() {
    this.queryService.getStatistics('bpi2019').subscribe(statistics => this.context = statistics);

    this.options = {
      type: 'bar',
      legendShow: true,
      legendPosition: 'top',
      tooltipsShow: true,

      x: {
        show: true,
        type: 'category'
      },
      y: {
        show: true
      },
      y2: {
        show: true
      },

      axis0: [{
        text: 'Item Type',
        alias: 'item_type',
        type: 'category'
      }],
      axis1: null,
      axis2: [{
        text: '# Cases',
        alias: 'count_cases',
        type: 'number',
        secondaryAxis: false
      }, {
        text: '# Variants',
        alias: 'count_variants',
        type: 'number',
        secondaryAxis: true
      }],

      dimensions: [{
        alias: 'item_type',
        type: 'case_attribute',
        attributeName: 'Item Type'
      }],
      kpis: [{
        alias: 'count_cases',
        type: 'count_cases',
        ordering: 'DESC'
      }, {
        alias: 'count_variants',
        type: 'count_variants'
      }]

    };
  }

}
