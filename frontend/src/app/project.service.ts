import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Branch } from './model/branch';
import { Project } from './model/project';
import { Observable } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { KeyServiceAccount, ServiceAccount, UserServiceAccount } from './model/serviceAccount';
import { BuildStatus } from './model/buildStatus';
import { Commit } from './model/commit';


@Injectable({
  providedIn: 'root'
})
export class ProjectService {

  constructor(
    private api: ApiService,
  ) { }

  loadProject(id: number): Observable<Project> {
    const url = `${this.api.backendUrl}projects/${id}`;
    return this.api.http.get<Project>(url)
      .pipe(
        map(project => {
          project.status = (<any>BuildStatus)[project['buildStatus']];
          if (project['serviceAccount'] != null) {
            if (project['serviceAccount']['key'] !== undefined) {
              const serviceAccount = new KeyServiceAccount();
              serviceAccount.sshKey = project.serviceAccount['key'];
              project.serviceAccount = serviceAccount;
            } else {
              const serviceAccount = new UserServiceAccount();
              serviceAccount.username = project.serviceAccount['username'];
              project.serviceAccount = serviceAccount;
            }
          }
          return project;
        }),
        catchError(this.api.handleError<Project>('loadProject'))
      );
  }

  loadBranches(id: number): Observable<Branch[]> {
    const url = `${this.api.backendUrl}projects/${id}/branches`;
    return this.api.http.get<Branch[]>(url)
      .pipe(
        map(response => {
          return response.map(branch => {
            branch.status = (<any>BuildStatus)[branch['buildStatus']];
            branch.lastCommit = <Commit>branch['lastCommit'];
            return branch;
          });
        }),
        catchError(this.api.handleError<Branch[]>('loadBranches', []))
      );
  }

  updateServiceAccount(id: number, newAccount: ServiceAccount): Observable<Project> {
    const url = `${this.api.backendUrl}projects/${id}`;
    const project = new Project();
    project.serviceAccount = newAccount;

    return this.api.http.put<Project>(url, project, this.api.httpOptions)
      .pipe(
        map(response => {
          response.status = (<any>BuildStatus)[response['buildStatus']];
          if (response['serviceAccount'] != null) {
            if (response['serviceAccount']['key'] !== undefined) {
              const serviceAccount = new KeyServiceAccount();
              serviceAccount.sshKey = response.serviceAccount['key'];
              response.serviceAccount = serviceAccount;
            } else {
              const serviceAccount = new UserServiceAccount();
              serviceAccount.username = response.serviceAccount['username'];
              response.serviceAccount = serviceAccount;
            }
          }
          return response;
        }),
        catchError(this.api.handleError<Project>('updateServiceAccount'))
      );
  }

  refreshProject(projId: number): Observable<{}> {
    const url = `${this.api.backendUrl}projects/${projId}/refresh`;
    return this.api.http.post(url, '', this.api.httpOptions)
      .pipe(catchError(this.api.handleError('refreshBranch')));
  }
}

