import { Component, Input, OnChanges } from '@angular/core';

import * as moment from 'moment';
import { QueryConvertService } from 'src/app/analysis/shared/query-convert.service';
import { AnalysisService } from 'src/app/analysis/shared/analysis.service';
import { Insight } from '../models/insight.model';
import { Condition } from '../models/condition.model';

@Component({
  selector: 'app-insight-list',
  templateUrl: './insight-list.component.html',
  styleUrls: ['./insight-list.component.scss']
})
export class InsightListComponent implements OnChanges {
  @Input() public logName: string;
  @Input() public conditions: Condition[];

  public insights: Insight[];
  public noData = false;
  public progress = false;

  constructor(
    private analysisService: AnalysisService,
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

    this.analysisService.getInsights(this.logName, this.queryConvertService.convertToQuery(this.conditions))
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
