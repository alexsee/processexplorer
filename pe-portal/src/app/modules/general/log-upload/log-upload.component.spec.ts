import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { LogUploadComponent } from './log-upload.component';

describe('LogUploadComponent', () => {
  let component: LogUploadComponent;
  let fixture: ComponentFixture<LogUploadComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ LogUploadComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LogUploadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
