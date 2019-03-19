import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectDetailComponent } from './project-detail.component';
import { ActivatedRoute } from '@angular/router';
import { BranchListItemComponent } from './branch-list-item/branch-list-item.component';
import { EditProjectDialogComponent } from './edit-project-dialog/edit-project-dialog.component';
import { RouterTestingModule } from '@angular/router/testing';
import { FormsModule } from '@angular/forms';
import { of } from 'rxjs';
import * as projectResponse from 'sample-requests/get.projects.id.response.json';
import * as branchesResponse from 'sample-requests/get.projects.id.branches.response.json';
import { HttpClientModule } from '@angular/common/http';
import { ProjectServiceStub } from 'src/testing/project-service-stub';
import { ProjectService } from '../project.service';
import { NotificationService } from '../notification.service';
import { NotificationServiceStub } from 'src/testing/notification-service-stub';

describe('ProjectDetailComponent', () => {
  let component: ProjectDetailComponent;
  let fixture: ComponentFixture<ProjectDetailComponent>;
  const project = projectResponse['default'];
  const branches = branchesResponse['default'];

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ProjectDetailComponent, BranchListItemComponent, EditProjectDialogComponent],
      providers: [
        {
          // Mock ActivatedRoute because a unit test can't have a "real" route
          provide: ActivatedRoute, useClass: class { snapshot = {}; data = of({ project: project, branches: branches }); }
        },
        { provide: ProjectService, useClass: ProjectServiceStub },
        { provide: NotificationService, useClass: NotificationServiceStub }
      ],
      imports: [RouterTestingModule, FormsModule, HttpClientModule]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should create list items', () => {
    const nativeElement: HTMLElement = fixture.nativeElement;
    const elements: NodeListOf<Element> = nativeElement.querySelectorAll('app-branch-list-item');
    expect(elements.length).toBe(branches.length);
    for (let i = 0; i < elements.length; i++) {
      expect(elements[i].querySelector('h5').textContent).toBe(' ' + branches[i].name + ' ');
    }
  });

  it('should refresh project', () => {
    const notification: NotificationServiceStub = TestBed.get(NotificationService);
    spyOn(notification, 'sendInfo');
    component.refreshProject();
    expect(notification.sendInfo).toHaveBeenCalledWith('refreshing', undefined, String(project['id']), undefined);
  });

  it('should fail to refresh project', () => {
    const notService: NotificationService = TestBed.get(NotificationService);
    spyOn(notService, 'sendWarning');
    const evt = new MessageEvent('project_refreshed', {
      data: `{ "projectId": ${project['id']}, "message": "git_not_available", "status": "FAILED"}`
    });
    component.handleProjectRefreshed(evt);
    expect(notService.sendWarning).toHaveBeenCalledWith('git_not_available', undefined, project['id'], undefined);
  });

  it('should reload project', () => {
    const projService: ProjectService = TestBed.get(ProjectService);
    spyOn(projService, 'loadProject').and.callThrough();
    const notService: NotificationService = TestBed.get(NotificationService);
    spyOn(notService, 'sendSuccess');
    const evt = new MessageEvent('project_refreshed', {
      data: `{ "projectId": ${project['id']}, "message": "refresh_successful", "status": "SUCCESS"}`
    });
    component.handleProjectRefreshed(evt);
    expect(notService.sendSuccess).toHaveBeenCalledWith('refresh_successful', undefined, project['id'], undefined);
  });

  it('should update buildstatus', () => {
    const notService: NotificationService = TestBed.get(NotificationService);
    spyOn(notService, 'sendSuccess');
    component.project.branches = branches;
    const evt = new MessageEvent('build_updated', {
      data: `{ "projectId": ${project['id']}, "name": "master", "status": "SUCCESS"}`
    });
    component.handleBuildUpdated(evt);
    expect(notService.sendSuccess).toHaveBeenCalledWith('build_success', 'master', String(project['id']), undefined);
  });

  it('should change project', () => {
    const projService: ProjectService = TestBed.get(ProjectService);
    spyOn(projService, 'loadProject').and.callThrough();
    const notService: NotificationService = TestBed.get(NotificationService);
    spyOn(notService, 'sendSuccess');
    const evt = new MessageEvent('project_refreshed', {
      data: `{ "projectId": ${project['id']}, "message": "serviceaccount_changed", "status": "SUCCESS"}`
    });
    component.handleProjectChanged(evt);
    expect(notService.sendSuccess).toHaveBeenCalledWith('serviceaccount_changed', undefined, String(project['id']), undefined);
  });
});
