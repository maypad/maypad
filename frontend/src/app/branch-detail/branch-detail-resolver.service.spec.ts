import { TestBed } from '@angular/core/testing';

import { BranchDetailResolverService } from './branch-detail-resolver.service';
import { ActivatedRouteSnapshot } from '@angular/router';
import { Branch } from '../model/branch';
import * as branchesResponse from 'sample-requests/get.projects.id.branches.ref.response.json';
import { BranchService } from '../branch.service';
import { BranchServiceStub } from 'src/testing/branch-service-stub';
import { HeaderComponent } from './header/header.component';

describe('BranchDetailResolverService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    declarations: [HeaderComponent],
    providers: [{ provide: BranchService, useClass: BranchServiceStub }]
  }));

  it('should be created', () => {
    const service: BranchDetailResolverService = TestBed.get(BranchDetailResolverService);
    expect(service).toBeTruthy();
  });

  it('should resolve branch', () => {
    const service: BranchDetailResolverService = TestBed.get(BranchDetailResolverService);
    const stub = new ActivatedRouteSnapshot();
    // Mock route snapshot
    spyOn(stub.paramMap, 'get').and.returnValue('');

    let branch: Branch;
    service.resolve(stub, null).subscribe(res => { branch = res; });
    expect(branch).toBe(branchesResponse['default']);
  });
});
