import { TestBed } from '@angular/core/testing';

import { DhistoryResolverService } from './dhistory-resolver.service';
import { BranchServiceStub } from 'src/testing/branch-service-stub';
import { BranchService } from '../branch.service';
import * as deploymentsResponse from 'sample-requests/get.projects.id.branches.ref.deployments.response.json';
import { Deployment } from '../model/deployment';
import { ActivatedRouteSnapshot } from '@angular/router';

describe('DhistoryResolverService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    providers: [{ provide: BranchService, useClass: BranchServiceStub }]
  }));

  it('should be created', () => {
    const service: DhistoryResolverService = TestBed.get(DhistoryResolverService);
    expect(service).toBeTruthy();
  });

  it('should resolve deploymenthistory', () => {
    const service: DhistoryResolverService = TestBed.get(DhistoryResolverService);
    const stub = new ActivatedRouteSnapshot();
    // Mock parammap, return value doesn't matter as projectservice is also mocked
    spyOn(stub.paramMap, 'get').and.returnValue(null);

    let subject: Deployment[];
    service.resolve(stub, null).subscribe(((deployments: Deployment[]) => { subject = deployments; }));
    expect(subject).toBe(deploymentsResponse['default']);
  });
});
