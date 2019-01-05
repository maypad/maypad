import { Component, OnInit, Input, ViewChild, ElementRef } from '@angular/core';
import { Projectgroup } from 'src/app/model/projectGroup';

@Component({
  selector: 'app-edit-projectgroup-dialog',
  templateUrl: './edit-projectgroup-dialog.component.html',
  styleUrls: ['./edit-projectgroup-dialog.component.css']
})
export class EditProjectgroupDialogComponent implements OnInit {
  @Input('projGroup') projGroup: Projectgroup;
  @ViewChild('newNameInput') newNameInput: ElementRef;
  constructor() { }

  ngOnInit() {
  }

  clearInput() {
    this.newNameInput.nativeElement.value = '';
  }

  updateProjectgroup(newName: string) {
    console.log(`Update projgroup ${this.projGroup.id} to new name: ${newName}`);
    // Reload page?
  }

  deleteProjectgroup() {
    console.log(`Delete projgroup ${this.projGroup.id}.`);
    // Reload page?
  }

}
