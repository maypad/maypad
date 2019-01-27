import { Component, OnInit } from '@angular/core';
import { BreadcrumbService } from '../breadcrumb.service';
import { ActivatedRoute } from '@angular/router';
import { Build } from '../model/build';
import { Branch } from '../model/branch';
import { Project } from '../model/project';

@Component({
  selector: 'app-build-history',
  templateUrl: './build-history.component.html',
  styleUrls: ['./build-history.component.css']
})
export class BuildHistoryComponent implements OnInit {
  builds: Build[];
  branch: Branch;
  project: Project;
  constructor(private route: ActivatedRoute, private crumbs: BreadcrumbService) { }

  ngOnInit() {
    this.route.data.subscribe((data: { builds: Build[], branch: Branch, project: Project }) => {
      this.builds = data.builds;
      this.branch = data.branch;
      this.project = data.project;
      this.setBreadcrumbs();
    });
  }

  setBreadcrumbs() {
    this.crumbs.setBreadcrumbs([
      { name: this.project.name, path: '/projects/' + this.project.id },
      { name: this.branch.name, path: '/projects/' + this.project.id + '/branches/' + this.branch.name },
      { name: 'Build History', path: '/projects/' + this.project.id + '/branches/' + this.branch.name + '/buildhistory' }
    ]);
  }

}
