import * as branchesResponse from 'sample-requests/get.projects.id.branches.ref.response.json';
import { of, Observable } from 'rxjs';
import { Branch } from 'src/app/model/branch';

export class BranchServiceStub {
    loadBranch(): Observable<Branch> {
        return of(branchesResponse['default']);
    }
}
