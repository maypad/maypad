import { Observable, of } from 'rxjs';
import * as branches from 'sample-requests/get.projects.id.branches.response.json';
import * as project from 'sample-requests/get.projects.id.response.json';
import { Branch } from 'src/app/model/branch';
import { Project } from 'src/app/model/project';


export class ProjectServiceStub {
    loadBranches(id: number): Observable<Branch[]> {
        return of(branches['default']);
    }

    loadProject(id: number): Observable<Project> {
        return of(project['default']);
    }
}
