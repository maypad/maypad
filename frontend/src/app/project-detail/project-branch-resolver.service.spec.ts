import { TestBed } from '@angular/core/testing';

import { ProjectBranchResolverService } from './project-branch-resolver.service';

describe('ProjectBranchResolverService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ProjectBranchResolverService = TestBed.get(ProjectBranchResolverService);
    expect(service).toBeTruthy();
  });
});
