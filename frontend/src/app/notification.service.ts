import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

interface Message {
  content: string;
  type: string;
  branch?: string;
  projId?: string;
  projgroupId?: string;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  public message: Subject<Message>;

  constructor() {
    this.message = new Subject<Message>();
  }

  send(content: string, type: string, branch?: string, projId?: string, projgroupId?: string) {
    this.message.next({ content, type, branch, projId, projgroupId });
  }

  sendInfo(content: string, branch?: string, projId?: string, projgroupId?: string) {
    this.message.next({ content, type: 'info', branch, projId, projgroupId });
  }

  sendWarning(content: string, branch?: string, projId?: string, projgroupId?: string) {
    this.message.next({ content, type: 'warning', branch, projId, projgroupId });
  }

  sendSuccess(content: string, branch?: string, projId?: string, projgroupId?: string) {
    this.message.next({ content, type: 'success', branch, projId, projgroupId });
  }
  sendError(content: string) {
    this.message.next({ content, type: 'error' });
  }
}
