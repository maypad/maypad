import { Component, OnInit, Input, ViewChild, ElementRef } from '@angular/core';
import { ProjectgroupService } from 'src/app/projectgroup.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-add-projectgroup-dialog',
  templateUrl: './add-projectgroup-dialog.component.html',
  styleUrls: ['./add-projectgroup-dialog.component.css']
})
export class AddProjectgroupDialogComponent implements OnInit {
  groupName = '';
  constructor(private groupService: ProjectgroupService, private router: Router) { }

  ngOnInit() {
  }

  addProjectgroup() {
    this.groupService.createProjectgroup(this.groupName).subscribe(
      () => { this.router.navigateByUrl('/'); }
    );
  }

  clearInput() {
    this.groupName = '';
  }
}
