import { Component, Input, OnChanges, EventEmitter, Output } from '@angular/core';
import { Condition } from '../models/condition.model';
import { AnalysisService } from '../shared/analysis.service';
import { QueryConvertService } from '../shared/query-convert.service';
import { Recommendation } from '../models/recommendation';

@Component({
  selector: 'app-recommendation-list',
  templateUrl: './recommendation-list.component.html',
  styleUrls: ['./recommendation-list.component.scss']
})
export class RecommendationListComponent implements OnChanges {
  @Input() public logName: string;
  @Input() public conditions: Condition[];

  @Output() public apply = new EventEmitter<Recommendation>();

  private oldLogName: string;
  public recommendations: Recommendation[];

  constructor(
    private analysisService: AnalysisService,
    private queryConvertService: QueryConvertService
  ) { }

  ngOnChanges() {
    this.update();
  }

  update() {
    if (!this.conditions) {
      return;
    }

    if (this.oldLogName === this.logName) {
      return;
    }

    this.oldLogName = this.logName;
    this.analysisService.getRecommendations(this.logName, this.queryConvertService.convertToQuery(this.conditions))
      .subscribe(recommendations => {
        this.recommendations = recommendations;
      });
  }

  doApply(recommendation: Recommendation) {
    this.apply.emit(recommendation);
  }

}
