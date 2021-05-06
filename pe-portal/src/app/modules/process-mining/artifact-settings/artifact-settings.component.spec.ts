import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ArtifactSettingsComponent } from './artifact-settings.component';

describe('ArtifactSettingsComponent', () => {
  let component: ArtifactSettingsComponent;
  let fixture: ComponentFixture<ArtifactSettingsComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ ArtifactSettingsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ArtifactSettingsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
