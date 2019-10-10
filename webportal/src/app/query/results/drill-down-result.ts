export class DrillDownResult {
    metaData: ColumnMetaData[];
    data: any[];
}

export class ColumnMetaData {
    public alias: string;
    public columnName: string;
    public columnType: string;
    public codes: string[];
}
