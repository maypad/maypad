import { Component, OnInit } from '@angular/core';
import { Deployment } from '../model/deployment';
import { Branch } from '../model/branch';
import { ActivatedRoute } from '@angular/router';
import { BreadcrumbService } from '../breadcrumb.service';

@Component({
  selector: 'app-deployment-history',
  templateUrl: './deployment-history.component.html',
  styleUrls: ['./deployment-history.component.css']
})
export class DeploymentHistoryComponent implements OnInit {
  deployments: Deployment[];
  branch: Branch;
  projId: number;
  constructor(private route: ActivatedRoute, private crumbs: BreadcrumbService) { }

  ngOnInit() {
    this.projId = parseInt(this.route.snapshot.paramMap.get('id'), 10);
    this.route.data.subscribe((data: { deplyoments: Deployment[], branch: Branch }) => {
      this.deployments = data.deplyoments;
      this.branch = data.branch;
      this.setBreadcrumbs();
    });
  }

  setBreadcrumbs() {
    this.crumbs.setBreadcrumbs([
      { name: this.branch.projectName, path: '/projects/' + this.projId },
      { name: this.branch.name, path: '/projects/' + this.projId + '/branches/' + this.branch.name },
      { name: 'Deployment History', path: '/projects/' + this.projId + '/branches/' + this.branch.name + '/deploymenthistory' }
    ]);
  }
}
