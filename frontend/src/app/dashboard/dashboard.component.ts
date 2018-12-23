import { Component, OnInit } from '@angular/core';
import { BreadcrumbService } from '../breadcrumb.service';

@Component({
  selector: 'Dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  constructor(private crumbs: BreadcrumbService) { }

  ngOnInit() {
    this.crumbs.setBreadcrumbs([]);
  }

}
