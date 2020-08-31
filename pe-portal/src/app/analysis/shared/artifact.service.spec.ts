import { TestBed } from '@angular/core/testing';

import { ArtifactService } from './artifact.service';

describe('ArtifactService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ArtifactService = TestBed.get(ArtifactService);
    expect(service).toBeTruthy();
  });
});
