import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Deployment } from '../model/deployment';
import { Observable } from 'rxjs';
import { BranchService } from '../branch.service';

@Injectable({
  providedIn: 'root'
})
export class DhistoryResolverService {

  constructor(private branchService: BranchService) { }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Deployment[]> {
    const id = route.paramMap.get('id');
    const branch = route.paramMap.get('branch');
    return this.branchService.loadDeploymentHistory(parseInt(id, 10), branch);
  }
}
