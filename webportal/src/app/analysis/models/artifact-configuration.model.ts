import { ArtifactUIField } from './artifact-ui-field.model';

export class ArtifactConfiguration {
    id: number;
    type: string;
    configuration: string;
    activated: boolean;

    fields: ArtifactUIField[];
    data: any;
}
