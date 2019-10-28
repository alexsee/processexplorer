import { Condition } from './condition.model';

export class Recommendation {
    public score: number;
    public numTraces: number;
    public conditions: Condition[];
}
