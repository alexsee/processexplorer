import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NzMessageService } from 'ng-zorro-antd/message';
import { LogService } from 'src/app/log/shared/log.service';

@Component({
  selector: 'app-log-upload',
  templateUrl: './log-upload.component.html',
  styleUrls: ['./log-upload.component.scss']
})
export class LogUploadComponent implements OnInit {

  logName: string;
  fileList: File[] = [];

  constructor(
    private nzMessageService: NzMessageService,
    private logService: LogService,
    private router: Router
  ) { }

  ngOnInit() {
  }

  beforeUpload = (file: File): boolean => {
    this.fileList = [];
    this.fileList = this.fileList.concat(file);
    return false;
  }

  doUpload() {
    this.logService.upload(this.logName, this.fileList[0]).subscribe(x => {
      this.router.navigate(['/logs']);
      this.nzMessageService.success('Event log <b>' + this.logName + '</b> uploaded successfully.');
    }, error => {
      this.nzMessageService.error('Could not upload <b>' + this.logName + '</b>.');
    });
  }
}
