import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AddProjectDialogComponent, AuthMethods, RepoTypes } from './add-project-dialog.component';
import { FormsModule } from '@angular/forms';
import { ProjectgroupServiceStub } from 'src/testing/projectgroup.service.stub';
import { ProjectgroupService } from 'src/app/projectgroup.service';
import { EnumToArrayPipe } from 'src/app/enum-to-array.pipe';
import { NotificationService } from 'src/app/notification.service';
import { NotificationServiceStub } from 'src/testing/notification-service-stub';
import { BuildStatus } from 'src/app/model/buildStatus';
import { of } from 'rxjs';

describe('AddProjectDialogComponent', () => {
  let component: AddProjectDialogComponent;
  let fixture: ComponentFixture<AddProjectDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AddProjectDialogComponent, EnumToArrayPipe],
      imports: [FormsModule],
      providers: [
        {
          provide: ProjectgroupService, useClass: ProjectgroupServiceStub
        },
        {
          provide: NotificationService, useClass: NotificationServiceStub
        }
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddProjectDialogComponent);
    component = fixture.componentInstance;
    component.projGroup = { id: 123, name: 'asd', projects: [], status: BuildStatus.UNKNOWN };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should clear info', () => {
    component.repoUrl = 'a';
    component.sshKey = 'a';
    component.username = 'a';
    component.password = 'a';
    component.clearInput();
    expect(component.repoUrl).toBe('');
    expect(component.sshKey).toBe('');
    expect(component.username).toBe('');
    expect(component.password).toBe('');
  });

  it('should select index', () => {
    component.authMethod = AuthMethods.None;
    component.setAuthSelected('SSH-Key');
    expect(component.authMethod).toBe(AuthMethods.SSH);
  });

  it('should select repo type', () => {
    component.repoType = RepoTypes.Git;
    component.setRepoSelected(RepoTypes.SVN);
    expect(component.repoType).toBe(RepoTypes.SVN);
  });

  it('should add project none auth', () => {
    const groupService: ProjectgroupService = TestBed.get(ProjectgroupService);
    spyOn(groupService, 'createProject').and.callThrough();
    const notService: NotificationService = TestBed.get(NotificationService);
    spyOn(notService, 'send');

    component.authMethod = AuthMethods.None;
    component.addProject();
    expect(groupService.createProject).toHaveBeenCalledWith(123, '', null, 'Git');
    expect(notService.send).toHaveBeenCalledWith('The project is now being processed.', 'info');
  });

  it('should add project ssh auth', () => {
    const groupService: ProjectgroupService = TestBed.get(ProjectgroupService);
    spyOn(groupService, 'createProject').and.callThrough();
    const notService: NotificationService = TestBed.get(NotificationService);
    spyOn(notService, 'send');

    component.authMethod = AuthMethods.SSH;
    const sshKey = 'adasdasdadadsdasdas';
    component.sshKey = sshKey;
    component.addProject();
    expect(groupService.createProject).toHaveBeenCalledWith(123, '', { sshKey: sshKey }, 'Git');
    expect(notService.send).toHaveBeenCalledWith('The project is now being processed.', 'info');
  });

  it('should add project serviceaccount auth', () => {
    const groupService: ProjectgroupService = TestBed.get(ProjectgroupService);
    spyOn(groupService, 'createProject').and.callThrough();
    const notService: NotificationService = TestBed.get(NotificationService);
    spyOn(notService, 'send');

    component.authMethod = AuthMethods.ServiceAccount;
    const username = 'asdf';
    const password = 'pswd';
    component.username = username;
    component.password = password;
    component.addProject();
    expect(groupService.createProject).toHaveBeenCalledWith(123, '', { username: username, password: password }, 'Git');
    expect(notService.send).toHaveBeenCalledWith('The project is now being processed.', 'info');
  });
});
