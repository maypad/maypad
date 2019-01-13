import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectListItemComponent } from './project-list-item.component';
import { RouterTestingModule } from '@angular/router/testing';
import * as get_projectgroups_id_projects_response from 'sample-requests/get.projectgroups.id.projects.response.json';
import { Project } from 'src/app/model/project';

describe('ProjectListItemComponent', () => {
  let component: ProjectListItemComponent;
  let fixture: ComponentFixture<ProjectListItemComponent>;
  const response: Project = get_projectgroups_id_projects_response['default'][0];

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
    component.project = response;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should list project details', () => {
    const nativeElement: HTMLElement = fixture.nativeElement;

    expect(nativeElement.querySelectorAll('div')[1].textContent).toBe(response.name);
    expect(nativeElement.querySelectorAll('div')[2].textContent).toBe('(#' + response.id.toString() + ')');
    expect(nativeElement.querySelectorAll('div')[3].textContent).toBe(' Build Status: ');
  });
});
