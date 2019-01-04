import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';

@Component({
  selector: 'app-add-project-dialog',
  templateUrl: './add-project-dialog.component.html',
  styleUrls: ['./add-project-dialog.component.css']
})
export class AddProjectDialogComponent implements OnInit {
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
