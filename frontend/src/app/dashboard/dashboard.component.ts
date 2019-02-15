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
    const initHandler = (e: MessageEvent) => { this.setProjectInfo(e); };
    const refreshHandler = (e: MessageEvent) => { this.refreshProject(e); };
    this.evtSource.addEventListener('project_init', initHandler);
    this.evtSource.addEventListener('project_refreshed', refreshHandler);
    this.evtSource.addEventListener('build_updated', refreshHandler);
    // Remove event listeners when navigating away
    this.routeSubscription = this.router.events.subscribe((event) => {
      if (event instanceof NavigationStart) {
        this.evtSource.removeEventListener('project_init', initHandler);
        this.evtSource.removeEventListener('project_refreshed', refreshHandler);
        this.evtSource.removeEventListener('build_updated', refreshHandler);
        this.routeSubscription.unsubscribe();
        this.evtSource.close();
      }
    });
  }

  setProjectInfo(e: MessageEvent) {
    const data = JSON.parse(e.data);
    this.projectGroups.forEach((group) => {
      group.projects.forEach((proj) => {
        if (proj.id === data['projectId']) {
          proj.name = data['name'];
          this.notificationService.send(`Project(${proj.id}): ${proj.name} has been initialized.`, 'success');
        }
      });
    });
  }

  refreshProject(e: MessageEvent) {
    const data = JSON.parse(e.data);
    this.projService.loadProject(data['projectId']).subscribe((proj: Project) => {
      this.projectGroups.forEach((group) => {
        group.projects.forEach((existingProject) => {
          if (existingProject.id === proj.id) {
            existingProject.status = proj.status;
            existingProject.name = proj.name;
          }
        });
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
