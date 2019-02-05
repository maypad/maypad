import { Component, OnInit, ViewChild, ElementRef, Input, ChangeDetectorRef } from '@angular/core';
import { Projectgroup } from 'src/app/model/projectGroup';
import { ProjectgroupService } from 'src/app/projectgroup.service';
import { ServiceAccount } from 'src/app/model/serviceAccount';
import { NotificationService } from 'src/app/notification.service';

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

  /* Values:
    "git", "svn"
  */
  repoSelectedIndex = 'git';

  /* Values:
  1 = No authentification method
  2 = Authentification via SSH-Key
  3 = Authentification via Serviceaccount
  */
  authSelectedIndex = 1;

  constructor(
    private groupService: ProjectgroupService,
    private ref: ChangeDetectorRef,
    private notification: NotificationService) { }

  ngOnInit() { }

  clearInput() {
    this.repoUrl = '';
    this.sshKey = '';
    this.username = '';
    this.password = '';
    this.ref.detectChanges();
  }

  setAuthSelected(num) {
    this.authSelectedIndex = num;
  }

  setRepoSelected(val) {
    this.repoSelectedIndex = val;
  }

  addProject() {
    let serviceAccount: ServiceAccount;
    switch (this.authSelectedIndex) {
      case 1:
        serviceAccount = null;
        break;
      case 2:
        serviceAccount = { sshKey: this.sshKey };
        break;
      case 3:
        serviceAccount = {
          username: this.username,
          password: this.password
        };
        break;
      default:
        console.error(`[${this.projGroup.id}]Invalid authentification method.`);
        this.clearInput();
        return;
    }
    this.groupService.createProject(this.projGroup.id, this.repoUrl, serviceAccount, this.repoSelectedIndex).subscribe(
      proj => { this.projGroup.projects.push(proj); }
    );
    this.clearInput();
    $(`#collapse${this.projGroup.id}`).collapse('show');
    this.notification.send('The project is now being processed.', 'info');
  }
}
