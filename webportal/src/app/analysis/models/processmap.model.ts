export class ProcessMap {
    edges: GraphEdge[];
}

export class GraphEdge {
    sourceEvent: number;
    targetEvent: number;

    avgDuration: number;
    minDuration: number;
    maxDuration: number;

    occurrence: number;

    variants: number[];
}

export class ProcessMapSettings {
    public mode = 'occurrence';
}
