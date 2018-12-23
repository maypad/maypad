import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BreadcrumbService } from '../breadcrumb.service';

@Component({
  selector: 'BranchDetail',
  templateUrl: './branch-detail.component.html',
  styleUrls: ['./branch-detail.component.css']
})
export class BranchDetailComponent implements OnInit {
  projId: number;
  branchName: string;
  projName = "placeholder";
  constructor(private route: ActivatedRoute, private crumbs: BreadcrumbService) { }

  ngOnInit() {
    this.projId = this.route.snapshot.params['id'];
    this.branchName = this.route.snapshot.params['branch'];
    this.crumbs.setBreadcrumbs([
      { name: this.projName, path: '/projects/' + this.projId },
      { name: this.branchName, path: '/projects/' + this.projId + '/branches/' + this.branchName }
    ]);
  }

}
