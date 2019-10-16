export class Insight {
    title: string;
    subTitle: string;

    effectSize: number;
    averageWithin: number;
    averageWithout: number;
    stddevWithin: number;
    stddevWithout: number;
    format: string;

    labels: string[];
    within: number[];
    without: number[];
}
