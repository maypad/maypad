import { Component, OnInit } from '@angular/core';
import { Deployment } from '../model/deployment';
import { Branch } from '../model/branch';
import { ActivatedRoute } from '@angular/router';
import { BreadcrumbService } from '../breadcrumb.service';
import { Project } from '../model/project';

@Component({
  selector: 'app-deployment-history',
  templateUrl: './deployment-history.component.html',
  styleUrls: ['./deployment-history.component.css']
})
export class DeploymentHistoryComponent implements OnInit {
  deployments: Deployment[];
  branch: Branch;
  project: Project;
  constructor(private route: ActivatedRoute, private crumbs: BreadcrumbService) { }

  ngOnInit() {
    this.route.data.subscribe((data: { deployments: Deployment[], branch: Branch, project: Project }) => {
      this.deployments = data.deployments;
      this.branch = data.branch;
      this.project = data.project;
      this.setBreadcrumbs();
    });
  }

  setBreadcrumbs() {
    this.crumbs.setBreadcrumbs([
      { name: this.project.name, path: '/projects/' + this.project.id },
      { name: this.branch.name, path: '/projects/' + this.project.id + '/branches/' + this.branch.name },
      { name: 'Deployment History', path: '/projects/' + this.project.id + '/branches/' + this.branch.name + '/deploymenthistory' }
    ]);
  }
}
