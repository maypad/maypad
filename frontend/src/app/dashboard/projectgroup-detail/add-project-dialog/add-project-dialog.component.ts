import { Component, OnInit, ViewChild, ElementRef, Input } from '@angular/core';
import { Projectgroup } from 'src/app/model/projectGroup';

@Component({
  selector: 'app-add-project-dialog',
  templateUrl: './add-project-dialog.component.html',
  styleUrls: ['./add-project-dialog.component.css']
})
export class AddProjectDialogComponent implements OnInit {
  @Input('projGroup') projGroup: Projectgroup;
  @ViewChild('repoUrl') repoField: ElementRef;
  @ViewChild('sshKey') sshField: ElementRef;
  @ViewChild('username') username: ElementRef;
  @ViewChild('password') password: ElementRef;
  selectedIndex: Number = 1;

  constructor() { }

  ngOnInit() {
  }

  clearInput() {
    this.repoField.nativeElement.value = '';
    this.sshField.nativeElement.value = '';
    this.username.nativeElement.value = '';
    this.password.nativeElement.value = '';
  }

  setSelected(num: Number) {
    this.selectedIndex = num;
  }

}
