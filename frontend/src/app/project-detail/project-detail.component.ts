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
    // Need to be able to remove the event listeners again
    const refreshHandler = (e: MessageEvent) => { this.reloadProject(e); };
    const buildUpdateHandler = (e: MessageEvent) => { this.updateBuildStatus(e); };
    this.evtSource.addEventListener('project_refreshed', refreshHandler);
    this.evtSource.addEventListener('auth_updated', refreshHandler);
    this.evtSource.addEventListener('build_updated', buildUpdateHandler);
    // Remove event listeners when navigating away
    this.routeSubscription = this.router.events.subscribe((event) => {
      if (event instanceof NavigationStart) {
        this.evtSource.removeEventListener('project_refreshed', refreshHandler);
        this.evtSource.removeEventListener('auth_updated', refreshHandler);
        this.evtSource.removeEventListener('build_updated', buildUpdateHandler);
        this.routeSubscription.unsubscribe();
        this.evtSource.close();
      }
    });
  }

  reloadProject(e: MessageEvent) {
    const data = JSON.parse(e.data);
    if (data['projectId'] === this.project.id) {
      this.projectService.loadProject(this.project.id).subscribe((proj) => {
        this.project = proj;
        this.notificationService.send('The project has been refreshed.', 'success');
      });
    }
  }

  updateBuildStatus(e: MessageEvent) {
    const data = JSON.parse(e.data);
    if (data['projectId'] === this.project.id) {
      this.branches.forEach((branch) => {
        if (branch.name === data['name']) {
          branch.status = (<any>BuildStatus)[data['status']];
        }
      });
    }
  }

  refreshProject() {
    this.projectService.refreshProject(this.project.id).subscribe(
      res => {
        if (res === null) {
          this.notificationService.send('The project is now being refreshed.', 'info');
        }
      });
  }
}
