import { BuildStatus } from "./buildStatus";
import { Commit } from "./commit";
import { Build } from "./build";
import { Deployment } from "./deployment";

export class Branch {
    buildTool: string;
    deploymentTarget: string[];
    respPerson: string;
    dependencies: number[];
    readmeURL: string;
    branchURL: string;
    buildSuccessURL: string;
    buildFailURL: string;
    tags: string[];
    status: BuildStatus;
    lastCommit: Commit;
    builds: Build[];
    deployments: Deployment[];
}