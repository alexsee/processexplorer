import { ProcessMap } from 'src/app/analysis/models/processmap.model';
import { Variant } from '../variant.model';

export class ProcessMapResult {
    processMap: ProcessMap;
    variants: Variant[];
}
