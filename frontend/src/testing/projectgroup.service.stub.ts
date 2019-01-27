import { Observable, of } from 'rxjs';
import { Projectgroup } from 'src/app/model/projectGroup';
import { Project } from 'src/app/model/project';
import { ServiceAccount } from 'src/app/model/serviceAccount';
import * as get_projectgroups_response from '../../sample-requests/get.projectgroups.response.json';
import * as get_projectgroups_id_projects_response from '../../sample-requests/get.projectgroups.id.projects.response.json';
import * as post_projectgroups_response from '../../sample-requests/post.projectgroups.response.json';

export class ProjectgroupServiceStub {
    loadProjectgroups(): Observable<Projectgroup[]> {
        return of(get_projectgroups_response['default']);
    }

    loadProjects(id: number): Observable<Project[]> {
        return of(get_projectgroups_id_projects_response['default']);
    }

    createProject(id: number, repoUrl: string, serviceAccount: ServiceAccount): Observable<Project> {
        const proj = new Project();
        proj.id = id;
        proj.serviceAccount = serviceAccount;
        return of(proj);
    }

    createProjectgroup(name: string): Observable<Projectgroup> {
        return of(post_projectgroups_response['default']);
    }

    updateProjectgroup(id: number, newName: string): Observable<Projectgroup> {
        const group = new Projectgroup();
        group.id = id;
        group.name = 'asd123';
        return of(group);
    }

    deleteProject(id: number): Observable<{}> {
        return of({});
    }

    deleteProjectgroup(id: number): Observable<{}> {
        return of({});
    }
}
