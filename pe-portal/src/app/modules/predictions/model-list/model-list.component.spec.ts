import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PredictionModelListComponent } from './model-list.component';

describe('ModelListComponent', () => {
  let component: PredictionModelListComponent;
  let fixture: ComponentFixture<PredictionModelListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PredictionModelListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PredictionModelListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
