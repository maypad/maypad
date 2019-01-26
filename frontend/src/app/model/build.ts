import { Commit } from './commit';
import { BuildStatus } from './buildStatus';

export class Build {
    timestamp: string;
    commit: Commit;
    status: BuildStatus;
}
