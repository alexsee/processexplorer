import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VariantConditionComponent } from './variant-condition.component';

describe('VariantConditionComponent', () => {
  let component: VariantConditionComponent;
  let fixture: ComponentFixture<VariantConditionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VariantConditionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VariantConditionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
