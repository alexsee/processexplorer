import { Component, OnInit } from '@angular/core';
import * as d3 from 'd3';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { EventLog, Model, QueryParams, BACKEND_URL, Attribute } from '../shared/interfaces';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-model-detail',
  templateUrl: './model-detail.component.html',
  styleUrls: ['./model-detail.component.scss']
})
export class ModelDetailComponent implements OnInit {
  public eventColor = d3
    .scaleLinear<string>()
    .domain([0.0, 1.0])
    .range(['#F5F5F5', '#FFC107']);

  public probabilityColor = d3
    .scaleLinear<string>()
    .domain([0.0, 1.0])
    .range(['#FFFFFF', '#4CAF50']);

  // Global objects
  activatedRoute: ActivatedRoute;
  console: any;
  JSON: any;

  // Global data
  log: EventLog;
  model: Model;
  queryParams: QueryParams;

  tooltip: any;
  eventBbox: any = { top: 0, bottom: 0, left: 0, right: 0 };

  // Frontend params
  error: string;
  init = true;
  refreshing = false;
  evaluating = false;

  probabilitiesForward: { id: number; value: string; probability: number; color: any }[];
  probabilitiesBackward: { id: number; value: string; probability: number; color: any }[];
  showAttention = true;

  // Backend params
  id: string;
  _mode: string = null;
  _base = 'scores';
  strategy: string = null;
  heuristic: string = null;
  tauForward: number[];
  tauBackward: number[];
  start: number;
  numCases: number;
  seed: number;
  _reduction: string = null;
  anomalyType: string = null;
  axis = 0;

  constructor(private http: HttpClient, private route: ActivatedRoute, private titleService: Title) {
    this.activatedRoute = route;
    this.JSON = JSON;
    this.console = console;
    // this._evaluationParams.axis = this.axis;
    this.id = this.activatedRoute.snapshot.paramMap.get('id');
  }

  getEventColorDomain(log: EventLog) {
    let min = Infinity;
    let max = 0;
    for (const _case of log.cases) {
      for (const event of _case.events) {
        for (const attribute of event.attributes.detection) {
          min = Math.min(min, attribute.score, attribute.score_backward);
          max = Math.max(max, attribute.score, attribute.score_backward);
        }
      }
    }
    return [min, max];
  }

  ngOnInit(): void {
    this.tooltip = document.querySelector('#tip');

    this.http.get(`${BACKEND_URL}/models/${this.id}`).subscribe((model: Model) => {
      this.model = model;
    });

    this.activatedRoute.queryParamMap.subscribe((params: ParamMap) => {
      const p = {};
      for (const key of params.keys) {
        p[key] = '' + params.get(key);
      }
      this.fetchLog(p);
    });
  }

  fetchLog(params) {
    const p = params || this.params;
    this.refreshing = !this.init;

    this.http.get(`${BACKEND_URL}/models/${this.id}/result`, { responseType: 'json', params: p }).subscribe(
      (log: EventLog) => {
        this.log = log;
        this.queryParams = log && log.attributes && log.attributes.queryParams;

        this.resetParameters();
        this.eventColor = d3
          .scaleLinear<string>()
          .domain(this.getEventColorDomain(this.log))
          .range(['#F5F5F5', '#FFC107']);

        this.init = false;
        this.refreshing = false;
      },
      error => {
        this.error = error;

        this.init = false;
        this.refreshing = false;
      }
    );
  }

  checkParameters() {
    if (this.queryParams) {
      const base = this.queryParams.base !== this.base;
      const mode = this.queryParams.mode !== this.mode;
      const heuristic = this.queryParams.heuristic !== this.heuristic;
      const strategy = this.queryParams.strategy !== this.strategy;
      const start = this.queryParams.start !== this.start;
      const numCases = this.queryParams.numCases !== this.numCases;
      const seed = this.queryParams.seed !== this.seed;
      const anomalyType = this.queryParams.anomalyType !== this.anomalyType;
      const reduction = this.queryParams.reduction !== this.reduction;
      return base || mode || heuristic || strategy || start || numCases || seed || anomalyType || reduction;
    }
    return false;
  }

  resetParameters() {
    this.mode = this.queryParams && this.queryParams.mode;
    this.heuristic = this.queryParams && this.queryParams.heuristic;
    this.strategy = this.queryParams && this.queryParams.strategy;
    this.tauForward = this.queryParams && this.queryParams.tauForward;
    this.tauBackward = this.queryParams && this.queryParams.tauBackward;
    this.start = this.queryParams && this.queryParams.start;
    this.numCases = this.queryParams && this.queryParams.numCases;
    this.seed = this.queryParams && this.queryParams.seed;
    this.anomalyType = this.queryParams && this.queryParams.anomalyType;
    this.reduction = this.queryParams && this.queryParams.reduction;
    this.base = this.queryParams && this.queryParams.base;
  }

  get params() {
    const params = {};
    if (this.mode) {
      params['mode'] = this.mode;
    }
    if (this.base) {
      params['base'] = this.base;
    }
    if (this.heuristic) {
      params['heuristic'] = this.heuristic;
    }
    if (this.strategy) {
      params['strategy'] = this.strategy;
    }
    if (this.start) {
      params['start'] = this.start;
    }
    if (this.numCases) {
      params['numCases'] = this.numCases;
    }
    if (this.seed) {
      params['seed'] = this.seed;
    }
    if (this.reduction) {
      params['reduction'] = this.reduction;
    }
    if (this.anomalyType) {
      params['anomalyType'] = this.anomalyType;
    }
    return params;
  }

  get mode() {
    return this._mode;
  }

  set mode(mode) {
    if (this.mode === null && mode !== this.mode) {
      this.heuristic = this.model.supportedParameters.heuristics[0];
      this.strategy = this.model.supportedParameters.strategies[0];
    }
    if (mode === 'classify') {
      if (this.model.supportedParameters.numAttributes === 1) {
        this.axis = 1;
      } else {
        this.axis = 2;
      }
    }
    if (mode === null) {
      this.base = 'scores';
      this.heuristic = null;
      this.strategy = null;
      this.tauForward = null;
      this.tauBackward = null;
    }
    this._mode = mode;
  }

  get base() {
    return this._base;
  }

  set base(base) {
    this._base = base;
  }

  get reduction() {
    return this._reduction;
  }

  set reduction(reduction) {
    if (reduction === null) {
      this.start = this.queryParams.start;
      this.seed = null;
    } else {
      if (reduction === 'uniform') {
        this.anomalyType = null;
      }
      this.start = null;
      this.seed = this.queryParams.seed;
    }
    this._reduction = reduction;
  }

  getAttributeProbabilities(a: Attribute) {
     return a.prediction
        .map((p, i) => ({ ...p, id: i }))
        .filter((p, i) => i <= 4 || a.value === p.value || i === a.prediction.length - 1);
  }

}
