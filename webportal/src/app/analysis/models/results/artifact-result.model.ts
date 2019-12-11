import { Condition } from '../condition.model';

export class ArtifactResult {
    name: string;
    type: string;

    conditions: Condition[];

    numAffectedCases: number;
}
