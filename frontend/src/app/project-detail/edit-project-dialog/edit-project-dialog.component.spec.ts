import { async, ComponentFixture, TestBed, getTestBed } from '@angular/core/testing';

import { EditProjectDialogComponent } from './edit-project-dialog.component';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { RouterTestingModule } from '@angular/router/testing';
import * as response from 'sample-requests/get.projects.id.response.json';
import { KeyServiceAccount, UserServiceAccount } from 'src/app/model/serviceAccount';
import { ProjectService } from 'src/app/project.service';
import { ProjectServiceStub } from 'src/testing/project-service-stub';
import { ProjectgroupService } from 'src/app/projectgroup.service';
import { ProjectgroupServiceStub } from 'src/testing/projectgroup.service.stub';
import { Router } from '@angular/router';

describe('EditProjectDialogComponent', () => {
  let component: EditProjectDialogComponent;
  let fixture: ComponentFixture<EditProjectDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EditProjectDialogComponent],
      imports: [FormsModule, HttpClientModule, RouterTestingModule],
      providers: [
        {
          provide: ProjectService, useClass: ProjectServiceStub
        },
        {
          provide: ProjectgroupService, useClass: ProjectgroupServiceStub
        }
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EditProjectDialogComponent);
    component = fixture.componentInstance;
    component.project = response['default'];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should init service accounts', () => {
    const testname = 'testname';
    const uAcc = new UserServiceAccount();
    uAcc.username = testname;
    component.project.serviceAccount = uAcc;
    component.initServiceMethod();
    expect(component.serviceType).toBe(3);
    expect(component.username).toBe(testname);

    const key = 'testKey';
    const sAcc = new KeyServiceAccount();
    sAcc.sshKey = key;
    component.project.serviceAccount = sAcc;
    component.initServiceMethod();
    expect(component.serviceType).toBe(2);
    expect(component.sshKey).toBe(key);

    component.project.serviceAccount = null;
    component.initServiceMethod();
    expect(component.serviceType).toBe(1);
  });

  it('should select', () => {
    component.setSelected(2);
    expect(component.serviceType).toBe(2);
  });

  it('should update project', () => {
    component.serviceType = 1;
    component.updateProject();
    expect(component.project.serviceAccount).toBe(null);
    expect(component.project.id).toBe(response['default'].id);

    component.serviceType = 2;
    const testKey = 'testKey';
    component.sshKey = testKey;
    component.updateProject();
    expect((<KeyServiceAccount>component.project.serviceAccount).sshKey).toBe(testKey);
    expect(component.project.id).toBe(response['default'].id);

    component.serviceType = 3;
    const testuser = 'testuser';
    component.username = testuser;
    component.updateProject();
    expect((<UserServiceAccount>component.project.serviceAccount).username).toBe(testuser);
    expect(component.project.id).toBe(response['default'].id);

    component.serviceType = 4;
    component.updateProject();
    expect(component.serviceType).toBe(4);
  });

  it('should delete project', () => {
    const router = getTestBed().get(Router);
    component.deleteProject();
    expect(router.url).toBe('/');
  });
});
