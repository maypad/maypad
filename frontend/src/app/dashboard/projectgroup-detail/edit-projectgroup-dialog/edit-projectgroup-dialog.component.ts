import { Component, OnInit, Input, ViewChild, ElementRef } from '@angular/core';
import { Projectgroup } from 'src/app/model/projectGroup';
import { ProjectgroupService } from 'src/app/projectgroup.service';
import { Router } from '@angular/router';
import { DashboardService } from '../../dashboard.service';

@Component({
  selector: 'app-edit-projectgroup-dialog',
  templateUrl: './edit-projectgroup-dialog.component.html',
  styleUrls: ['./edit-projectgroup-dialog.component.css']
})
export class EditProjectgroupDialogComponent implements OnInit {
  @Input() projGroup: Projectgroup;
  newName = '';
  constructor(private groupService: ProjectgroupService,
    private router: Router,
    private dashService: DashboardService) { }

  ngOnInit() {
    this.newName = this.projGroup.name;
  }

  clearInput() {
    this.newName = this.projGroup.name;
  }

  updateProjectgroup() {
    if (this.newName === '') {
      alert(`Error: Group name can't be empty`);
      return;
    }
    this.groupService.updateProjectgroup(this.projGroup.id, this.newName).subscribe(
      // In case of the backend not accepting the new name
      group => {
        this.projGroup.name = group.name;
        this.newName = group.name;
      }
    );
  }

  deleteProjectgroup() {
    this.groupService.deleteProjectgroup(this.projGroup.id).subscribe(
      () => { this.dashService.deleteProjGroup(this.projGroup); }
    );
  }

}
