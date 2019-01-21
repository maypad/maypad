import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Project } from '../model/project';
import { ProjectService } from '../project.service';
import { Observable, EMPTY } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ProjectDetailResolverService implements Resolve<Project> {

  constructor(private projService: ProjectService, private router: Router) { }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Project> {
    const id = route.paramMap.get('id');
    return this.projService.loadProject(parseInt(id, 10)).pipe(
      map(proj => {
        // Maybe inform user that the project may not exista
        if (proj) {
          return proj;
        } else {
          this.router.navigateByUrl('/404');
          return null;
        }
      })
    );
  }
}
