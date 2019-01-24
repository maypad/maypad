import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Projectgroup } from './model/projectGroup';
import { Project } from './model/project';
import { Observable } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { KeyServiceAccount, ServiceAccount, UserServiceAccount } from './model/serviceAccount';
import { BuildStatus } from './model/buildStatus';


@Injectable({
  providedIn: 'root'
})
export class ProjectgroupService {

  constructor(
    private api: ApiService,
  ) { }

  loadProjectgroups(): Observable<Projectgroup[]> {
    const url = `${this.api.backendUrl}projectgroups`;
    return this.api.http.get<Projectgroup[]>(url)
      .pipe(
        map(response => {
          return response.map(group => {
            group.status = (<any>BuildStatus)[group['buildStatus']];
            return group;
          });
        }),
        catchError(this.api.handleError<Projectgroup[]>('loadProjectgroups', []))
      );
  }

  loadProjects(id: number): Observable<Project[]> {
    const url = `${this.api.backendUrl}projectgroups/${id}/projects`;
    return this.api.http.get<Project[]>(url)
      .pipe(
        map(response => {
          return response.map(project => {
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
          });
        }),
        catchError(this.api.handleError<Project[]>('loadProjects', []))
      );
  }

  createProject(id: number, repoUrl: string, serviceAccount: ServiceAccount): Observable<Project> {
    const url = `${this.api.backendUrl}projects`;
    const project = new Project();
    project.groupId = id;
    project.repositoryUrl = repoUrl;
    project.serviceAccount = serviceAccount;

    return this.api.http.post<Project>(url, project, this.api.httpOptions)
      .pipe(
        map(response => {
          response.status = (<any>BuildStatus)[response['buildStatus']];
          if (response['serviceAccount'] != null) {
            if (response['serviceAccount']['key'] !== undefined) {
              const account = new KeyServiceAccount();
              account.sshKey = response['serviceAccount']['key'];
              response.serviceAccount = account;
            } else {
              const account = new UserServiceAccount();
              account.username = response.serviceAccount['username'];
              response.serviceAccount = account;
            }
          }
          return response;
        }),
        catchError(this.api.handleError<Project>('createProject'))
      );
  }

  createProjectgroup(name: string): Observable<Projectgroup> {
    const url = `${this.api.backendUrl}projectgroups`;
    const projectgroup = new Projectgroup();
    projectgroup.name = name;

    return this.api.http.post<Projectgroup>(url, projectgroup, this.api.httpOptions)
      .pipe(
        map(group => {
          group.status = (<any>BuildStatus)[group['buildStatus']];
          return group;
        }),
        catchError(this.api.handleError<Projectgroup>('createProjectgroup'))
      );
  }

  updateProjectgroup(id: number, newName: string): Observable<Projectgroup> {
    const url = `${this.api.backendUrl}projectgroups/${id}`;
    const projectgroup = new Projectgroup();
    projectgroup.name = newName;

    return this.api.http.put<Projectgroup>(url, projectgroup, this.api.httpOptions)
      .pipe(
        map(group => {
          group.status = (<any>BuildStatus)[group['buildStatus']];
          return group;
        }),
        catchError(this.api.handleError<Projectgroup>('updateProjectgroup'))
      );
  }

  deleteProject(id: number): Observable<{}> {
    const url = `${this.api.backendUrl}projects/${id}`;
    return this.api.http.delete(url, this.api.httpOptions)
      .pipe(catchError(this.api.handleError<Project>('deleteProject')));
  }

  deleteProjectgroup(id: number): Observable<{}> {
    const url = `${this.api.backendUrl}projectgroups/${id}`;
    return this.api.http.delete(url, this.api.httpOptions)
      .pipe(catchError(this.api.handleError<Projectgroup>('deleteProjectgroup')));
  }
}
