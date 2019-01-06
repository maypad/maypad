import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { BreadcrumbService } from '../breadcrumb.service';
import { Projectgroup } from '../model/projectGroup';
import { Project } from '../model/project';
import { BuildStatus } from '../model/buildStatus';
import { Branch } from '../model/branch';
import { ServiceAccount } from '../model/serviceAccount';
import { AddProjectgroupDialogComponent } from './add-projectgroup-dialog/add-projectgroup-dialog.component';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  @ViewChild('addGroupDialog') modal: AddProjectgroupDialogComponent;
  projectGroups: Projectgroup[];

  constructor(private crumbs: BreadcrumbService) { }

  ngOnInit() {
    this.crumbs.setBreadcrumbs([]);
    // Mocking api
    const sAccount: ServiceAccount = { sshKey: 'asd123' };
    const branch: Branch = null;
    const proj: Project = {
      name: 'Project Beta', id: 42,
      repositoryURL: 'testgit.com/repo.git', branches: [branch],
      serviceAccount: sAccount, status: BuildStatus.SUCCESS
    };
    const proj2: Project = {
      name: 'Project Delta', id: 19,
      repositoryURL: 'testgit.com/repo.git', branches: [branch],
      serviceAccount: sAccount, status: BuildStatus.FAILED
    };
    this.projectGroups = [{
      name: 'Group Alpha', id: 123,
      projects: [proj, proj2, proj], status: BuildStatus.SUCCESS
    }, {
      name: 'Group Gamma', id: 124,
      projects: [proj, proj, proj2], status: BuildStatus.SUCCESS
    }];
  }

  clearInput(event: FocusEvent) {
    if (event.relatedTarget == null) {
      this.modal.clearInput();
    }
  }
}
