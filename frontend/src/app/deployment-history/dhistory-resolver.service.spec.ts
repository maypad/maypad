import { TestBed } from '@angular/core/testing';

import { DhistoryResolverService } from './dhistory-resolver.service';

describe('DhistoryResolverService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: DhistoryResolverService = TestBed.get(DhistoryResolverService);
    expect(service).toBeTruthy();
  });
});
