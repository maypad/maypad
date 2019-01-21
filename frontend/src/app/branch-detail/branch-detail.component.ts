import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BreadcrumbService } from '../breadcrumb.service';
import { Branch } from '../model/branch';
import { Project } from '../model/project';

@Component({
  selector: 'app-branch-detail',
  templateUrl: './branch-detail.component.html',
  styleUrls: ['./branch-detail.component.css']
})
export class BranchDetailComponent implements OnInit {
  branch: Branch;
  project: Project;
  constructor(private route: ActivatedRoute, private crumbs: BreadcrumbService) { }

  ngOnInit() {
    this.route.data.subscribe((data: { branch: Branch, project: Project }) => {
      this.branch = data.branch;
      this.project = data.project;
      this.crumbs.setBreadcrumbs([
        { name: this.project.name, path: '/projects/' + this.project.id },
        { name: this.branch.name, path: '/projects/' + this.project.id + '/branches/' + this.branch.name }
      ]);
    });
  }
}
