export class ProcessMap {
    edges: GraphEdge[];
}

export class GraphEdge {
    sourceEvent: string;
    targetEvent: string;

    avgDuration: number;
    minDuration: number;
    maxDuration: number;

    occurrence: number;
}

export class ProcessMapSettings {
    public mode = 'occurrence';
}
