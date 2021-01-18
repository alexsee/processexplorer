import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { LogAnnotationComponent } from './log-annotation.component';

describe('LogAnnotationComponent', () => {
  let component: LogAnnotationComponent;
  let fixture: ComponentFixture<LogAnnotationComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ LogAnnotationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LogAnnotationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
