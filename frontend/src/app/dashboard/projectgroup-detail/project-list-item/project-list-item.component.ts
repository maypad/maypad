import { Component, OnInit, Input } from '@angular/core';
import { Project } from 'src/app/model/project';

@Component({
  selector: 'app-project-list-item',
  templateUrl: './project-list-item.component.html',
  styleUrls: ['./project-list-item.component.css']
})
export class ProjectListItemComponent implements OnInit {
  @Input() project: Project;
  @Input() border: boolean;
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
