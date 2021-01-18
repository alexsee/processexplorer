import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { OpenCasesListComponent } from './open-cases-list.component';

describe('OpenCasesListComponent', () => {
  let component: OpenCasesListComponent;
  let fixture: ComponentFixture<OpenCasesListComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ OpenCasesListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OpenCasesListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
