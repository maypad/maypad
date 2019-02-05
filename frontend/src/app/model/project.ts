import { ServiceAccount } from './serviceAccount';
import { BuildStatus } from './buildStatus';
import { Branch } from './branch';
import { Tag } from './tag';

export class Project {
    name: string;
    id: number;
    repositoryUrl: string;
    branches: Branch[];
    serviceAccount: ServiceAccount;
    status: BuildStatus;
    groupId: number;
    description: string;
    refreshUrl: string;
    tags: Tag[];
}
