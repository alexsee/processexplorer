import {Component, ElementRef, Input, OnChanges, OnInit, ViewChild} from '@angular/core';
import * as cytoscape from 'cytoscape';
import dagre from 'cytoscape-dagre';
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

    let nodes = [];
    for (const edge of this.data.edges) {
      // add as nodes
      if (nodes.indexOf(edge.sourceEvent) === -1) {
        nodes.push(edge.sourceEvent);
      }
      if (nodes.indexOf(edge.targetEvent) === -1) {
        nodes.push(edge.targetEvent);
      }

      elements.edges.push({data: {edgeWeight: edge.occurrence, source: edge.sourceEvent, target: edge.targetEvent},
        style: { label: edge.occurrence }});
    }

    for (const node of nodes) {
      if (node === 'Startknoten') {
        elements.nodes.push({data: {id: node}, style: {label: node, backgroundColor: '#00cc66'}});
      } else if (node === 'Endknoten') {
        elements.nodes.push({data: {id: node}, style: {label: node, backgroundColor: '#ff3300'}});
      } else {
        elements.nodes.push({data: {id: node}, style: {label: node}});
      }
    }

    cytoscape.use(dagre);
    const cy = cytoscape({
      container: element,

      boxSelectionEnabled: false,
      autounselectify: true,

      layout: {
        name: 'dagre',
        rankDir: 'TB',

        rankSep: 40,
        edgeSep: 30,
        nodeSep: 40,

        nodeDimensionsIncludeLabels: true
      },

      style: [
        {
          selector: 'node',
          style: {
            'background-color': '#11479e',
            'content': 'data(name)',
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

}
