import { TestBed } from '@angular/core/testing';

import { ProjectDetailResolverService } from './project-detail-resolver.service';
import { ProjectService } from '../project.service';
import { ProjectServiceStub } from 'src/testing/project-service-stub';
import { Router, ActivatedRouteSnapshot } from '@angular/router';
import { Project } from '../model/project';
import * as projectResponse from 'sample-requests/get.projects.id.response.json';

describe('ProjectDetailResolverService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    providers: [
      { provide: ProjectService, useClass: ProjectServiceStub },
      { provide: Router, useValue: null }
    ]
  }));

  it('should be created', () => {
    const service: ProjectDetailResolverService = TestBed.get(ProjectDetailResolverService);
    expect(service).toBeTruthy();
  });

  it('should resolve project', () => {
    const service: ProjectDetailResolverService = TestBed.get(ProjectDetailResolverService);
    const stub = new ActivatedRouteSnapshot();
    // Mock parammap, return value doesn't matter as projectservice is also mocked
    spyOn(stub.paramMap, 'get').and.returnValue(1);

    let subject: Project;
    service.resolve(stub, null).subscribe(((proj: Project) => { subject = proj; }));
    expect(subject).toBe(projectResponse['default']);
  });
});
