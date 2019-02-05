import { Component, OnInit, Input } from '@angular/core';
import { Branch } from 'src/app/model/branch';

@Component({
  selector: 'app-branch-list-item',
  templateUrl: './branch-list-item.component.html',
  styleUrls: ['./branch-list-item.component.css']
})
export class BranchListItemComponent implements OnInit {
  @Input() branch: Branch;
  fmtTimestamp: string;
  constructor() { }

  ngOnInit() {
    $(function () {
      $('[data-toggle="tooltip"]').tooltip({
        title: function (this: Element) {
          if (this.className.includes('dot-0')) {
            return 'Successful';
          } else if (this.className.includes('dot-1')) {
            return 'Failing';
          } else if (this.className.includes('dot-2')) {
            return 'Running';
          } else if (this.className.includes('dot-3')) {
            return 'Unknown';
          }
        }
      });
    });
    const mom = moment(this.branch.lastCommit.timestamp, moment.ISO_8601);
    this.fmtTimestamp = mom.fromNow();
  }

}
