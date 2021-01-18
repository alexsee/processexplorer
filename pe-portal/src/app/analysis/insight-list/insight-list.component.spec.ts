import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { InsightListComponent } from './insight-list.component';

describe('InsightListComponent', () => {
  let component: InsightListComponent;
  let fixture: ComponentFixture<InsightListComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ InsightListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InsightListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
