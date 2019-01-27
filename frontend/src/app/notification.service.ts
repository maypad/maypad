import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private hulla;

  constructor() {
    this.hulla = new hullabaloo();
    this.hulla.options.align = 'center';
    this.hulla.options.width = 500;
    this.hulla.options.offset = { from: 'top', amount: 30 };
  }

  send(message: string, type: string) {
    this.hulla.send(message, type);
  }
}
