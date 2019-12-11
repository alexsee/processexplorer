import { Component, OnInit } from '@angular/core';
import { EventLogStatistics } from 'src/app/log/models/eventlog-statistics.model';
import { ActivatedRoute, Router } from '@angular/router';
import { ArtifactService } from 'src/app/analysis/shared/artifact.service';
import { QueryService } from 'src/app/analysis/shared/query.service';
import { ArtifactConfiguration } from 'src/app/analysis/models/artifact-configuration.model';
import { forkJoin } from 'rxjs';
import { NzMessageService } from 'ng-zorro-antd';
import { Artifact } from 'src/app/analysis/models/artifact.model';

@Component({
  selector: 'app-artifact-settings',
  templateUrl: './artifact-settings.component.html',
  styleUrls: ['./artifact-settings.component.scss']
})
export class ArtifactSettingsComponent implements OnInit {

  logName: string;
  context: EventLogStatistics;

  artifactList: Artifact[];
  artifactClasses = {};
  artifacts: ArtifactConfiguration[];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private nzMessageService: NzMessageService,
    private queryService: QueryService,
    private artifactService: ArtifactService,
  ) { }

  ngOnInit() {
    this.logName = this.route.snapshot.paramMap.get('logName');

    this.queryService.getStatistics(this.logName)
      .subscribe(statistics => this.context = statistics);

    this.artifactService.getArtifacts()
      .subscribe(artifacts => {
        this.artifactClasses = artifacts.reduce((map, obj) => {
          map[obj.type] = obj;
          return map;
        }, {});

        this.artifactList = artifacts;

        this.artifactService.getArtifactConfiguration(this.logName)
        .subscribe(configuration => {
          this.artifacts = configuration;

          for (const artifact of this.artifacts) {
            artifact.data = JSON.parse(artifact.configuration);

            this.artifactService.getArtifactUI(artifact.type)
              .subscribe(fields => artifact.fields = fields);
          }
        });
      });
  }

  doCancel() {
    this.router.navigate(['/artifacts', this.logName]);
  }

  doSave() {
    const requests = [];

    for (const artifact of this.artifacts) {
      artifact.configuration = JSON.stringify(artifact.data);
      requests.push(this.artifactService.saveArtifactConfiguration(this.logName, artifact));
    }

    forkJoin(requests).subscribe(response => {
      this.nzMessageService.success('Process knowledge artifacts updated.');
      this.router.navigate(['/artifacts', this.logName]);
    });
  }

  doAddArtifact(artifact: Artifact) {
    this.artifactService.getArtifactUI(artifact.type)
      .subscribe(fields => {
        const item = {
          id: null,
          type: artifact.type,
          fields,
          activated: true,
          configuration: '',
          data: {}
        };

        for (const field of fields) {
          if (field.type === 'MULTI_COLUMN') {
            item.data[field.fieldName] = [];
          } else {
            item.data[field.fieldName] = null;
          }
        }

        this.artifacts.push(item);
      });
  }

  doDeleteArtifact(artifact: ArtifactConfiguration) {
    this.artifactService.delete(artifact).subscribe(x => {
      this.nzMessageService.success('Artifact deleted successfully.');
      this.artifacts.splice(this.artifacts.indexOf(artifact), 1);
    });
  }

}
