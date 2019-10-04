import { TestBed } from '@angular/core/testing';

import { QueryConvertService } from './query-convert.service';

describe('QueryConvertService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: QueryConvertService = TestBed.get(QueryConvertService);
    expect(service).toBeTruthy();
  });
});
