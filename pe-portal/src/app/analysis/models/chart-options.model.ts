export class ChartComponentOptions {
    type: string;

    dimensions: any[];
    kpis: any[];

    tooltipsShow: boolean;

    x: ChartXAxisConfig;
    y: ChartYAxisConfig;
    y2: ChartYAxisConfig;

    axis0: ChartAxis[];
    axis1: ChartAxis[];
    axis2: ChartAxis[];

    legendShow: boolean;
    legendPosition?: string;
}

export class ChartXAxisConfig {
    show?: boolean;
    type?: string;
    localtime?: boolean;
    categories?: string[];
    tick?: ChartXAxisTickConfig;
    max?: number;
    min?: number;
    height?: number;
    label?: string;
}

export class ChartYAxisConfig {
    show?: boolean;
    inner?: boolean;
    type?: string;
    max?: number;
    min?: number;
    inverted?: boolean;
    center?: number;
    label?: string;
    tick?: ChartYAxisTickConfig;
    padding?: number;
}

export class ChartXAxisTickConfig {
    centered?: boolean;
    format?: any;
    culling?: boolean;
    count?: number;
    fit?: boolean;
    values?: number;
    rotate?: number;
    outer?: boolean;
    multiline?: boolean;
    multilineMax?: number;
    width?: number;
}

export class ChartYAxisTickConfig {
    format?: any;
    outer?: boolean;
    values?: number[];
    count?: number;
}

export class ChartAxis {
    text: string;
    alias: string;
    type: string;

    secondaryAxis?: boolean;
}
