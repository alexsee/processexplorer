import { Component, OnInit } from '@angular/core';
import { LogService } from 'src/app/services/log.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-log-upload',
  templateUrl: './log-upload.component.html',
  styleUrls: ['./log-upload.component.scss']
})
export class LogUploadComponent implements OnInit {

  logName: string;
  file: File;

  constructor(
    private logService: LogService,
    private router: Router
  ) { }

  ngOnInit() {
  }

  onFileInput(files: FileList) {
    this.file = files.item(0);
  }

  doUpload() {
    this.logService.upload(this.logName, this.file).subscribe(x => {
      this.router.navigate(['/logs']);
    }, error => {

    });
  }

}
