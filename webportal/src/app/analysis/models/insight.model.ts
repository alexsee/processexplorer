export class Insight {
    title: string;
    subTitle: string;

    effectSize: number;
    casesWithin: number;
    casesWithout: number;
    averageWithin: number;
    averageWithout: number;
    stddevWithin: number;
    stddevWithout: number;
    format: string;

    labels: string[];
    within: number[];
    without: number[];
}
