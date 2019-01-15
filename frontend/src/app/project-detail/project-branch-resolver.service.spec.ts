import { TestBed } from '@angular/core/testing';

import { ProjectBranchResolverService } from './project-branch-resolver.service';
import { ProjectService } from '../project.service';
import { ProjectServiceStub } from 'src/testing/project-service-stub';

describe('ProjectBranchResolverService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    providers: [{ provide: ProjectService, useClass: ProjectServiceStub }]
  }));

  it('should be created', () => {
    const service: ProjectBranchResolverService = TestBed.get(ProjectBranchResolverService);
    expect(service).toBeTruthy();
  });
});
