export class SocialNetwork {
    edges: SocialNetworkEdge[];
}

export class SocialNetworkEdge {
    sourceResource: string;
    targetResource: string;

    avgDuration: number;
    minDuration: number;
    maxDuration: number;

    occurrence: number;

    variants: number[];
}
