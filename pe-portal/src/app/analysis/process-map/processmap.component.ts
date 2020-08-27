import {Component, ElementRef, Input, OnChanges, OnInit, ViewChild, SimpleChanges} from '@angular/core';

import HumanizeDuration from 'humanize-duration';
import * as d3 from 'd3';
import * as dagreD3 from 'dagre-d3';
import { Condition } from '../models/condition.model';
import { ProcessMap, ProcessMapSettings } from '../models/processmap.model';
import { QueryService } from '../shared/query.service';
import { QueryConvertService } from '../shared/query-convert.service';
import { LocalStorageService } from 'src/app/shared/storage.service';
import { Variant } from '../models/variant.model';
import { EventLogStatistics } from 'src/app/log/models/eventlog-statistics.model';
import { SocialNetwork } from '../models/social-network.model';

@Component({
  selector: 'app-processmap',
  templateUrl: './processmap.component.html',
  styleUrls: ['./processmap.component.scss']
})
export class ProcessMapComponent implements OnInit, OnChanges {
  @ViewChild('processmap', {static: true}) private processmapContainer: ElementRef;

  @Input() private logName: string;
  @Input() private context: EventLogStatistics;
  @Input() private conditions: Condition[];

  data: ProcessMap;
  socialNetwork: SocialNetwork;
  settings: ProcessMapSettings;

  toggleSocialNetwork = false;

  zoom: any;
  svg: any;
  inner: any;

  graph: any;

  variant = 0;
  variants: Variant[];
  minVariant: number;
  maxVariant: number;

  constructor(
    private queryService: QueryService,
    private queryConvertService: QueryConvertService,
    private storageService: LocalStorageService) {
  }

  ngOnInit() {
    this.loadSettings();
  }

  ngOnChanges(changes: SimpleChanges) {
    this.update();
  }

  update() {
    if (!this.logName || !this.conditions) {
      return;
    }

    this.loadSettings();

    if (!this.context) {
      return;
    }

    // query process map
    if (!this.toggleSocialNetwork) {
      this.queryService.getProcessMap(this.logName, this.queryConvertService.convertToQuery(this.conditions))
        .subscribe(response => {
          this.data = response.processMap;
          this.variants = response.variants;

          // set variants slider
          this.minVariant = 0;
          this.maxVariant = this.variants.length - 1;

          this.variants.sort(x => x.occurrence);
          this.setVariantSliderPareto();

          // compute process map
          this.createProcessMap();
        });
    } else {
      this.queryService.getSocialNetworkGraph(this.logName, this.queryConvertService.convertToQuery(this.conditions))
        .subscribe(response => {
          this.socialNetwork = response.socialNetwork;
          this.variants = response.variants;

          // set variants slider
          this.minVariant = 0;
          this.maxVariant = this.variants.length - 1;

          this.variants.sort(x => x.occurrence);
          this.setVariantSliderPareto();

          // compute process map
          this.createSocialNetwork();
        });
    }
  }

  loadSettings() {
    this.settings = this.storageService.readConfig(this.logName, 'processmap.settings');

    // default settings
    if (!this.settings) {
      this.settings = {
        mode: 'occurrence'
      };
    }
  }

  onSettingsChange() {
    this.storageService.writeConfig(this.logName, 'processmap.settings', this.settings);

    if (!this.toggleSocialNetwork) {
      this.createProcessMap();
    } else {
      this.createSocialNetwork();
    }
  }

  setVariantSliderPareto() {
    const total =  this.variants.reduce((s, current) => s + current.occurrence, 0) * .8;
    let i = 0;
    let sum = 0;

    for (const variant of this.variants) {
      sum += variant.occurrence;
      if (sum > total) {
        break;
      }

      i++;
    }

    this.variant = Math.min(250, i);
  }

  createProcessMap() {
    if (this.data.edges.length === 0) {
      return;
    }

    const selectedVariant = this.variants.slice(0, this.variant + 1);

    // generate graph
    this.graph = new dagreD3.graphlib.Graph({directed: true, multigraph: true, compound: true})
      .setGraph({
        align: 'DR',
        acyclicer: 'greedy',
        ranker: 'longest-path'
      });

    const nodes = [];
    for (const edge of this.data.edges) {
      // filter by variant
      const filteredVariants = selectedVariant.filter(x => edge.variants.includes(x.id));
      if (filteredVariants.length === 0) {
        continue;
      }

      // add as nodes
      edge.sourceEvent = edge.sourceEvent === null ? -1 : edge.sourceEvent;
      edge.targetEvent = edge.targetEvent === null ? -2 : edge.targetEvent;

      if (nodes.indexOf(edge.sourceEvent) === -1) {
        nodes.push(edge.sourceEvent);
        this.addNode(edge.sourceEvent, this.graph);
      }
      if (nodes.indexOf(edge.targetEvent) === -1) {
        nodes.push(edge.targetEvent);
        this.addNode(edge.targetEvent, this.graph);
      }

      // add edge
      this.graph.setEdge(edge.sourceEvent, edge.targetEvent,
        {
          label: this.settings.mode === 'occurrence'
            ? edge.occurrence + '' : HumanizeDuration(edge.avgDuration * 1000, { largest: 2, round: true }),
          curve: d3.curveBasis,
          weight: edge.occurrence
        });
    }

    // generate the renderer
    const render = new dagreD3.render();

    // render graph into svg g
    this.svg = d3.select(this.processmapContainer.nativeElement);
    this.inner = this.svg.select('g');

    // set zoom support
    this.zoom = d3.zoom()
      .on('zoom', x => {
        this.inner.attr('transform', d3.event.transform);
      });
    this.svg.call(this.zoom);

    render(this.inner, this.graph);

    // set initial scale
    const containerWidth = this.svg.node().getBoundingClientRect().width;

    const minScale = Math.min(this.svg.node().getBoundingClientRect().width / this.graph.graph().width,
      this.svg.node().getBoundingClientRect().height / this.graph.graph().height);

    this.svg.call(this.zoom.transform,
      d3.zoomIdentity.translate((containerWidth - this.graph.graph().width * minScale) / 2, 0)
      .scale(minScale));
  }

