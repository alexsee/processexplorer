import { EventLog } from './eventlog.model';

export class EventLogModel {
    id?: number;
    logName?: string;
    modelId: number;
    modelName: string;
    creationDate: Date;
    algorithm: string;
    trainingDuration: number;
    hyperparameters: string;
    state: string;
    use: boolean;
}