import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BreadcrumbService } from '../breadcrumb.service';
import { Project } from '../model/project';
import { Branch } from '../model/branch';
import { EditProjectDialogComponent } from './edit-project-dialog/edit-project-dialog.component';

@Component({
  selector: 'app-project-detail',
  templateUrl: './project-detail.component.html',
  styleUrls: ['./project-detail.component.css']
})
export class ProjectDetailComponent implements OnInit {
  @ViewChild('editDialog') editDialog: EditProjectDialogComponent;
  project: Project;
  branches: Branch[];
  constructor(private route: ActivatedRoute, private crumbs: BreadcrumbService) { }

  ngOnInit() {
    this.route.data.subscribe(
      (data: { project: Project, branches: Branch[] }) => {
        this.project = data.project;
        this.crumbs.setBreadcrumbs([{ name: this.project.name, path: 'projects/' + this.project.id }]);
        this.branches = data.branches;
      }
    );
  }

  clearInput(event: FocusEvent) {
    if (event.relatedTarget == null) {
      this.editDialog.initServiceMethod();
    }
  }

}
