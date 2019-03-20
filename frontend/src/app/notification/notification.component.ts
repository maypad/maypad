import { Component, OnInit, ViewChild } from '@angular/core';
import { NotificationService } from '../notification.service';
import { NotifierService } from 'angular-notifier';

@Component({
  selector: 'app-notification',
  templateUrl: './notification.component.html',
  styleUrls: ['./notification.component.css']
})
export class NotificationComponent implements OnInit {
  @ViewChild('notificationContent') notificationContent;
  message: string;
  branch: string;
  projId: string;
  projgroupId: string;
  constructor(private notificationService: NotificationService,
    private notifier: NotifierService) { }

  ngOnInit() {
    this.notificationService.message.subscribe(message => {
      this.message = message.content;
      this.branch = message.branch ? message.branch : null;
      this.projId = message.projId ? message.projId : null;
      this.projgroupId = message.projgroupId ? message.projgroupId : null;
      this.show(message.content, message.type);
    });
  }

  show(message: string, type: string) {
    this.notifier.show({
      message: message,
      type: type,
      template: this.notificationContent
    });
  }
}
