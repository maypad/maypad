import { Component, OnInit, ViewChild, ElementRef, Input } from '@angular/core';
import { Projectgroup } from 'src/app/model/projectGroup';

@Component({
  selector: 'app-add-project-dialog',
  templateUrl: './add-project-dialog.component.html',
  styleUrls: ['./add-project-dialog.component.css']
})
export class AddProjectDialogComponent implements OnInit {
  @Input() projGroup: Projectgroup;
  repoUrl = '';
  sshKey = '';
  username = '';
  password = '';
  selectedIndex = 1;

  constructor() { }

  ngOnInit() {
  }

  clearInput() {
    this.repoUrl = '';
    this.sshKey = '';
    this.username = '';
    this.password = '';
  }

  setSelected(num) {
    this.selectedIndex = num;
  }

  addProject() {
    switch (this.selectedIndex) {
      case 1:
        console.log(`[${this.projGroup.id}]Add project url: ${this.repoUrl} with sshKey:`, this.sshKey);
        break;
      case 2:
        console.log(`[${this.projGroup.id}]Add project url: ${this.repoUrl} with username/password:`, this.username, this.password);
        break;
      default:
        console.error(`[${this.projGroup.id}]Invalid authentification method.`);
        break;
    }
    this.clearInput();
  }

}
