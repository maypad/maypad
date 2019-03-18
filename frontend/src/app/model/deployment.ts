import { Build } from './build';
import { BuildStatus } from './buildStatus';

export class Deployment {
    timestamp: string;
    type: string;
    status: BuildStatus;
}
