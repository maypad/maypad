import { Component, OnInit, ViewChild } from '@angular/core';
import { BreadcrumbService } from '../breadcrumb.service';
import { Projectgroup } from '../model/projectGroup';
import { AddProjectgroupDialogComponent } from './add-projectgroup-dialog/add-projectgroup-dialog.component';
import { DashboardService } from './dashboard.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  @ViewChild('addGroupDialog') modal: AddProjectgroupDialogComponent;
  projectGroups: Projectgroup[];

  constructor(private crumbs: BreadcrumbService,
    private route: ActivatedRoute,
    private dashService: DashboardService) { }

  ngOnInit() {
    this.crumbs.setBreadcrumbs([]);
    this.route.data.subscribe(
      (data: { groups: Projectgroup[] }) => {
        this.projectGroups = data.groups;
      }
    );
    this.dashService.subjectNewGroup.subscribe(group => { this.projectGroups.push(group); });
    this.dashService.subjectDeleteGroup.subscribe(group => {
      const index = this.projectGroups.indexOf(group, 0);
      if (index > -1) {
        this.projectGroups.splice(index, 1);
      }
    });
    $('#addGroupModal').on('hidden.bs.modal', () => {
      this.modal.clearInput();
    });
  }

  toggleGroups(action) {
    $('.collapse').collapse(action);
  }
}
