import { Component, OnInit } from '@angular/core';
import { BreadcrumbService } from '../breadcrumb.service';
import { ActivatedRoute } from '@angular/router';
import { Build } from '../model/build';
import { Branch } from '../model/branch';

@Component({
  selector: 'app-build-history',
  templateUrl: './build-history.component.html',
  styleUrls: ['./build-history.component.css']
})
export class BuildHistoryComponent implements OnInit {
  builds: Build[];
  branch: Branch;
  projId: number;
  constructor(private route: ActivatedRoute, private crumbs: BreadcrumbService) { }

  ngOnInit() {
    this.projId = parseInt(this.route.snapshot.paramMap.get('id'), 10);
    this.route.data.subscribe((data: { builds: Build[], branch: Branch }) => {
      this.builds = data.builds;
      this.branch = data.branch;
      this.setBreadcrumbs();
    });
  }

  setBreadcrumbs() {
    this.crumbs.setBreadcrumbs([
      { name: this.branch.projectName, path: '/projects/' + this.projId },
      { name: this.branch.name, path: '/projects/' + this.projId + '/branches/' + this.branch.name },
      { name: 'Build History', path: '/projects/' + this.projId + '/branches/' + this.branch.name + '/buildhistory' }
    ]);
  }

}
