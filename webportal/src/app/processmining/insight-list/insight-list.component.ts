import { Component, Input, OnChanges } from '@angular/core';
import { Condition } from 'src/app/entities/conditions/condition';
import { Insight } from 'src/app/entities/insight';
import { QueryService } from 'src/app/services/query.service';

import * as moment from 'moment';
import { QueryConvertService } from 'src/app/services/query-convert.service';

@Component({
  selector: 'app-insight-list',
  templateUrl: './insight-list.component.html',
  styleUrls: ['./insight-list.component.scss']
})
export class InsightListComponent implements OnChanges {
  @Input() private logName: string;
  @Input() private conditions: Condition[];

  private insights: Insight[];
  private noData = false;
  private progress = false;

  constructor(
    private queryService: QueryService,
    private queryConvertService: QueryConvertService
  ) { }

  ngOnChanges() {
    this.update();
  }

  update() {
    if (!this.conditions || this.conditions.length === 0) {
      return;
    }

    this.noData = true;
    this.progress = true;

    this.queryService.getInsights(this.logName, this.queryConvertService.convertToQuery(this.conditions))
      .subscribe(insights => {
        this.insights = insights;
        this.insights.sort(this.sortByEffectSize);

        this.noData = this.insights.length > 0;
        this.progress = false;
      });
  }

  sortByEffectSize(a: Insight, b: Insight) {
    if (Math.abs(a.effectSize) < Math.abs(b.effectSize)) {
      return 1;
    }
    if (Math.abs(a.effectSize) > Math.abs(b.effectSize)) {
      return -1;
    }
    return 0;
  }

  humanizeDuration(duration: number) {
    return moment.duration(duration, 'seconds').humanize();
  }
}
