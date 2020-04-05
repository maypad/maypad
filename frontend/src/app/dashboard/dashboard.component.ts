import { Component, OnInit, ViewChild } from '@angular/core';
import { BreadcrumbService } from '../breadcrumb.service';
import { Projectgroup } from '../model/projectGroup';
import { AddProjectgroupDialogComponent } from './add-projectgroup-dialog/add-projectgroup-dialog.component';
import { DashboardService } from './dashboard.service';
import { ActivatedRoute, NavigationStart, Router } from '@angular/router';
import { NotificationService } from '../notification.service';
import { Subscription } from 'rxjs';
import { ProjectService } from '../project.service';
import { Project } from '../model/project';
import { BuildStatus } from '../model/buildStatus';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  @ViewChild('addGroupDialog') modal: AddProjectgroupDialogComponent;
  projectGroups: Projectgroup[];
  showAll = true;
  evtSource: EventSource;
  routeSubscription: Subscription;

  constructor(private crumbs: BreadcrumbService,
    private route: ActivatedRoute,
    private dashService: DashboardService,
    private notificationService: NotificationService,
    private router: Router,
    private projService: ProjectService) { }

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

    this.evtSource = new EventSource('/sse');
    const refreshHandler = (e: MessageEvent) => { this.handleProjectRefreshed(e); };
    const changedHandler = (e: MessageEvent) => { this.handleProjectChanged(e); };
    const buildHandler = (e: MessageEvent) => { this.handleBuildUpdated(e); };
    this.evtSource.addEventListener('project_refreshed', refreshHandler);
    this.evtSource.addEventListener('project_changed', changedHandler);
    this.evtSource.addEventListener('build_updated', buildHandler);
    // Remove event listeners when navigating away
    this.routeSubscription = this.router.events.subscribe((event) => {
      if (event instanceof NavigationStart) {
        this.evtSource.removeEventListener('project_refreshed', refreshHandler);
        this.evtSource.removeEventListener('project_changed', changedHandler);
        this.evtSource.removeEventListener('build_updated', buildHandler);
        this.routeSubscription.unsubscribe();
        this.evtSource.close();
      }
    });
  }

  handleProjectRefreshed(e: MessageEvent) {
    const data = JSON.parse(e.data);
    this.projService.loadProject(data['projectId']).subscribe((proj: Project) => {
      this.projectGroups.forEach((group) => {
        group.projects.forEach((existingProject) => {
          if (existingProject.id === proj.id) {
            existingProject.status = proj.status;
            existingProject.name = proj.name;
            if (data['status'] === 'SUCCESS') {
              this.notificationService.sendSuccess(data['message'], undefined, data['projectId'], String(group.id));
            } else {
              this.notificationService.sendWarning(data['message'], undefined, data['projectId'], String(group.id));
            }
          }
        });
      });
    });
  }

  handleProjectChanged(e: MessageEvent) {
    const data = JSON.parse(e.data);
    this.notificationService.sendInfo(data['message'], undefined, data['projectId'], data['projectgroupId']);
  }

  handleBuildUpdated(e: MessageEvent) {
    const data = JSON.parse(e.data);
    this.projectGroups.forEach((group) => {
      group.projects.forEach((proj) => {
        if (proj.id === data['projectId']) {
          proj.status = (<any>BuildStatus)[data['status']];
          switch (proj.status) {
            case BuildStatus.SUCCESS:
              this.notificationService.sendSuccess('build_success', data['name'], data['projectId'], data['projectgroupId']);
              break;
            case BuildStatus.FAILED:
              this.notificationService.sendWarning(data['message'], data['name'], data['projectId'], data['projectgroupId']);
              break;
            case BuildStatus.RUNNING:
              this.notificationService.sendInfo('build_running', data['name'], data['projectId'], data['projectgroupId']);
              break;
            case BuildStatus.UNKNOWN:
              this.notificationService.sendWarning('build_unknown', data['name'], data['projectId'], data['projectgroupId']);
              break;
          }
        }
      });
    });
  }

  toggleGroups() {
    if (this.showAll) {
      $('.collapse').collapse('hide');
      this.showAll = !this.showAll;
    } else {
      $('.collapse').collapse('show');
      this.showAll = !this.showAll;
    }
  }
}
