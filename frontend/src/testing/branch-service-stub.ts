import * as branchesResponse from 'sample-requests/get.projects.id.branches.ref.response.json';
import * as deploymentsResponse from 'sample-requests/get.projects.id.branches.ref.deployments.response.json';
import * as buildsResponse from 'sample-requests/get.projects.id.branches.ref.builds.response.json';
import { of, Observable, throwError } from 'rxjs';
import { Branch } from 'src/app/model/branch';
import { Deployment } from 'src/app/model/deployment';
import { Build } from 'src/app/model/build';

export class BranchServiceStub {
    loadBranch(): Observable<Branch> {
        return of(branchesResponse['default']);
    }

    loadBuildHistory(): Observable<Build[]> {
        return of(buildsResponse['default']);
    }

    loadDeploymentHistory(): Observable<Deployment[]> {
        return of(deploymentsResponse['default']);
    }

    triggerDeployment(id, branchName, withBuild, rebuild): Observable<{}> {
        return of({});
    }

    triggerBuild(id, branchName, rebuild): Observable<{}> {
        return of({});
    }
}
