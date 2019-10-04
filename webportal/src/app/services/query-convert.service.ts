import { Injectable } from '@angular/core';
import { Condition } from '../entities/conditions/condition';

import { PathConditionComponent } from '../query/path-condition/path-condition.component';
import { AttributeConditionComponent } from '../query/attribute-condition/attribute-condition.component';
import { VariantConditionComponent } from '../query/variant-condition/variant-condition.component';
import { ClusterConditionComponent } from '../query/cluster-condition/cluster-condition.component';

@Injectable({
  providedIn: 'root'
})
export class QueryConvertService {

  constructor() { }

  /**
   * Converts a list of conditions to a query for the QueryService.
   *
   * @param conditions A list of conditions to convert.
   */
  convertToQuery(conditions: Condition[]) {
    const query = [];

    for (const condition of conditions) {
      const type = condition.component.name.toLocaleLowerCase().replace('conditioncomponent', '');
      query.push({ type, ...condition.data});
    }

    return query;
  }

  /**
   * Converts a query to a list of conditions.
   *
   * @param query A query result.
   */
  convertFromQuery(query: any[]): Condition[] {
    const conditions = [];

    if (!query) {
      return conditions;
    }

    for (const qry of query) {
      if (qry.type === 'path') {
        conditions.push(new Condition(PathConditionComponent, qry));
      } else if (qry.type === 'attribute') {
        conditions.push(new Condition(AttributeConditionComponent, qry));
      } else if (qry.type === 'variant') {
        conditions.push(new Condition(VariantConditionComponent, qry));
      } else if (qry.type === 'cluster') {
        conditions.push(new Condition(ClusterConditionComponent, qry));
      }
    }

    return conditions;
  }
}
