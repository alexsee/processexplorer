import { Component, OnInit, Input, OnChanges, OnDestroy } from '@angular/core';
import { Condition } from 'src/app/entities/conditions/condition';
import { QueryService } from 'src/app/services/query.service';
import { Insight } from 'src/app/entities/insight';

import * as moment from 'moment';

@Component({
  selector: 'app-insight',
  templateUrl: './insight.component.html',
  styleUrls: ['./insight.component.scss']
})
export class InsightComponent implements OnChanges {
  @Input() private logName: string;
  @Input() private conditions: Condition[];

  private insights: Insight[];

  constructor(
    private queryService: QueryService
  ) { }

  ngOnChanges() {
    this.update();
  }

  update() {
    if (!this.conditions) {
      return;
    }

    this.queryService.getInsights(this.logName, this.queryService.convertToQuery(this.conditions))
      .subscribe(insights => {
        this.insights = insights;
        this.insights.sort(this.sortByEffectSize);
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
