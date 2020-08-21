import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InsightListComponent } from './insight-list.component';

describe('InsightListComponent', () => {
  let component: InsightListComponent;
  let fixture: ComponentFixture<InsightListComponent>;

  beforeEach(async(() => {
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
