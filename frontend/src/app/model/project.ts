import { ServiceAccount } from './serviceAccount';
import { BuildStatus } from './buildStatus';
import { Branch } from './branch';

export class Project {
    name: string;
    id: number;
    repositoryURL: string;
    branches: Branch[];
    serviceAccount: ServiceAccount;
    status: BuildStatus;
    groupId: number;
}
