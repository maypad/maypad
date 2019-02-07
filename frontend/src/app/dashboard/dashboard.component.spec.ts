import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DashboardComponent } from './dashboard.component';
import { ProjectgroupDetailComponent } from './projectgroup-detail/projectgroup-detail.component';
import { AddProjectgroupDialogComponent } from './add-projectgroup-dialog/add-projectgroup-dialog.component';
import { ProjectListItemComponent } from './projectgroup-detail/project-list-item/project-list-item.component';
import { FormsModule } from '@angular/forms';
import { AddProjectDialogComponent } from './projectgroup-detail/add-project-dialog/add-project-dialog.component';
import { EditProjectgroupDialogComponent } from './projectgroup-detail/edit-projectgroup-dialog/edit-projectgroup-dialog.component';
import { RouterTestingModule } from '@angular/router/testing';
import { ProjectgroupService } from '../projectgroup.service';
import { ProjectgroupServiceStub } from 'src/testing/projectgroup.service.stub';
import { Projectgroup } from '../model/projectGroup';
import * as get_projectgroups_response from 'sample-requests/get.projectgroups.response.json';
import { ActivatedRoute, ActivatedRouteSnapshot } from '@angular/router';
import { of } from 'rxjs';
import { EnumToArrayPipe } from '../enum-to-array.pipe';

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  const response: Projectgroup[] = get_projectgroups_response['default'];
  const snapshot = new ActivatedRouteSnapshot();

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [DashboardComponent, ProjectgroupDetailComponent,
        AddProjectgroupDialogComponent, ProjectListItemComponent,
        AddProjectDialogComponent, EditProjectgroupDialogComponent, EnumToArrayPipe],
      imports: [FormsModule, RouterTestingModule],
      providers: [
        { provide: ProjectgroupService, useClass: ProjectgroupServiceStub },
        {
          // Mock ActivatedRoute because a unit test can't have a "real" route
          provide: ActivatedRoute, useClass: class {
            snapshot = snapshot; data = of({ groups: get_projectgroups_response['default'] });
          }
        }]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should list projectgroups', () => {
    const nativeElement: HTMLElement = fixture.nativeElement;
    const groupDetails: NodeListOf<Element> = nativeElement.querySelectorAll('app-projectgroup-detail');

    expect(groupDetails.length).toBe(response.length);
    expect(groupDetails[0].querySelector('h4').textContent).toBe(response[0].name);
    expect(groupDetails[1].querySelector('h4').textContent).toBe(response[1].name);
  });

  it('should create button', () => {
    const nativeElement: HTMLElement = fixture.nativeElement;
    expect(nativeElement.querySelector('button').textContent).toBe('Toggle All');
  });
});
