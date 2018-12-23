import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BreadcrumbService } from '../breadcrumb.service';

@Component({
  selector: 'ProjectDetail',
  templateUrl: './project-detail.component.html',
  styleUrls: ['./project-detail.component.css']
})
export class ProjectDetailComponent implements OnInit {
  projId: number;
  projName = "placeholder";

  constructor(private route: ActivatedRoute, private crumbs: BreadcrumbService) { }

  ngOnInit() {
    this.projId = this.route.snapshot.params['id'];
    this.crumbs.setBreadcrumbs([{ name: this.projName, path: 'projects/' + this.projId }]);
  }

}
