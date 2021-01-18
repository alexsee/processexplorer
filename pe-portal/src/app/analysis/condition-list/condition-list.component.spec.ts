import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ConditionListComponent } from './condition-list.component';

describe('ConditionListComponent', () => {
  let component: ConditionListComponent;
  let fixture: ComponentFixture<ConditionListComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ ConditionListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConditionListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
