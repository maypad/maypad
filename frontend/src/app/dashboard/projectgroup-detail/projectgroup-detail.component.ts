import { Component, OnInit, Input, ViewChild } from '@angular/core';
import { Projectgroup } from 'src/app/model/projectGroup';
import { AddProjectDialogComponent } from './add-project-dialog/add-project-dialog.component';
import { EditProjectgroupDialogComponent } from './edit-projectgroup-dialog/edit-projectgroup-dialog.component';

@Component({
  selector: 'app-projectgroup-detail',
  templateUrl: './projectgroup-detail.component.html',
  styleUrls: ['./projectgroup-detail.component.css']
})
export class ProjectgroupDetailComponent implements OnInit {
  @Input() projGroup: Projectgroup;
  @ViewChild('addProjectDialog{{projGroup.id}}') addModal: AddProjectDialogComponent;
  @ViewChild('editProjectgroupDialog{{projGroup.id}}') editModal: EditProjectgroupDialogComponent;
  constructor() { }

  ngOnInit() {
  }

  clearInput(event: FocusEvent) {
    if (event.relatedTarget == null) {
      this.addModal.clearInput();
      this.editModal.clearInput();
    }
  }
}
