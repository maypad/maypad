import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BreadcrumbService } from '../breadcrumb.service';
import { Project } from '../model/project';
import { Branch } from '../model/branch';
import { EditProjectDialogComponent } from './edit-project-dialog/edit-project-dialog.component';
import { ProjectService } from '../project.service';

@Component({
  selector: 'app-project-detail',
  templateUrl: './project-detail.component.html',
  styleUrls: ['./project-detail.component.css']
})
export class ProjectDetailComponent implements OnInit {
  @ViewChild('editDialog') editDialog: EditProjectDialogComponent;
  project: Project;
  branches: Branch[];
  constructor(
    private route: ActivatedRoute,
    private crumbs: BreadcrumbService,
    private projectService: ProjectService) { }

  ngOnInit() {
    $('#editProjectModal').on('hidden.bs.modal', () => {
      // Clear input
      this.editDialog.initServiceMethod();
    });
    this.route.data.subscribe(
      (data: { project: Project, branches: Branch[] }) => {
        this.project = data.project;
        this.crumbs.setBreadcrumbs([
          {
            name: this.project.name ? this.project.name : 'Error',
            path: 'projects/' + this.project.id
          }
        ]);
        this.branches = data.branches;
      }
    );
  }

  refreshProject() {
    this.projectService.refreshProject(this.project.id).subscribe(
      res => {
        if (res) {
          const hulla = new hullabaloo();
          hulla.options.align = 'center';
          hulla.options.width = 400;
          hulla.options.offset = { from: 'top', amount: 30 };
          hulla.send('The project is now being refreshed.', 'info');
        }
      });
  }
}
