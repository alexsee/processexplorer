import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ModelDetailComponent } from './model-detail.component';

describe('ModelDetailComponent', () => {
  let component: ModelDetailComponent;
  let fixture: ComponentFixture<ModelDetailComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ ModelDetailComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ModelDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
