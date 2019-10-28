import {Component, ElementRef, Input, OnChanges, OnInit, ViewChild} from '@angular/core';

import * as moment from 'moment';
import * as d3 from 'd3';
import * as dagreD3 from 'dagre-d3';
import { Condition } from '../models/condition.model';
import { ProcessMap, ProcessMapSettings } from '../models/processmap.model';
import { QueryService } from '../shared/query.service';
import { QueryConvertService } from '../shared/query-convert.service';
import { LocalStorageService } from 'src/app/shared/storage.service';



@Component({
  selector: 'app-processmap',
  templateUrl: './processmap.component.html',
  styleUrls: ['./processmap.component.scss']
})
export class ProcessMapComponent implements OnChanges {
  @ViewChild('processmap', {static: true}) private processmapContainer: ElementRef;

  @Input() private logName: string;
  @Input() private conditions: Condition[];

  public data: ProcessMap;
  public settings: ProcessMapSettings;

  public zoom: any;
  public svg: any;
  public inner: any;

  public graph: any;

  constructor(
    private queryService: QueryService,
    private queryConvertService: QueryConvertService,
    private storageService: LocalStorageService) {
  }

  ngOnChanges() {
    this.update();
  }

  update() {
    if (!this.logName || !this.conditions) {
      return;
    }

    // query process map
    this.queryService.getProcessMap(this.logName, this.queryConvertService.convertToQuery(this.conditions))
      .subscribe(response => {
        this.data = response.processMap;
        this.createProcessMap();
      });

    this.loadSettings();
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

    this.createProcessMap();
  }

  createProcessMap() {
    if (this.data.edges.length === 0) {
      return;
    }

    this.graph = new dagreD3.graphlib.Graph({directed: true, multigraph: true, compound: true})
      .setGraph({
        acyclicer: 'greedy',
        align: 'DR'
      });

    const nodes = [];
    for (const edge of this.data.edges) {
      // add as nodes
      if (nodes.indexOf(edge.sourceEvent) === -1) {
        nodes.push(edge.sourceEvent);
        this.addNode(edge.sourceEvent, this.graph);
      }
      if (nodes.indexOf(edge.targetEvent) === -1) {
        nodes.push(edge.targetEvent);
        this.addNode(edge.targetEvent, this.graph);
      }

      // add edge
      this.graph.setEdge(this.getCleanName(edge.sourceEvent), this.getCleanName(edge.targetEvent),
        {
          label: this.settings.mode === 'occurrence' ? edge.occurrence + '' : moment.duration(edge.avgDuration, 'seconds').humanize(),
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

  addNode(node: string, graph: any) {
    if (node === 'Startknoten') {
      graph.setNode(this.getCleanName(node), {
        label: node,
        rx: 5,
        ry: 5,
        labelStyle: 'font-size: 0.75em; fill: white;',
        style: 'fill: #00cc66'
      });
    } else if (node === 'Endknoten') {
      graph.setNode(this.getCleanName(node), {
        label: node,
        rx: 5,
        ry: 5,
        labelStyle: 'font-size: 0.75em; fill: white;',
        style: 'fill: #ff3300'
      });
    } else {
      graph.setNode(this.getCleanName(node), {label: node, rx: 5, ry: 5});
    }
  }

  getCleanName(text: string) {
    return text.split(' ').join('')
      .split(':').join('')
      .split('.').join('')
      .split('(').join('')
      .split(')').join('');
  }
}
