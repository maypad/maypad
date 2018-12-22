import { ServiceAccount } from "./serviceAccount";
import { BuildStatus } from "./buildStatus";

export class Project {
    name: string;
    id: number;
    repositoryURL: string;
    branches: Branch[];
    serviceAccount: ServiceAccount;
    status: BuildStatus;
}