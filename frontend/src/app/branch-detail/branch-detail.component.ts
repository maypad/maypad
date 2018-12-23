import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'BranchDetail',
  templateUrl: './branch-detail.component.html',
  styleUrls: ['./branch-detail.component.css']
})
export class BranchDetailComponent implements OnInit {
  projId: number;
  branchName: string;
  constructor(private route: ActivatedRoute) { }

  ngOnInit() {
    this.projId = this.route.snapshot.params['id'];
    this.branchName = this.route.snapshot.params['branch'];
  }

}
