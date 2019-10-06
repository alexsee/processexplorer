import { Component, OnInit } from '@angular/core';
import { Log } from 'src/app/entities/log';
import { QueryService } from 'src/app/services/query.service';

@Component({
  selector: 'app-test',
  templateUrl: './test.component.html',
  styleUrls: ['./test.component.scss']
})
export class TestComponent implements OnInit {

  public context: Log;
  public options: any;

  constructor(private queryService: QueryService) { }

  ngOnInit() {
    this.queryService.getStatistics('bpi2019').subscribe(statistics => this.context = statistics);

    this.options = {
      type: 'line',
      dimensions: [{
        type: 'case_attribute',
        attributeName: 'Item Type'
      }],
      kpis: [{
        type: 'count_cases',
        ordering: 'DESC'
      }, {
        type: 'count_variants'
      }]
    };
  }

}
