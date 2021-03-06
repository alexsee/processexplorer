import { Component, Input } from '@angular/core';
import { ArtifactUIField } from 'src/app/analysis/models/artifact-ui-field.model';
import { EventLogStatistics } from 'src/app/log/models/eventlog-statistics.model';

@Component({
    selector: 'app-artifact-settings-field',
    templateUrl: './artifact-settings-field.component.html',
    styleUrls: ['./artifact-settings-field.component.scss']
})
export class ArtifactSettingsFieldComponent {
  @Input() context: EventLogStatistics;
  @Input() field: ArtifactUIField;
  @Input() data: any;

  newitem: any = {};

  constructor() {
  }

  doAddColumn() {
    this.data.push(this.newitem);
    this.newitem = {};
  }

  doRemoveColumn(item: any) {
    this.data.splice(this.data.indexOf(item), 1);
  }
}
