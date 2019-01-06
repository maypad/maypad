import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectListItemComponent } from './project-list-item.component';
import { RouterTestingModule } from '@angular/router/testing';
import { Project } from 'src/app/model/project';
import { BuildStatus } from 'src/app/model/buildStatus';

describe('ProjectListItemComponent', () => {
  let component: ProjectListItemComponent;
  let fixture: ComponentFixture<ProjectListItemComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ProjectListItemComponent],
      imports: [RouterTestingModule]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectListItemComponent);
    component = fixture.componentInstance;
    const proj: Project = {
      name: 'Project Beta', id: 42,
      repositoryURL: 'testgit.com/repo.git', branches: [],
      serviceAccount: null, status: BuildStatus.SUCCESS
    };
    component.project = proj;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
