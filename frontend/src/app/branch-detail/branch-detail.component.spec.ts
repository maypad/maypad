import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BranchDetailComponent } from './branch-detail.component';
import { ActivatedRoute, ActivatedRouteSnapshot } from '@angular/router';
import { HeaderComponent } from './header/header.component';
import { MarkdownModule, MarkdownService, MarkedOptions } from 'ngx-markdown';
import { HttpClientModule } from '@angular/common/http';
import * as branchesResponse from 'sample-requests/get.projects.id.branches.ref.response.json';
import * as projectResponse from 'sample-requests/get.projects.id.response.json';
import { of } from 'rxjs';
import { RouterTestingModule } from '@angular/router/testing';
import { NotificationService } from '../notification.service';
import { NotificationServiceStub } from 'src/testing/notification-service-stub';

describe('BranchDetailComponent', () => {
  let component: BranchDetailComponent;
  let fixture: ComponentFixture<BranchDetailComponent>;
  const branch = branchesResponse['default'];
  const project = projectResponse['default'];
  const snapshot = new ActivatedRouteSnapshot();

  beforeEach(async(() => {
    spyOn(snapshot.paramMap, 'get').and.returnValue('');
    TestBed.configureTestingModule({
      declarations: [BranchDetailComponent, HeaderComponent],
      providers: [
        {
          // Mock ActivatedRoute because a unit test can't have a "real" route
          provide: ActivatedRoute, useClass: class {
            snapshot = snapshot; data = of({ branch: branch, project: project });
          }
        },
        {
          provide: MarkdownService, useClass: MarkdownService
        },
        {
          provide: MarkedOptions, useValue: {}
        },
        {
          provide: NotificationService, useClass: NotificationServiceStub
        }
      ],
      imports: [MarkdownModule, HttpClientModule, RouterTestingModule]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BranchDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update success build status', () => {
    const notService: NotificationService = TestBed.get(NotificationService);
    spyOn(notService, 'sendSuccess');
    const evt = new MessageEvent('build_updated', {
      data: `{ "projectId": ${project['id']},
            "name": "${branch['name']}", "status": "SUCCESS" }`
    });
    component.handleBuildUpdated(evt);
    expect(notService.sendSuccess).toHaveBeenCalledWith('build_success', branch['name'], String(project['id']), undefined);
  });

  it('should update failed build status', () => {
    const notService: NotificationService = TestBed.get(NotificationService);
    spyOn(notService, 'sendWarning');
    const evt = new MessageEvent('build_updated', {
      data: `{ "projectId": ${project['id']},
            "name": "${branch['name']}", "status": "FAILED", "message":"build_failed" }`
    });
    component.handleBuildUpdated(evt);
    expect(notService.sendWarning).toHaveBeenCalledWith('build_failed', branch['name'], String(project['id']), undefined);
  });

  it('should update failed build status', () => {
    const notService: NotificationService = TestBed.get(NotificationService);
    spyOn(notService, 'send');
    const evt = new MessageEvent('build_updated', {
      data: `{ "projectId": ${project['id']},
            "name": "${branch['name']}", "status": "UNKNOWN" }`
    });
    component.handleBuildUpdated(evt);
    expect(notService.send).toHaveBeenCalledTimes(0);
  });
  it('should update success deployment status', () => {
    const notService: NotificationService = TestBed.get(NotificationService);
    spyOn(notService, 'sendSuccess');
    const evt = new MessageEvent('deployment_updated', {
      data: `{ "projectId": ${project['id']},
            "name": "${branch['name']}", "status": "SUCCESS" }`
    });
    component.handleDeploymentUpdated(evt);
    expect(notService.sendSuccess).toHaveBeenCalledWith('deployment_success', branch['name'], String(project['id']), undefined);
  });

  it('should update failed deployment status', () => {
    const notService: NotificationService = TestBed.get(NotificationService);
    spyOn(notService, 'sendWarning');
    const evt = new MessageEvent('deployment_updated', {
      data: `{ "projectId": ${project['id']},
            "name": "${branch['name']}", "status": "FAILED", "message":"deployment_failed" }`
    });
    component.handleDeploymentUpdated(evt);
    expect(notService.sendWarning).toHaveBeenCalledWith('deployment_failed', branch['name'], String(project['id']), undefined);
  });
});
