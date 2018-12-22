import { BuildStatus } from './buildStatus';
import { Commit } from './commit';

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
