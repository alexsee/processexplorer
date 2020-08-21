import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PredictionNavigationComponent } from './prediction-navigation.component';

describe('PredictionNavigationComponent', () => {
  let component: PredictionNavigationComponent;
  let fixture: ComponentFixture<PredictionNavigationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PredictionNavigationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PredictionNavigationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
