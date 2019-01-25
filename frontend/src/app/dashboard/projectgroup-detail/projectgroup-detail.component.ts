import { Component, OnInit, Input, ViewChild } from '@angular/core';
import { Projectgroup } from 'src/app/model/projectGroup';
import { AddProjectDialogComponent } from './add-project-dialog/add-project-dialog.component';
import { EditProjectgroupDialogComponent } from './edit-projectgroup-dialog/edit-projectgroup-dialog.component';
import { ProjectgroupService } from 'src/app/projectgroup.service';
import { Project } from 'src/app/model/project';

@Component({
  selector: 'app-projectgroup-detail',
  templateUrl: './projectgroup-detail.component.html',
  styleUrls: ['./projectgroup-detail.component.css']
})
export class ProjectgroupDetailComponent implements OnInit {
  @Input() projGroup: Projectgroup;
  @ViewChild('addProjectDialog{{projGroup.id}}') addModal: AddProjectDialogComponent;
  @ViewChild('editProjectgroupDialog{{projGroup.id}}') editModal: EditProjectgroupDialogComponent;
  constructor(private groupService: ProjectgroupService) { }

  ngOnInit() {
    this.loadGroup();
    $(`#addProjectModal${this.projGroup.id}`).on('hidden.bs.modal', () => {
      this.addModal.clearInput();
    });
    $(`#editProjectgroupModal${this.projGroup.id}`).on('hidden.bs.modal', () => {
      this.editModal.clearInput();
    });
  }

  loadGroup() {
    this.groupService.loadProjects(this.projGroup.id).subscribe(
      projects => {
        this.projGroup.projects = projects.sort((a: Project, b: Project) => {
          return a.id < b.id ? -1 : 1;
        });
      });
  }
}
