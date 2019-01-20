import { Injectable } from '@angular/core';
import { Branch } from '../model/branch';
import { RouterStateSnapshot, ActivatedRouteSnapshot, Resolve } from '@angular/router';
import { BranchService } from '../branch.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BranchDetailResolverService implements Resolve<Branch> {

  constructor(private branchService: BranchService) { }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Branch> {
    const id = route.paramMap.get('id');
    const branch = route.paramMap.get('branch');
    return this.branchService.loadBranch(parseInt(id, 10), branch);
  }
}
