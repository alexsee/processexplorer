export class EventLog {
    public id: number;
    public logName: string;
    public fileName: string;
    public type: string;
    public creationDate: Date;

    public imported: boolean;
    public processed: boolean;

    public processing: boolean;
    public errorMessage: string;
}
