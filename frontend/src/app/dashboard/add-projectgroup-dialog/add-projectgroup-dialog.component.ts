import { Component, OnInit, Input, ViewChild, ElementRef } from '@angular/core';

@Component({
  selector: 'app-add-projectgroup-dialog',
  templateUrl: './add-projectgroup-dialog.component.html',
  styleUrls: ['./add-projectgroup-dialog.component.css']
})
export class AddProjectgroupDialogComponent implements OnInit {
  @ViewChild('groupName') input: ElementRef;
  constructor() { }

  ngOnInit() {
  }

  // POST new request, wait for response and reload site
  addProjectgroup(name: string) {
    console.log(name);
  }

  clearInput() {
    this.input.nativeElement.value = '';
  }
}
