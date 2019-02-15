import { Component, OnInit, Input } from '@angular/core';
import { Branch } from 'src/app/model/branch';
import { BuildStatus } from 'src/app/model/buildStatus';

@Component({
  selector: 'app-branch-list-item',
  templateUrl: './branch-list-item.component.html',
  styleUrls: ['./branch-list-item.component.css']
})
export class BranchListItemComponent implements OnInit {
  @Input() branch: Branch;
  fmtTimestamp: string;
  BuildStatus = BuildStatus;
  constructor() { }

  ngOnInit() {
    const mom = moment(this.branch.lastCommit.timestamp, moment.ISO_8601);
    this.fmtTimestamp = mom.fromNow();
  }

}
