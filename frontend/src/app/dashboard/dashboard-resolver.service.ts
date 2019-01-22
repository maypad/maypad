import { Injectable } from '@angular/core';
import { ProjectgroupService } from '../projectgroup.service';
import { ActivatedRouteSnapshot, RouterStateSnapshot, Resolve } from '@angular/router';
import { Observable } from 'rxjs';
import { Projectgroup } from '../model/projectGroup';

@Injectable({
  providedIn: 'root'
})
export class DashboardResolverService implements Resolve<Projectgroup[]> {

  constructor(private groupService: ProjectgroupService) { }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Projectgroup[]> {
    return this.groupService.loadProjectgroups();
  }
}
