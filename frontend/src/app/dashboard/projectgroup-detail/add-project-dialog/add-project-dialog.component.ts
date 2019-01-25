import { Component, OnInit, ViewChild, ElementRef, Input, ChangeDetectorRef } from '@angular/core';
import { Projectgroup } from 'src/app/model/projectGroup';
import { ProjectgroupService } from 'src/app/projectgroup.service';
import { ServiceAccount } from 'src/app/model/serviceAccount';

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
  1 = No authentification method
  2 = Authentification via SSH-Key
  3 = Authentification via Serviceaccount
  */
  selectedIndex = 1;

  constructor(
    private groupService: ProjectgroupService,
    private ref: ChangeDetectorRef) { }

  ngOnInit() { }

  clearInput() {
    this.repoUrl = '';
    this.sshKey = '';
    this.username = '';
    this.password = '';
    this.ref.detectChanges();
  }

  setSelected(num) {
    this.selectedIndex = num;
  }

  addProject() {
    let serviceAccount: ServiceAccount;
    switch (this.selectedIndex) {
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
    this.groupService.createProject(this.projGroup.id, this.repoUrl, serviceAccount).subscribe(
      proj => { this.projGroup.projects.push(proj); }
    );
    this.clearInput();
    $(`#collapse${this.projGroup.id}`).collapse('show');

    const hulla = new hullabaloo();
    hulla.options.align = 'center';
    hulla.options.width = 400;
    hulla.options.offset = { from: 'top', amount: 30 };
    hulla.send('The project is now being processed.', 'info');
  }
}
