import { Class } from './enums';

// export const BACKEND_URL = 'http://april.timonolle.de:5000';
export const BACKEND_URL = 'http://localhost:5000';

export interface EventLog {
  cases: Case[];
  attributes: {
    evaluation?: {
      combined: EvaluationDetail;
      forward: EvaluationDetail;
      backward: EvaluationDetail;
    };
    queryParams?: QueryParams;
    [key: string]: any;
  };
}

export interface Case {
  id: string;
  events: Event[];
  attributes: {
    label?: string;
    [key: string]: any;
  };
}

export interface Event {
  name: string;
  timestamp: string;
  attributes: {
    detection: Attribute[];
    [key: string]: any;
  };
}

export interface Attribute {
  name: string;
  value: string;
  score?: number;
  score_backward?: number;
  prediction?: any[];
  prediction_backward?: any[];
  attention?: any[];
  attention_backward?: any[];
  target?: Class;
  classification?: Class;
  classification_backward?: Class;
}

export interface Evaluation {
  forward: EvaluationDetail;
  tauForward: number[];
  backward?: EvaluationDetail;
  tauBackward?: number[];
  combined?: EvaluationDetail;
  numClasses?: number;
}

export interface EvaluationDetail {
  combined: EvaluationResult;
  cf?: EvaluationResult;
  data?: EvaluationResult;
  cm: { [key: string]: { [key: string]: number } };
}

export interface EvaluationResult {
  precision: number;
  recall: number;
  f1: number;
  support: number;
}

export interface EvaluationParams {
  mode: string;
  base: string;
  strategy: string;
  heuristic: string;
  tauForward?: number[];
  tauBackward?: number[];
  axis?: number;
  detailed?: boolean;
}

export interface Model {
  id: number;
  creationDate: any;
  algorithm: string;
  trainingEventLog: string;
  trainingDuration: number;
  prettyTrainingDuration?: string;
  fileName: string;
  trainingHost: string;
  hyperparameters: any;
  prettyHyperparameters?: any;
  modelFileExists: boolean;
  cached: boolean;
  supportedParameters?: {
    heuristics: string[];
    strategies: string[];
    modes: string[];
    bases: string[];
    reductions: string[];
    anomalyTypes: string[];
    attention: boolean;
    classesAvailable: boolean;
    numAttributes: number;
  };
}

export interface QueryParams {
  mode: string;
  base: string;
  strategy: string;
  heuristic: string;
  tauForward: number[];
  tauBackward: number[];
  start: number;
  numCases: number;
  seed: number;
  reduction: string;
  anomalyType: string;
}
