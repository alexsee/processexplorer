import { TestBed } from '@angular/core/testing';

import { AnalysisService } from './analysis.service';

describe('AnalysisService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: AnalysisService = TestBed.get(AnalysisService);
    expect(service).toBeTruthy();
  });
});
