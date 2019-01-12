import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { BreadcrumbService } from '../breadcrumb.service';
import { Projectgroup } from '../model/projectGroup';
import { AddProjectgroupDialogComponent } from './add-projectgroup-dialog/add-projectgroup-dialog.component';
import { ProjectgroupService } from '../projectgroup.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  @ViewChild('addGroupDialog') modal: AddProjectgroupDialogComponent;
  projectGroups: Projectgroup[];
  finishedLoading = false;
  constructor(private crumbs: BreadcrumbService, private groupService: ProjectgroupService) { }

  ngOnInit() {
    this.crumbs.setBreadcrumbs([]);
    this.groupService.loadProjectgroups().subscribe(
      groups => {
        this.projectGroups = groups;
        this.finishedLoading = true;
      }
    );
  }

  clearInput(event: FocusEvent) {
    if (event.relatedTarget == null) {
      this.modal.clearInput();
    }
  }
}
