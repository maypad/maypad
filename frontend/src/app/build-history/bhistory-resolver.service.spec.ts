import { TestBed } from '@angular/core/testing';

import { BhistoryResolverService } from './bhistory-resolver.service';

describe('BhistoryResolverService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: BhistoryResolverService = TestBed.get(BhistoryResolverService);
    expect(service).toBeTruthy();
  });
});
