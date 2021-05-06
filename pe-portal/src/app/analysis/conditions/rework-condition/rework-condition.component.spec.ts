import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ReworkConditionComponent } from './rework-condition.component';

describe('ReworkConditionComponent', () => {
  let component: ReworkConditionComponent;
  let fixture: ComponentFixture<ReworkConditionComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ ReworkConditionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ReworkConditionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
