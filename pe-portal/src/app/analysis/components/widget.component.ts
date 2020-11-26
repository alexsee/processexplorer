import { TemplateRef } from '@angular/core';
import { EventLogStatistics } from 'src/app/log/models/eventlog-statistics.model';
import { Condition } from '../models/condition.model';
import { Widget } from '../models/widget.model';
import { WidgetHostComponent } from './widget/widget-host.component';

export interface WidgetComponent {
    widget: Widget;
    context: EventLogStatistics;
    conditions: Condition[];

    parent: WidgetHostComponent;

    getOptionsTemplate(): TemplateRef<any>;
    getSensitivityTemplate(): TemplateRef<any>;

    doResize();
    doUpdate();
}
