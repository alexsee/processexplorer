import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { TrainModelComponent } from './train-model.component';

describe('TrainModelComponent', () => {
  let component: TrainModelComponent;
  let fixture: ComponentFixture<TrainModelComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ TrainModelComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TrainModelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
