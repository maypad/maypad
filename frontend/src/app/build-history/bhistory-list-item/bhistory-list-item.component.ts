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
  }

}
