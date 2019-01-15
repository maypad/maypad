import { TestBed } from '@angular/core/testing';

import { ProjectDetailResolverService } from './project-detail-resolver.service';
import { ProjectService } from '../project.service';
import { ProjectServiceStub } from 'src/testing/project-service-stub';
import { Router } from '@angular/router';

describe('ProjectDetailResolverService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    providers: [
      { provide: ProjectService, useCLass: ProjectServiceStub },
      { provide: Router, useValue: null }
    ]
  }));

  it('should be created', () => {
    const service: ProjectDetailResolverService = TestBed.get(ProjectDetailResolverService);
    expect(service).toBeTruthy();
  });
});
