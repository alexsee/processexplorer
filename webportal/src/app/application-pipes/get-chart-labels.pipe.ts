import { Pipe, PipeTransform } from '@angular/core';
import { Insight } from '../entities/insight';

@Pipe({
  name: 'getChartLabels',
  pure: true
})
export class GetChartLabelsPipe implements PipeTransform {
  transform(value: Insight): any {
    return this.getChartLabels(value);
  }

  getChartLabels(insight: Insight) {
    if (insight.format !== 'DISTRIBUTION') {
      return null;
    }
    return insight.labels;
  }
}
