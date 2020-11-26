import { Component, ElementRef, Input, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { nb_NO } from 'ng-zorro-antd/i18n';
import { EventLogStatistics } from 'src/app/log/models/eventlog-statistics.model';
import { Condition } from '../../models/condition.model';
import { Widget } from '../../models/widget.model';
import { ProcessMapComponent } from '../../process-map/processmap.component';
import { WidgetComponent } from '../widget.component';
import { WidgetHostComponent } from '../widget/widget-host.component';

@Component({
  selector: 'app-widget-process-map',
  templateUrl: './process-map.component.html',
  styleUrls: ['./process-map.component.scss']
})
export class WidgetProcessMapComponent implements OnInit, WidgetComponent {
  @ViewChild(ProcessMapComponent) public processmapContainer: ProcessMapComponent;
  @ViewChild('optionsTemplate', {static: true}) public optionsTemplate: TemplateRef<any>;

  // input parameters
  @Input() public context: EventLogStatistics;
  @Input() public conditions: Condition[];
  @Input() public widget: Widget;
  @Input() public parent: WidgetHostComponent;

  constructor() { }

  getOptionsTemplate(): TemplateRef<any> {
    return this.optionsTemplate;
  }

  doResize() {
  }

  doUpdate() {
    if (this.processmapContainer) {
      this.processmapContainer.update();
    }
  }

  getSensitivityTemplate(): TemplateRef<any> {
    return null;
  }

  ngOnInit(): void {
  }

}
