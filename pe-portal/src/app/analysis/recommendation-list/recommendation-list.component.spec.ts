import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RecommendationListComponent } from './recommendation-list.component';

describe('RecommendationListComponent', () => {
  let component: RecommendationListComponent;
  let fixture: ComponentFixture<RecommendationListComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ RecommendationListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RecommendationListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
