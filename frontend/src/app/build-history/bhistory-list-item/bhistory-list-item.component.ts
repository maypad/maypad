import { Component, OnInit, Input } from '@angular/core';
import { Build } from 'src/app/model/build';

@Component({
  selector: 'app-bhistory-list-item',
  templateUrl: './bhistory-list-item.component.html',
  styleUrls: ['./bhistory-list-item.component.css']
})
export class BhistoryListItemComponent implements OnInit {
  @Input() build: Build;
  constructor() { }

  ngOnInit() { }

}
