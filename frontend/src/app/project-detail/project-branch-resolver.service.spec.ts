import { TestBed } from '@angular/core/testing';

import { ProjectBranchResolverService } from './project-branch-resolver.service';
import { ProjectService } from '../project.service';
import { ProjectServiceStub } from 'src/testing/project-service-stub';
import { ActivatedRouteSnapshot } from '@angular/router';
import { Branch } from '../model/branch';
import * as branchesResponse from 'sample-requests/get.projects.id.branches.response.json';

describe('ProjectBranchResolverService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    providers: [{ provide: ProjectService, useClass: ProjectServiceStub }]
  }));

  it('should be created', () => {
    const service: ProjectBranchResolverService = TestBed.get(ProjectBranchResolverService);
    expect(service).toBeTruthy();
  });

  it('should resolve branches', () => {
    const service: ProjectBranchResolverService = TestBed.get(ProjectBranchResolverService);
    const stub = new ActivatedRouteSnapshot();
    // Mock parammap, return value doesn't matter as projectservice is also mocked
    spyOn(stub.paramMap, 'get').and.returnValue(1);

    let subject: Branch[];
    service.resolve(stub, null).subscribe(((branch: Branch[]) => { subject = branch; }));
    expect(subject).toBe(branchesResponse['default']);
  });
});
