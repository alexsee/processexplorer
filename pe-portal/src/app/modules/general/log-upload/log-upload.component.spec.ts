import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LogUploadComponent } from './log-upload.component';

describe('LogUploadComponent', () => {
  let component: LogUploadComponent;
  let fixture: ComponentFixture<LogUploadComponent>;

  beforeEach(async(() => {
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
