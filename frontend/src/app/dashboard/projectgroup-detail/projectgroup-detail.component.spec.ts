import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectgroupDetailComponent } from './projectgroup-detail.component';
import { ProjectListItemComponent } from './project-list-item/project-list-item.component';
import { AddProjectDialogComponent } from './add-project-dialog/add-project-dialog.component';
import { EditProjectgroupDialogComponent } from './edit-projectgroup-dialog/edit-projectgroup-dialog.component';
import { RouterTestingModule } from '@angular/router/testing';
import { FormsModule } from '@angular/forms';
import { ProjectgroupService } from 'src/app/projectgroup.service';
import { ProjectgroupServiceStub } from 'src/testing/projectgroup.service.stub';
import { Projectgroup } from 'src/app/model/projectGroup';
import * as get_projectgroups_response from 'sample-requests/get.projectgroups.response.json';
import * as get_projectgroups_id_projects_response from 'sample-requests/get.projectgroups.id.projects.response.json';
import { Project } from 'src/app/model/project';
import { EnumToArrayPipe } from 'src/app/enum-to-array.pipe';

describe('ProjectgroupDetailComponent', () => {
  let component: ProjectgroupDetailComponent;
  let fixture: ComponentFixture<ProjectgroupDetailComponent>;
  const response: Projectgroup = get_projectgroups_response['default'][0];
  const projects: Project[] = get_projectgroups_id_projects_response['default'];

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ProjectgroupDetailComponent, ProjectListItemComponent,
        AddProjectDialogComponent, EditProjectgroupDialogComponent, EnumToArrayPipe],
      imports: [RouterTestingModule, FormsModule],
      providers: [{ provide: ProjectgroupService, useClass: ProjectgroupServiceStub }]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectgroupDetailComponent);
    component = fixture.componentInstance;
    component.projGroup = get_projectgroups_response['default'][0];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show unloaded info', () => {
    const nativeElement: HTMLElement = fixture.nativeElement;
    const buttons: NodeListOf<Element> = nativeElement.querySelectorAll('button');

    expect(nativeElement.querySelector('h4').textContent).toBe(response.name);
    expect(buttons[0].textContent).toBe('Edit');
    expect(buttons[1].textContent).toBe('Add Project');
  });

  it('should show projectgroup info', () => {
    component.loadGroup();
    fixture.detectChanges();
    const nativeElement: HTMLElement = fixture.nativeElement;
    const items: NodeListOf<Element> = nativeElement.querySelectorAll('app-project-list-item');

    expect(nativeElement.querySelector('h4').textContent).toBe(response.name);
    expect(items.length).toBe(projects.length);
    for (let i = 0; i < items.length; i++) {
      expect(items[i].querySelectorAll('div')[1].textContent).toBe(projects[i].name);
    }
  });
});
