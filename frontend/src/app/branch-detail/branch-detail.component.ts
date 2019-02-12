import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, NavigationStart } from '@angular/router';
import { BreadcrumbService } from '../breadcrumb.service';
import { Branch } from '../model/branch';
import { Project } from '../model/project';
import { Subscription } from 'rxjs';
import { NotificationService } from '../notification.service';

@Component({
  selector: 'app-branch-detail',
  templateUrl: './branch-detail.component.html',
  styleUrls: ['./branch-detail.component.css']
})
export class BranchDetailComponent implements OnInit {
  branch: Branch;
  project: Project;
  fmtTimestamp: string;
  evtSource = new EventSource('/sse');
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

    const buildUpdateHandler = (e: MessageEvent) => { this.updateStatus(e, 'build'); };
    const deploymentUpdateHandler = (e: MessageEvent) => { this.updateStatus(e, 'deployment'); };
    this.evtSource.addEventListener('build_updated', buildUpdateHandler);
    this.evtSource.addEventListener('deployment_updated', deploymentUpdateHandler);
    // Remove event listeners when navigating away
    this.routeSubscription = this.router.events.subscribe((event) => {
      if (event instanceof NavigationStart) {
        this.evtSource.removeEventListener('build_updated', buildUpdateHandler);
        this.evtSource.removeEventListener('deployment_updated', deploymentUpdateHandler);
        this.routeSubscription.unsubscribe();
      }
    });
  }

  updateStatus(e: MessageEvent, type: string) {
    const data = JSON.parse(e.data);
    if (this.project.id === data['projectId']) {
      if (this.branch.name === data['name']) {
        const status = data['status'];
        let message;
        let notificationType;
        switch (status) {
          case 'SUCCESS':
            message = `A ${type} for this branch has been successful.`;
            notificationType = 'success';
            break;
          case 'FAILED':
            message = `A ${type} for this branch has failed.`;
            notificationType = 'danger';
            break;
          default:
            return;
        }
        this.notificationService.send(message, notificationType);
      }
    }
  }
}
