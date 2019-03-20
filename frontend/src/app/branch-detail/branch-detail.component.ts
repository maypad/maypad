import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, NavigationStart } from '@angular/router';
import { BreadcrumbService } from '../breadcrumb.service';
import { Branch } from '../model/branch';
import { Project } from '../model/project';
import { Subscription } from 'rxjs';
import { NotificationService } from '../notification.service';
import { BuildStatus } from '../model/buildStatus';

@Component({
  selector: 'app-branch-detail',
  templateUrl: './branch-detail.component.html',
  styleUrls: ['./branch-detail.component.css']
})
export class BranchDetailComponent implements OnInit {
  branch: Branch;
  project: Project;
  fmtTimestamp: string;
  evtSource: EventSource;
  routeSubscription: Subscription;
  constructor(private route: ActivatedRoute,
    private crumbs: BreadcrumbService,
    private router: Router,
    private notificationService: NotificationService) { }

  ngOnInit() {
    this.route.data.subscribe((data: { branch: Branch, project: Project }) => {
      this.branch = data.branch;
      this.project = data.project;
      this.crumbs.setBreadcrumbs([
        { name: this.project.name, path: '/projects/' + this.project.id },
        { name: this.branch.name, path: '/projects/' + this.project.id + '/branches/' + this.branch.name }
      ]);
    });
    const mom = moment(this.branch.lastCommit.timestamp, moment.ISO_8601);
    this.fmtTimestamp = mom.fromNow();

    this.evtSource = new EventSource('/sse');
    const buildUpdateHandler = (e: MessageEvent) => { this.handleBuildUpdated(e); };
    const deploymentUpdateHandler = (e: MessageEvent) => { this.handleDeploymentUpdated(e); };
    this.evtSource.addEventListener('build_updated', buildUpdateHandler);
    this.evtSource.addEventListener('deployment_updated', deploymentUpdateHandler);
    // Remove event listeners when navigating away
    this.routeSubscription = this.router.events.subscribe((event) => {
      if (event instanceof NavigationStart) {
        this.evtSource.removeEventListener('build_updated', buildUpdateHandler);
        this.evtSource.removeEventListener('deployment_updated', deploymentUpdateHandler);
        this.routeSubscription.unsubscribe();
        this.evtSource.close();
      }
    });
  }

  handleBuildUpdated(e: MessageEvent) {
    const data = JSON.parse(e.data);
    if (this.project.id === data['projectId']) {
      if (this.branch.name === data['name']) {
        const status = (<any>BuildStatus)[data['status']];
        switch (status) {
          case BuildStatus.SUCCESS:
            this.notificationService.sendSuccess('build_success', this.branch.name, String(this.project.id), undefined);
            break;
          case BuildStatus.FAILED:
            this.notificationService.sendWarning(data['message'], this.branch.name, String(this.project.id), undefined);
            break;
          case BuildStatus.RUNNING:
            this.notificationService.sendInfo('build_running', this.branch.name, String(this.project.id), undefined);
            break;
          case BuildStatus.UNKNOWN:
            this.notificationService.sendWarning('build_unknown', this.branch.name, String(this.project.id), undefined);
            break;
        }
      }
    }
  }

  handleDeploymentUpdated(e: MessageEvent) {
    const data = JSON.parse(e.data);
    if (this.project.id === data['projectId']) {
      if (this.branch.name === data['name']) {
        if (data['status'] === 'SUCCESS') {
          this.notificationService.sendSuccess('deployment_success', this.branch.name, String(this.project.id), undefined);
        } else {
          this.notificationService.sendWarning('deployment_failed', this.branch.name, String(this.project.id), undefined);
        }
      }
    }
  }
}
