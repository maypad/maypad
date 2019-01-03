import { Commit } from './commit';
import { BuildStatus } from './buildStatus';

export class Build {
    projectName: string;
    branchName: string;
    timestamp: string;
    commit: Commit;
    status: BuildStatus;
}
