import { Commit } from './commit';
import { BuildStatus } from './buildStatus';

export enum BuildReason {
    DEPENDENCY_BUILD_FAILED = 'A dependency failed',
    BUILD_FAILED = 'The build failed',
    BUILD_NOT_STARTED = 'The build could not be started'
}

export class Build {
    timestamp: string;
    commit: Commit;
    status: BuildStatus;
    reason: BuildReason;
    reasonDependency: string;
}
