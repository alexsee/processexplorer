import { Injectable } from '@angular/core';
import { Condition } from '../models/condition.model';
import { PathConditionComponent } from '../conditions/path-condition/path-condition.component';
import { AttributeConditionComponent } from '../conditions/attribute-condition/attribute-condition.component';
import { VariantConditionComponent } from '../conditions/variant-condition/variant-condition.component';
import { ClusterConditionComponent } from '../conditions/cluster-condition/cluster-condition.component';
import { ReworkConditionComponent } from '../conditions/rework-condition/rework-condition.component';
import { DurationConditionComponent } from '../conditions/duration-condition/duration-condition.component';
import { ResourcePathConditionComponent } from '../conditions/resource-path-condition/resource-path-condition.component';

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
      } else if (qry.type === 'rework') {
        conditions.push(new Condition(ReworkConditionComponent, qry));
      } else if (qry.type === 'duration') {
        conditions.push(new Condition(DurationConditionComponent, qry));
      } else if (qry.type === 'resourcepath') {
        conditions.push(new Condition(ResourcePathConditionComponent, qry));
      }
    }

    return conditions;
  }
}
