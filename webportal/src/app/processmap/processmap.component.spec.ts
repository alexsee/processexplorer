import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProcessmapComponent } from './processmap.component';

describe('ProcessmapComponent', () => {
  let component: ProcessmapComponent;
  let fixture: ComponentFixture<ProcessmapComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ProcessmapComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProcessmapComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
