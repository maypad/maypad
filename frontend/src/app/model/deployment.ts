import { Build } from './build';

export class Deployment {
    timestamp: string;
    build: Build;
    projectName: string;
    branchName: string;
    type: string;
}
