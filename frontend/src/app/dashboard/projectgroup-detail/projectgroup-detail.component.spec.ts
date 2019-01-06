import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectgroupDetailComponent } from './projectgroup-detail.component';
import { ProjectListItemComponent } from './project-list-item/project-list-item.component';
import { AddProjectDialogComponent } from './add-project-dialog/add-project-dialog.component';
import { EditProjectgroupDialogComponent } from './edit-projectgroup-dialog/edit-projectgroup-dialog.component';
import { RouterTestingModule } from '@angular/router/testing';
import { FormsModule } from '@angular/forms';
import { Project } from 'src/app/model/project';
import { Projectgroup } from 'src/app/model/projectGroup';
import { BuildStatus } from 'src/app/model/buildStatus';

describe('ProjectgroupDetailComponent', () => {
  let component: ProjectgroupDetailComponent;
  let fixture: ComponentFixture<ProjectgroupDetailComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ProjectgroupDetailComponent, ProjectListItemComponent,
        AddProjectDialogComponent, EditProjectgroupDialogComponent],
      imports: [RouterTestingModule, FormsModule]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectgroupDetailComponent);
    component = fixture.componentInstance;
    const mockProj: Project = {
      name: 'Project Beta', id: 42,
      repositoryURL: 'testgit.com/repo.git', branches: [],
      serviceAccount: null, status: null
    };
    const mockGroup: Projectgroup = {
      name: 'Group Alpha', id: 123,
      projects: [mockProj], status: BuildStatus.SUCCESS
    };
    component.projGroup = mockGroup;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
