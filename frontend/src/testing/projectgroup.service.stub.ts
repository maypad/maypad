import { Observable, of } from 'rxjs';
import { Projectgroup } from 'src/app/model/projectGroup';
import { Project } from 'src/app/model/project';
import { ServiceAccount } from 'src/app/model/serviceAccount';
import * as get_projectgroups_response from '../../sample-requests/get.projectgroups.response.json';
import * as get_projectgroups_id_projects_response from '../../sample-requests/get.projectgroups.id.projects.response.json';

export class ProjectgroupServiceStub {
    loadProjectgroups(): Observable<Projectgroup[]> {
        return of(get_projectgroups_response['default']);
    }

    loadProjects(id: number): Observable<Project[]> {
        return of(get_projectgroups_id_projects_response['default']);
    }

    createProjectgroup(name: string): Observable<Projectgroup> {
        const group = new Projectgroup();
        group.name = name;
        return of(group);
    }

    updateProjectgroup(id: number, newName: string): Observable<Projectgroup> {
        const group = new Projectgroup();
        group.id = id;
        group.name = newName;
        return of(group);
    }

    deleteProject(id: number): Observable<{}> {
        return of(null);
    }

    deleteProjectgroup(id: number): Observable<{}> {
        return of(null);
    }

    createProject(id: number, repoUrl: string, serviceAccount: ServiceAccount, repoType: string): Observable<Project> {
        const proj = new Project();
        return of(proj);
    }
}