  createSocialNetwork() {
    if (this.socialNetwork.edges.length === 0) {
      return;
    }

    const selectedVariant = this.variants.slice(0, this.variant + 1);

    // generate graph
    this.graph = new dagreD3.graphlib.Graph({directed: true, multigraph: true, compound: true})
      .setGraph({
        align: 'DR',
        acyclicer: 'greedy',
        ranker: 'longest-path'
      });

    const nodes = [];
    for (const edge of this.socialNetwork.edges) {
      // filter by variant
      const filteredVariants = selectedVariant.filter(x => edge.variants.includes(x.id));
      if (filteredVariants.length === 0) {
        continue;
      }

      // add as nodes
      edge.sourceResource = edge.sourceResource === null ? 'start' : edge.sourceResource;
      edge.targetResource = edge.targetResource === null ? 'end' : edge.targetResource;

      if (nodes.indexOf(edge.sourceResource) === -1) {
        nodes.push(edge.sourceResource);
        this.addNodeStr(edge.sourceResource, this.graph);
      }
      if (nodes.indexOf(edge.targetResource) === -1) {
        nodes.push(edge.targetResource);
        this.addNodeStr(edge.targetResource, this.graph);
      }

      // add edge
      this.graph.setEdge(
        edge.sourceResource, edge.targetResource,
        {
          label: this.settings.mode === 'occurrence'
            ? edge.occurrence + '' : HumanizeDuration(edge.avgDuration * 1000, { largest: 2, round: true }),
          curve: d3.curveBasis,
          weight: edge.occurrence
        });
    }

    // generate the renderer
    const render = new dagreD3.render();

    // render graph into svg g
    this.svg = d3.select(this.processmapContainer.nativeElement);
    this.inner = this.svg.select('g');

    // set zoom support
    this.zoom = d3.zoom()
      .on('zoom', x => {
        this.inner.attr('transform', d3.event.transform);
      });
    this.svg.call(this.zoom);

    render(this.inner, this.graph);

    // set initial scale
    const containerWidth = this.svg.node().getBoundingClientRect().width;

    const minScale = Math.min(this.svg.node().getBoundingClientRect().width / this.graph.graph().width,
      this.svg.node().getBoundingClientRect().height / this.graph.graph().height);

    this.svg.call(this.zoom.transform,
      d3.zoomIdentity.translate((containerWidth - this.graph.graph().width * minScale) / 2, 0)
      .scale(minScale));
  }

  addNode(node: number, graph: any) {
    if (node === -1) {
      graph.setNode(node, {
        label: 'Start',
        rx: 5,
        ry: 5,
        labelStyle: 'font-size: 0.75em; fill: white;',
        style: 'fill: #00cc66'
      });
    } else if (node === -2) {
      graph.setNode(node, {
        label: 'End',
        rx: 5,
        ry: 5,
        labelStyle: 'font-size: 0.75em; fill: white;',
        style: 'fill: #ff3300'
      });
    } else {
      graph.setNode(node, {label: this.context.activities[node].name, rx: 5, ry: 5});
    }
  }

  addNodeStr(node: string, graph: any) {
    if (node === 'start') {
      graph.setNode(node, {
        label: 'Start',
        rx: 5,
        ry: 5,
        labelStyle: 'font-size: 0.75em; fill: white;',
        style: 'fill: #00cc66'
      });
    } else if (node === 'end') {
      graph.setNode(node, {
        label: 'End',
        rx: 5,
        ry: 5,
        labelStyle: 'font-size: 0.75em; fill: white;',
        style: 'fill: #ff3300'
      });
    } else {
      graph.setNode(node, {label: node, rx: 5, ry: 5});
    }
  }

  toggleGraph(): void {
    this.toggleSocialNetwork = !this.toggleSocialNetwork;
    this.update();
  }
}
