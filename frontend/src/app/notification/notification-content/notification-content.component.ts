import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-notification-content',
  templateUrl: './notification-content.component.html',
  styleUrls: ['./notification-content.component.css']
})
export class NotificationContentComponent implements OnInit {
  @Input() inputMessage: string;
  @Input() inputBranch: string;
  @Input() inputProjId: string;
  @Input() inputProjgroupId: string;
  copiedMessage: string;
  copiedBranch: string;
  copiedProjId: string;
  copiedProjgroupId: string;

  constructor() { }

  ngOnInit() {
    this.copiedMessage = this.inputMessage;
    this.copiedBranch = this.inputBranch;
    this.copiedProjId = this.inputProjId;
    this.copiedProjgroupId = this.inputProjgroupId;
  }

}
