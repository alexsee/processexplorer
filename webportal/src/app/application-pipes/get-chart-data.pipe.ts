import { Pipe, PipeTransform } from '@angular/core';
import { Insight } from '../entities/insight';

@Pipe({
  name: 'getChartData',
  pure: true
})
export class GetChartDataPipe implements PipeTransform {

  transform(value: Insight): any {
    return this.getChartData(value);
  }

  getChartData(insight: Insight) {
      if (insight.format !== 'DISTRIBUTION') {
        return null;
      }
      return [{data: insight.within, label: 'within'}, {data: insight.without, label: 'without'}];
  }
}
