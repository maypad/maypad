import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router, NavigationStart } from '@angular/router';
import { BreadcrumbService } from '../breadcrumb.service';
import { Project } from '../model/project';
import { Branch } from '../model/branch';
import { EditProjectDialogComponent } from './edit-project-dialog/edit-project-dialog.component';
import { ProjectService } from '../project.service';
import { NotificationService } from '../notification.service';
import { BuildStatus } from '../model/buildStatus';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-project-detail',
  templateUrl: './project-detail.component.html',
  styleUrls: ['./project-detail.component.css']
})
export class ProjectDetailComponent implements OnInit {
  @ViewChild('editDialog') editDialog: EditProjectDialogComponent;
  project: Project;
  branches: Branch[];
  evtSource: EventSource;
  routeSubscription: Subscription;
  constructor(
    private route: ActivatedRoute,
    private crumbs: BreadcrumbService,
    private projectService: ProjectService,
    private notificationService: NotificationService,
    private router: Router) { }

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
    // Initialize tooltips
    $(function () {
      $('[data-toggle="tooltip"]').tooltip();
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
    if (data['status'] === 'SUCCESS') {
      if (data['projectId'] === this.project.id) {
        this.projectService.loadProject(this.project.id).subscribe((proj) => {
          this.project = proj;
          this.notificationService.sendSuccess(data['message'], undefined, data['projectId'], data['projectgroupId']);
        });
      }
    } else {
      this.notificationService.sendWarning(data['message'], undefined, data['projectId'], data['projectgroupId']);
    }
  }

  handleProjectChanged(e: MessageEvent) {
    const data = JSON.parse(e.data);
    if (data['projectId'] === this.project.id) {
      if (data['message'] === 'serviceaccount_changed') {
        this.projectService.loadProject(this.project.id).subscribe((proj) => {
          this.project = proj;
          this.notificationService.sendSuccess(data['message'], undefined, String(data['projectId']), data['projectgroupId']);
        });
      }
    }
  }

  handleBuildUpdated(e: MessageEvent) {
    const data = JSON.parse(e.data);
    if (data['projectId'] === this.project.id) {
      this.branches.forEach((branch) => {
        if (branch.name === data['name']) {
          branch.status = (<any>BuildStatus)[data['status']];
          switch (branch.status) {
            case BuildStatus.SUCCESS:
              this.notificationService.sendSuccess('build_success', branch.name, String(this.project.id), undefined);
              break;
            case BuildStatus.FAILED:
              this.notificationService.sendWarning(data['message'], branch.name, String(this.project.id), undefined);
              break;
            case BuildStatus.RUNNING:
              this.notificationService.sendInfo('build_running', branch.name, String(this.project.id), undefined);
              break;
            case BuildStatus.UNKNOWN:
              this.notificationService.sendWarning('build_unknown', branch.name, String(this.project.id), undefined);
              break;
          }
        }
      });
    }
  }

  refreshProject() {
    this.projectService.refreshProject(this.project.id).subscribe(
      res => {
        if (res === null) {
          this.notificationService.sendInfo('refreshing', undefined, String(this.project.id), undefined);
        }
      });
  }
}
