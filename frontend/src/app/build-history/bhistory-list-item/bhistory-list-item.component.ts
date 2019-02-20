import { Component, OnInit, Input } from '@angular/core';
import { Build } from 'src/app/model/build';

@Component({
  selector: 'app-bhistory-list-item',
  templateUrl: './bhistory-list-item.component.html',
  styleUrls: ['./bhistory-list-item.component.css']
})
export class BhistoryListItemComponent implements OnInit {
  @Input() build: Build;
  fmtDependency: string;
  constructor() { }

  ngOnInit() {
    if (this.build.reasonDependency) {
      const parts = this.build.reasonDependency.split(':');
      console.log(parts);
      if (parts.length === 2) {
        this.fmtDependency = `Project ID: ${parts[0]} Branch: ${parts[1]}`;
      }
    }
  }

}
