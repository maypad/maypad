import { Component, OnInit, Input, ViewChild, ElementRef } from '@angular/core';
import { ProjectgroupService } from 'src/app/projectgroup.service';
import { Router } from '@angular/router';
import { DashboardService } from '../dashboard.service';

@Component({
  selector: 'app-add-projectgroup-dialog',
  templateUrl: './add-projectgroup-dialog.component.html',
  styleUrls: ['./add-projectgroup-dialog.component.css']
})
export class AddProjectgroupDialogComponent implements OnInit {
  groupName = '';
  constructor(private groupService: ProjectgroupService,
    private router: Router,
    private dashService: DashboardService) { }

  ngOnInit() {
  }

  addProjectgroup() {
    this.groupService.createProjectgroup(this.groupName).subscribe(
      (group) => {
        if (group) {
          this.dashService.addProjGroup(group);
        }
      }
    );
  }

  clearInput() {
    this.groupName = '';
  }
}
