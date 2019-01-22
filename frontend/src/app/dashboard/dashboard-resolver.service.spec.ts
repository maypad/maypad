import { TestBed } from '@angular/core/testing';

import { DashboardResolverService } from './dashboard-resolver.service';
import { ActivatedRouteSnapshot } from '@angular/router';
import { ProjectgroupService } from '../projectgroup.service';
import { ProjectgroupServiceStub } from 'src/testing/projectgroup.service.stub';
import * as projectgroupsResponse from 'sample-requests/get.projectgroups.response.json';
import { Projectgroup } from '../model/projectGroup';

describe('DashboardResolverService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    providers: [{ provide: ProjectgroupService, useClass: ProjectgroupServiceStub }]
  }));

  it('should be created', () => {
    const service: DashboardResolverService = TestBed.get(DashboardResolverService);
    expect(service).toBeTruthy();
  });

  it('should resolve projectgroups', () => {
    const service: DashboardResolverService = TestBed.get(DashboardResolverService);
    const stub = new ActivatedRouteSnapshot();
    // Mock route snapshot
    spyOn(stub.paramMap, 'get').and.returnValue('');

    let subject: Projectgroup[];
    service.resolve(stub, null).subscribe(res => { subject = res; });
    expect(subject.length).toBe(projectgroupsResponse['default'].length);
    for (let i = 0; i < subject.length; i++) {
      expect(subject[i].id).toBe(projectgroupsResponse['default'][i].id);
      expect(subject[i].name).toBe(projectgroupsResponse['default'][i].name);
    }
  });
});
