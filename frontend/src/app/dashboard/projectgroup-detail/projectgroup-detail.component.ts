import { Component, OnInit, Input } from '@angular/core';
import { Projectgroup } from 'src/app/model/projectGroup';

@Component({
  selector: 'app-projectgroup-detail',
  templateUrl: './projectgroup-detail.component.html',
  styleUrls: ['./projectgroup-detail.component.css']
})
export class ProjectgroupDetailComponent implements OnInit {
  @Input('projGroup') projGroup: Projectgroup;
  constructor() { }

  ngOnInit() {
  }

}
