import {Component, ElementRef, Input, OnChanges, OnInit, ViewChild} from '@angular/core';

import * as moment from 'moment';

import * as cytoscape from 'cytoscape';
import dagre from 'cytoscape-dagre';
import klay from 'cytoscape-klay';
import dot from './cytoscape-dot';
import {ProcessMap} from '../entities/processmap';

@Component({
  selector: 'app-processmap',
  templateUrl: './processmap.component.html',
  styleUrls: ['./processmap.component.scss']
})
export class ProcessmapComponent implements OnInit, OnChanges {
  @ViewChild('processmap', {static: true}) private processmapContainer: ElementRef;
  @Input() private data: ProcessMap;

  private zoom;
  private svg;
  private g;
  private graph;

  constructor() {
  }

  ngOnInit() {
    if (this.data.edges.length > 0) {
      this.createProcessMap();
    }
  }

  ngOnChanges() {
    if (this.data.edges.length > 0) {
      this.createProcessMap();
    }
  }

  createProcessMap() {
    const element = this.processmapContainer.nativeElement;

    const elements = {
      nodes: [],
      edges: []
    };

    const nodes = [];
    for (const edge of this.data.edges) {
      // add as nodes
      if (nodes.indexOf(edge.sourceEvent) === -1) {
        nodes.push(edge.sourceEvent);
      }
      if (nodes.indexOf(edge.targetEvent) === -1) {
        nodes.push(edge.targetEvent);
      }

      elements.edges.push({data: {
        occurrence: edge.occurrence,
        edgeWeight: edge.occurrence,

        averageDuration: moment.duration(edge.avgDuration, 'seconds').humanize(),

        source: this.getCleanName(edge.sourceEvent),
        target: this.getCleanName(edge.targetEvent)
      }});
    }

    for (const node of nodes) {
      if (node === 'Startknoten') {
        elements.nodes.push({data: {id: this.getCleanName(node), label: node}, style: {backgroundColor: '#00cc66'}});
      } else if (node === 'Endknoten') {
        elements.nodes.push({data: {id: this.getCleanName(node), label: node}, style: {backgroundColor: '#ff3300'}});
      } else {
        elements.nodes.push({data: {id: this.getCleanName(node), label: node}});
      }
    }

    cytoscape.use(dagre);
    const cy = cytoscape({
      container: element,

      boxSelectionEnabled: false,
      autounselectify: true,

      layout: {
        name: 'dagre',
        // name: 'klay',
        // name: 'dot'

        // klay: {
        //   aspectRatio: 1.8,
        //   direction: 'DOWN',
        //   edgeRouting: 'SPLINES',
        //   nodeLayering: 'LONGEST_PATH',
        //   layoutHierarchy: true,
        //   thoroughness: 10,
        //   feedbackEdges: true,
        //   fixedAlignment: 'BALANCED',
        //   linearSegmentsDeflectionDampening: 0
        // }
        rankDir: 'TB',

        edgeSep: 50,
        nodeSep: 50,

        nodeDimensionsIncludeLabels: true,
        padding: 0,
        minLen: (edge) => (edge.source === 'Startknoten' || edge.target === 'Endknoten') ? 2 : 1,

        // edgeWeight: (edge) => edge.edgeWeight
      },

      style: [
        {
          selector: 'node',
          style: {
            'content': 'data(label)',
            'background-color': '#11479e',
            'shape': 'diamond',
            'font-size': '12px',
            'text-valign': 'center',
            'text-halign': 'right',
            'text-margin-x': '0.3em',
            'overlay-padding': '6px',
            'z-index': '10',
            'height': '1.2em',
            'width': '1.2em'
          }
        },
        {
          selector: 'edge',
          style: {
            'label': 'data(averageDuration)',
            'target-arrow-shape': 'triangle',
            'line-color': '#9dbaea',
            'target-arrow-color': '#9dbaea',
            'curve-style': 'bezier',
            'font-size': '9px',
            'text-halign': 'right',
            'text-background-color': 'black',
            'text-background-opacity': 0.2,
            'text-background-shape': 'roundrectangle',
            'text-background-padding': '3px'
          }
        }
      ],

      elements: elements
    });
  }

  getCleanName(text: string) {
    return text.split(' ').join('')
      .split(':').join('')
      .split('.').join('')
      .split('(').join('')
      .split(')').join('');
  }
}
