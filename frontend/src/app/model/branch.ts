import { BuildStatus } from './buildStatus';
import { Commit } from './commit';
import { Build } from './build';
import { Deployment } from './deployment';

export class Branch {
    deploymentType: string;
    respPerson: string;
    dependencies: string[];
    readmeURL: string;
    branchURL: string;
    buildSuccessURL: string;
    buildFailURL: string;
    tags: string[];
    status: BuildStatus;
    lastCommit: Commit;
}
