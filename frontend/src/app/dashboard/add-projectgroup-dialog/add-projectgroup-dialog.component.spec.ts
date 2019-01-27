import { async, ComponentFixture, TestBed, getTestBed } from '@angular/core/testing';

import { AddProjectgroupDialogComponent } from './add-projectgroup-dialog.component';
import { FormsModule } from '@angular/forms';
import { ProjectgroupService } from 'src/app/projectgroup.service';
import { ProjectgroupServiceStub } from 'src/testing/projectgroup.service.stub';
import { Router } from '@angular/router';
import { DashboardServiceStub } from 'src/testing/dashboard-service-stub';
import { DashboardService } from '../dashboard.service';

describe('AddProjectgroupDialogComponent', () => {
  let component: AddProjectgroupDialogComponent;
  let fixture: ComponentFixture<AddProjectgroupDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AddProjectgroupDialogComponent],
      imports: [FormsModule],
      providers: [
        { provide: ProjectgroupService, useClass: ProjectgroupServiceStub },
        { provide: Router, useValue: null },
        { provide: DashboardService, useClass: DashboardServiceStub }
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddProjectgroupDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should clear input', () => {
    component.groupName = 'asd';
    component.clearInput();
    expect(component.groupName).toBe('');
  });

  it('should add projectgroup', () => {
    const groupName = 'group';
    const dashService: DashboardServiceStub = TestBed.get(DashboardService);
    spyOn(dashService, 'addProjGroup');
    component.groupName = groupName;
    component.addProjectgroup();
    expect(dashService.addProjGroup).toHaveBeenCalled();
  });
});
