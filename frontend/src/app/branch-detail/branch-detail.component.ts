import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BreadcrumbService } from '../breadcrumb.service';
import { Branch } from '../model/branch';

@Component({
  selector: 'app-branch-detail',
  templateUrl: './branch-detail.component.html',
  styleUrls: ['./branch-detail.component.css']
})
export class BranchDetailComponent implements OnInit {
  branch: Branch;
  projId: number;
  constructor(private route: ActivatedRoute, private crumbs: BreadcrumbService) { }

  ngOnInit() {
    this.projId = parseInt(this.route.snapshot.paramMap.get('id'), 10);
    this.route.data.subscribe((data: { branch: Branch }) => {
      this.branch = data.branch;
    });
    this.crumbs.setBreadcrumbs([
      { name: this.branch.projectName, path: '/projects/' + this.projId },
      { name: this.branch.name, path: '/projects/' + this.projId + '/branches/' + this.branch.name }
    ]);
  }
}
