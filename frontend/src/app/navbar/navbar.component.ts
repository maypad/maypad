import { Component, OnInit } from '@angular/core';
import { BreadcrumbService, IBreadcrumbs } from '../breadcrumb.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  breadcrumbs: IBreadcrumbs[];
  constructor(private crumbs: BreadcrumbService) { }

  ngOnInit() {
    this.crumbs.breadcrumbs.subscribe(data => {
      this.breadcrumbs = data;
    },
      (err) => { console.log(err) }
    )
  }
}
