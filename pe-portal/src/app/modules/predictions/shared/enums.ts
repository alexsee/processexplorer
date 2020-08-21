export enum Axis {
  CASE = 0,
  EVENT = 1,
  ATTRIBUTE = 2
}

export enum Class {
  NORMAL,
  ANOMALY,
  INSERT,
  SKIP,
  REWORK,
  EARLY,
  LATE,
  SHIFT,
  REPLACE,
  ATTRIBUTE
}

export enum Mode {
  BINARIZE = 'binarize',
  CLASSIFY = 'classify'
}

export enum Base {
  LEGACY = 'legacy',
  SCORES = 'scores',
  CONFIDENCE = 'confidence'
}

export enum Normalization {
  CONFIDENCE = 'confidence',
  CONFIDENCE_MIN_MAX = 'confidence_min_max'
}

export enum Strategy {
  SINGLE = 'single',
  ATTRIBUTE = 'position',
  POSITION = 'attribute',
  POSITION_ATTRIBUTE = 'position_attribute'
}

export enum Heuristic {
  DEFAULT = 'default',
  MANUAL = 'manual',
  BEST = 'best',
  ELBOW = 'elbow',
  LOWEST_PLATEAU = 'lowest_plateau',
  LEAST_CHANGE = 'least_change',
  RATIO = 'ratio',
  AVERAGE = 'average'
}
