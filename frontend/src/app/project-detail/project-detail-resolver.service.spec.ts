import { TestBed } from '@angular/core/testing';

import { ProjectDetailResolverService } from './project-detail-resolver.service';

describe('ProjectDetailResolverService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ProjectDetailResolverService = TestBed.get(ProjectDetailResolverService);
    expect(service).toBeTruthy();
  });
});
