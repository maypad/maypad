import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'ProjectDetail',
  templateUrl: './project-detail.component.html',
  styleUrls: ['./project-detail.component.css']
})
export class ProjectDetailComponent implements OnInit {
  projId: number;

  constructor(private route: ActivatedRoute) { }

  ngOnInit() {
    this.projId = this.route.snapshot.params['id'];
  }

}
