import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HeaderComponent } from './header.component';
import { HttpClientModule } from '@angular/common/http';
import * as branchesResponse from 'sample-requests/get.projects.id.branches.ref.response.json';
import { BranchService } from 'src/app/branch.service';
import { BranchServiceStub } from 'src/testing/branch-service-stub';
import { NotificationService } from 'src/app/notification.service';
import { NotificationServiceStub } from 'src/testing/notification-service-stub';
import { Observable, throwError } from 'rxjs';

describe('HeaderComponent', () => {
  let component: HeaderComponent;
  let fixture: ComponentFixture<HeaderComponent>;
  const branch = branchesResponse['default'];

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [HeaderComponent],
      imports: [HttpClientModule],
      providers: [
        {
          provide: BranchService, useClass: BranchServiceStub
        },
        {
          provide: NotificationService, useClass: NotificationServiceStub
        }
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HeaderComponent);
    component = fixture.componentInstance;
    component.projId = 2;
    component.branch = branch;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should error trigger deployment', () => {
    const notificationService = TestBed.get(NotificationService);
    const branchService: BranchServiceStub = TestBed.get(BranchService);
    spyOn(branchService, 'triggerDeployment').and.returnValue(throwError('error'));
    spyOn(notificationService, 'send');
    component.triggerDeploy(true);
    expect(notificationService.send).toHaveBeenCalledWith(`Deployment couldn't be started. see console for error log.`, 'danger');
  });

  it('should trigger deployment', () => {
    const notificationService = TestBed.get(NotificationService);
    spyOn(notificationService, 'send');
    component.triggerDeploy(false);
    expect(notificationService.send).toHaveBeenCalledWith('The deployment has been started.', 'info');
  });

  it('should trigger build', () => {
    const notificationService = TestBed.get(NotificationService);
    spyOn(notificationService, 'send');
    component.triggerBuild();
    expect(notificationService.send).toHaveBeenCalledWith('The build has been started.', 'info');
  });

  it('should error trigger build', () => {
    const notificationService = TestBed.get(NotificationService);
    const branchService: BranchServiceStub = TestBed.get(BranchService);
    spyOn(branchService, 'triggerBuild').and.returnValue(throwError('error'));
    spyOn(notificationService, 'send');
    component.rebuild.nativeElement.checked = true;
    component.triggerBuild();
    expect(notificationService.send).toHaveBeenCalledWith(`Build couldn't be started. see console for error log.`, 'danger');
  });

  it('should build and deploy', () => {
    const notificationService = TestBed.get(NotificationService);
    const branchService: BranchServiceStub = TestBed.get(BranchService);
    spyOn(branchService, 'triggerDeployment').and.returnValue(throwError('error'));
    spyOn(notificationService, 'send');
    component.buildAndDeploy();
    expect(notificationService.send).toHaveBeenCalledWith(`Deployment couldn't be started. see console for error log.`, 'danger');
  });
});
