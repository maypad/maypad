import { Component, OnInit, Input } from '@angular/core';
import { Branch } from 'src/app/model/branch';

@Component({
  selector: 'app-branch-list-item',
  templateUrl: './branch-list-item.component.html',
  styleUrls: ['./branch-list-item.component.css']
})
export class BranchListItemComponent implements OnInit {
  @Input() branch: Branch;
  constructor() { }

  ngOnInit() {
  }

}
