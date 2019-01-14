import { Injectable } from '@angular/core';
import { Branch } from '../model/branch';
import { Resolve, RouterStateSnapshot, ActivatedRouteSnapshot } from '@angular/router';
import { ProjectService } from '../project.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProjectBranchResolverService implements Resolve<Branch[]> {

  constructor(private projService: ProjectService) { }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Branch[]> | Observable<never> {
    const id = route.paramMap.get('id');
    return this.projService.loadBranches(parseInt(id, 10));
  }
}
