import { Project } from "./project";
import { BuildStatus } from "./buildStatus";

export class Projectgroup {
    name: string;
    id: number;
    projects: Project[];
    status: BuildStatus;
}