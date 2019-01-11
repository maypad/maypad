import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Observable } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { Build } from './model/build';
import { Deployment } from './model/deployment';
import { Branch } from './model/branch';
import {BuildStatus} from './model/buildStatus';
import {Commit} from './model/commit';


@Injectable({
  providedIn: 'root'
})
export class BranchService {

  constructor(
    private api: ApiService,
  ) {}

  loadBuildHistory(projId: number, branch: string): Observable<Build[]> {
    const url = `${this.api.backendUrl}projects/${projId}/branches/${branch}/builds`;
    return this.api.http.get<Build[]>(url)
      .pipe(
        map(response => {
          return response.map(build => {
            build.status = (<any>BuildStatus)[build['status']];
            build.commit = <Commit>build['commit'];
            return build;
          });
        }),
        catchError(this.api.handleError<Build[]>('loadBuildHistory', []))
    );
  }

  loadDeploymentHistory(projId: number, branch: string): Observable<Deployment[]> {
    const url = `${this.api.backendUrl}projects/${projId}/branches/${branch}/deployments`;
    return this.api.http.get<Deployment[]>(url).pipe(
      catchError(this.api.handleError<Deployment[]>('loadDeploymentHistory', []))
    );
  }

  loadBranch(projId: number, branch: string): Observable<Branch> {
    const url = `${this.api.backendUrl}projects/${projId}/branches/${branch}`;
    return this.api.http.get<Branch>(url)
      .pipe(
        map(response => {
          response.status = (<any>BuildStatus)[response['buildStatus']];
          response.lastCommit = <Commit>response['lastCommit'];
          return response;
        }),
        catchError(this.api.handleError<Branch>('loadBranch'))
    );
  }

  refreshBranch(projId: number, branch: string): void {
    const url = `${this.api.backendUrl}projects/${projId}/refresh`;
    new Promise((resolve, reject) => {
      this.api.http.post(url, '', this.api.httpOptions)
        .pipe(catchError(this.api.handleError('refreshBranch')))
        .toPromise().then(() => { resolve(); });
    }).valueOf();
  }

  triggerBuild(projId: number, branch: string, withDependencies = false): void {
    const url = `${this.api.backendUrl}projects/${projId}/branches/${branch}/builds`;
    const body = JSON.stringify({ 'withDependencies' : withDependencies});
    new Promise((resolve, reject) => {
      this.api.http.post(url, body, this.api.httpOptions)
        .pipe(catchError(this.api.handleError('triggerBuild')))
        .toPromise().then(() => { resolve(); });
    }).valueOf();
  }

  triggerDeployment(projId: number, branch: string, withBuild = false, withDependencies = false): void {
    const url = `${this.api.backendUrl}projects/${projId}/branches/${branch}/deployments`;
    const body = JSON.stringify({ 'withBuild' : withBuild, 'withDependencies' : withDependencies});
    new Promise((resolve, reject) => {
      this.api.http.post(url, body, this.api.httpOptions)
        .pipe(catchError(this.api.handleError('triggerDeployment')))
        .toPromise().then(() => { resolve(); });
    }).valueOf();
  }
}

