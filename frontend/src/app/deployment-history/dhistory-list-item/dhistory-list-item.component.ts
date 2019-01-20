import { Component, OnInit, Input } from '@angular/core';
import { Deployment } from 'src/app/model/deployment';

@Component({
  selector: 'app-dhistory-list-item',
  templateUrl: './dhistory-list-item.component.html',
  styleUrls: ['./dhistory-list-item.component.css']
})
export class DhistoryListItemComponent implements OnInit {
  @Input() deployment: Deployment;
  constructor() { }

  ngOnInit() {
  }

}
