import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { AttributeConditionComponent } from './attribute-condition.component';

describe('AttributeConditionComponent', () => {
  let component: AttributeConditionComponent;
  let fixture: ComponentFixture<AttributeConditionComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ AttributeConditionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AttributeConditionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
