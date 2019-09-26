import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConditionSingleComponent } from './condition-single.component';

describe('ConditionSingleComponent', () => {
  let component: ConditionSingleComponent;
  let fixture: ComponentFixture<ConditionSingleComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ConditionSingleComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConditionSingleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
