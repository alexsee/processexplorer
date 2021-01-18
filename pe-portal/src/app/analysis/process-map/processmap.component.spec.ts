import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ProcessMapComponent } from './processmap.component';

describe('ProcessMapComponent', () => {
  let component: ProcessMapComponent;
  let fixture: ComponentFixture<ProcessMapComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ ProcessMapComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProcessMapComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
