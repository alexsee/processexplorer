import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DurationConditionComponent } from './duration-condition.component';

describe('DurationConditionComponent', () => {
  let component: DurationConditionComponent;
  let fixture: ComponentFixture<DurationConditionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DurationConditionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DurationConditionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
