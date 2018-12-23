import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

export interface IBreadcrumbs {
  name: string;
  path: string;
}

@Injectable({
  providedIn: 'root'
})
export class BreadcrumbService {
  public breadcrumbs: Subject<IBreadcrumbs[]>;
  constructor() {
    this.breadcrumbs = new Subject<IBreadcrumbs[]>();
  }

  setBreadcrumbs(newVal: IBreadcrumbs[]) {
    this.breadcrumbs.next(newVal);
  }
}
