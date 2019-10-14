import { Component, OnInit } from '@angular/core';
import { LogService } from 'src/app/services/log.service';
import { Router } from '@angular/router';
import { UploadFile } from 'ng-zorro-antd';

@Component({
  selector: 'app-log-upload',
  templateUrl: './log-upload.component.html',
  styleUrls: ['./log-upload.component.scss']
})
export class LogUploadComponent implements OnInit {

  logName: string;
  fileList: UploadFile[] = [];

  constructor(
    private logService: LogService,
    private router: Router
  ) { }

  ngOnInit() {
  }

  beforeUpload = (file: UploadFile): boolean => {
    this.fileList = [];
    this.fileList = this.fileList.concat(file);
    return false;
  };

  doUpload() {
    this.logService.upload(this.logName, this.fileList[0]).subscribe(x => {
      this.router.navigate(['/logs']);
    }, error => {

    });
  }
}
