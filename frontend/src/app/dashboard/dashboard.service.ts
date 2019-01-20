import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { Projectgroup } from '../model/projectGroup';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  public projGroups: Subject<Projectgroup>;
  constructor() {
    this.projGroups = new Subject<Projectgroup>();
  }

  addProjGroup(newVal: Projectgroup) {
    this.projGroups.next(newVal);
  }
}
