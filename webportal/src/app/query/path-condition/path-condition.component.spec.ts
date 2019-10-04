import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PathConditionComponent } from './path-condition.component';

describe('PathConditionComponent', () => {
  let component: PathConditionComponent;
  let fixture: ComponentFixture<PathConditionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PathConditionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PathConditionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
