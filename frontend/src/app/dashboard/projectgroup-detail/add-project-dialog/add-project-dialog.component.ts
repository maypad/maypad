import { Component, OnInit, ViewChild, ElementRef, Input, ChangeDetectorRef } from '@angular/core';
import { Projectgroup } from 'src/app/model/projectGroup';
import { ProjectgroupService } from 'src/app/projectgroup.service';
import { ServiceAccount } from 'src/app/model/serviceAccount';
import { NotificationService } from 'src/app/notification.service';

enum RepoTypes {
  Git = 'Git',
  SVN = 'SVN'
}

export enum AuthMethods {
  None = 'None',
  SSH = 'SSH-Key',
  ServiceAccount = 'Service Account'
}

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

  // Can't declare actual enum in class
  RepoTypes = RepoTypes;
  AuthMethods = AuthMethods;

  authMethod = AuthMethods.None;
  repoType = RepoTypes.Git;

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

  setAuthSelected(val) {
    this.authMethod = val;
  }

  setRepoSelected(val: string) {
    this.repoType = RepoTypes[val];
  }

  addProject() {
    let serviceAccount: ServiceAccount;
    switch (this.authMethod) {
      case AuthMethods.None:
        serviceAccount = null;
        break;
      case AuthMethods.SSH:
        serviceAccount = { sshKey: this.sshKey };
        break;
      case AuthMethods.ServiceAccount:
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
    this.groupService.createProject(this.projGroup.id, this.repoUrl, serviceAccount, this.repoType).subscribe(
      proj => { this.projGroup.projects.push(proj); }
    );
    this.clearInput();
    $(`#collapse${this.projGroup.id}`).collapse('show');
    this.notification.send('The project is now being processed.', 'info');
  }
}
