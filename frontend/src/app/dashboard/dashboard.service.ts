import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { Projectgroup } from '../model/projectGroup';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  public subjectNewGroup: Subject<Projectgroup>;
  public subjectDeleteGroup: Subject<Projectgroup>;
  constructor() {
    this.subjectNewGroup = new Subject<Projectgroup>();
    this.subjectDeleteGroup = new Subject<Projectgroup>();
  }

  addProjGroup(newVal: Projectgroup) {
    this.subjectNewGroup.next(newVal);
  }

  deleteProjGroup(newVal: Projectgroup) {
    this.subjectDeleteGroup.next(newVal);
  }
}
