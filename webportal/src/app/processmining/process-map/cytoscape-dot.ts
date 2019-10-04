import Viz from 'viz.js';
import {Module, render} from 'viz.js/full.render';

function CytoscapeDotLayout(options) {

  this.options = options;
}

CytoscapeDotLayout.prototype.run = function() {
  let dotStr = 'digraph G {\n';
  dotStr += 'rankdir=TB;\n';

  const nodes = this.options.eles.nodes();
  for (let i = 0; i < nodes.length; ++i) {
    const node = nodes[i];
    const {w, h} = node.layoutDimensions(this.options);

    console.info(this.options);

    dotStr += `  ${node.id()}[label="${node.id()}"];\n`;
  }

  dotStr += '  { rank=source; "Startknoten" }\n';
  dotStr += '  { rank=sink; "Endknoten" }\n';

  const edges = this.options.eles.edges();
  for (let i = 0; i < edges.length; ++i) {
    const edge = edges[i];
    dotStr += `  ${edge.source().id()} -> ${edge.target().id()} [weight=${edge.data('edgeWeight')}];\n`;
  }

  dotStr += '}';

  console.info(dotStr);

  const viz = new Viz({Module, render});

  viz.renderSVGElement(dotStr).then((svg) => {

    const svgNodes = svg.getElementsByClassName('node');

    const idToPositions = {};

    let minY = Number.POSITIVE_INFINITY;

    for (let i = 0; i < svgNodes.length; ++i) {
      const node = svgNodes[i];

      const id = node.getElementsByTagName('title')[0].innerHTML.trim();

      const ellipse = node.getElementsByTagName('ellipse')[0];
      const y = ellipse.cy.baseVal.value * 2;

      idToPositions[id] = {
        x: ellipse.cx.baseVal.value * 2,
        y,
      };

      minY = Math.min(minY, y);
    }

    nodes.layoutPositions(this, this.options, (ele) => {
      let {x, y} = idToPositions[ele.id()];
      y -= minY - 30;
      return {x, y};
    });
  });

  return this;
};

export default function (cytoscape) {
  cytoscape('layout', 'dot', CytoscapeDotLayout);
}