import {Component, ElementRef, Input, OnChanges, OnInit, ViewChild} from '@angular/core';

import * as moment from 'moment';

import * as dagre from 'cytoscape-dagre';
import * as cytoscape from 'cytoscape';

import {ProcessMap} from '../../entities/processmap';
import { QueryService } from 'src/app/services/query.service';
import { Condition } from 'src/app/entities/conditions/condition';
import { ProcessMapSettings } from 'src/app/entities/settings/process-map-settings';
import { LocalStorageService } from 'src/app/services/storage.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-processmap',
  templateUrl: './processmap.component.html',
  styleUrls: ['./processmap.component.scss']
})
export class ProcessMapComponent implements OnChanges {
  @ViewChild('processmap', {static: true}) private processmapContainer: ElementRef;

  @Input() private logName: string;
  @Input() private conditions: Condition[];

  private data: ProcessMap;
  private settings: ProcessMapSettings;

  private zoom;
  private svg;
  private g;
  private graph;

  private noData = false;
  private progress = true;

  constructor(
    private queryService: QueryService,
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
    this.progress = true;

    this.queryService.getProcessMap(this.logName, this.queryService.convertToQuery(this.conditions))
      .subscribe(processMap => {
        this.data = processMap;
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
    this.progress = false;

    if (this.data.edges.length === 0) {
      this.noData = true;
      return;
    }

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
        minLen: (edge) => (edge.source === 'Startknoten' || edge.target === 'Endknoten') ? 2 : 1
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
            'label': this.settings.mode === 'duration' ? 'data(averageDuration)' : 'data(occurrence)',
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
