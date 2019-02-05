import { Component, OnInit, Input } from '@angular/core';
import { Project } from 'src/app/model/project';
import { UserServiceAccount, KeyServiceAccount } from 'src/app/model/serviceAccount';
import { ProjectService } from 'src/app/project.service';
import { ProjectgroupService } from 'src/app/projectgroup.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-edit-project-dialog',
  templateUrl: './edit-project-dialog.component.html',
  styleUrls: ['./edit-project-dialog.component.css']
})
export class EditProjectDialogComponent implements OnInit {
  @Input() project: Project;
  sshKey: string;
  username: string;
  password: string;
  // 1 = None; 2 = User; 3 = SSH-Key
  serviceType = 1;
  constructor(
    private projService: ProjectService,
    private groupService: ProjectgroupService,
    private router: Router) { }

  ngOnInit() {
    this.initServiceMethod();
  }

  initServiceMethod() {
    if (this.project.serviceAccount != null) {
      if (this.project.serviceAccount instanceof UserServiceAccount) {
        this.serviceType = 3;
        this.username = this.project.serviceAccount.username;
        this.password = '';
      } else if (this.project.serviceAccount instanceof KeyServiceAccount) {
        this.serviceType = 2;
        this.sshKey = this.project.serviceAccount.sshKey;
      }
    } else {
      this.serviceType = 1;
    }
  }

  setSelected(num) {
    this.serviceType = num;
  }

  updateProject() {
    let newAccount = null;
    switch (this.serviceType) {
      case 1:
        break;
      case 2:
        newAccount = { sshKey: this.sshKey };
        break;
      case 3:
        newAccount = { username: this.username, password: this.password };
        break;
      default:
        this.initServiceMethod();
        return;
    }
    this.projService.updateServiceAccount(this.project.id, newAccount).subscribe(project => {
      this.project = project;
      this.initServiceMethod();
    });
  }

  deleteProject() {
    this.groupService.deleteProject(this.project.id).subscribe((res) => {
      if (res === null) {
        this.router.navigateByUrl('/');
      }
    });
  }

}
