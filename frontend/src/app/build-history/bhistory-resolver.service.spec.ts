import { TestBed } from '@angular/core/testing';

import { BhistoryResolverService } from './bhistory-resolver.service';
import { ActivatedRouteSnapshot } from '@angular/router';
import { Build } from '../model/build';
import * as buildsResponse from 'sample-requests/get.projects.id.branches.ref.builds.response.json';
import { BranchService } from '../branch.service';
import { BranchServiceStub } from 'src/testing/branch-service-stub';

describe('BhistoryResolverService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    providers: [{ provide: BranchService, useClass: BranchServiceStub }]
  }));

  it('should be created', () => {
    const service: BhistoryResolverService = TestBed.get(BhistoryResolverService);
    expect(service).toBeTruthy();
  });

  it('should resolve buildhistory', () => {
    const service: BhistoryResolverService = TestBed.get(BhistoryResolverService);
    const stub = new ActivatedRouteSnapshot();
    // Mock parammap, return value doesn't matter as projectservice is also mocked
    spyOn(stub.paramMap, 'get').and.returnValue(null);

    let subject: Build[];
    service.resolve(stub, null).subscribe(((builds: Build[]) => { subject = builds; }));
    expect(subject.length).toBe(buildsResponse['default'].length);
  });
});
