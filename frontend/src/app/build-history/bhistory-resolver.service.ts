import { Injectable } from '@angular/core';
import { Resolve, RouterStateSnapshot, ActivatedRouteSnapshot } from '@angular/router';
import { Build } from '../model/build';
import { BranchService } from '../branch.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BhistoryResolverService implements Resolve<Build[]> {

  constructor(private branchService: BranchService) { }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Build[]> {
    const id = route.paramMap.get('id');
    const branch = route.paramMap.get('branch');
    return this.branchService.loadBuildHistory(parseInt(id, 10), branch);
  }
}
