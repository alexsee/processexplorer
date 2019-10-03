/*!
Copyright (c) The Cytoscape Consortium

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the “Software”), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
of the Software, and to permit persons to whom the Software is furnished to do
so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

;(function(){ 'use strict';

  // registers the extension on a cytoscape lib ref
  var register = function( cytoscape, dagre ){
    if( !cytoscape || !dagre ){ return; } // can't register if cytoscape unspecified

    var isFunction = function(o){ return typeof o === 'function'; };

    // default layout options
    var defaults = {
      // dagre algo options, uses default value on undefined
      nodeSep: undefined, // the separation between adjacent nodes in the same rank
      edgeSep: undefined, // the separation between adjacent edges in the same rank
      rankSep: undefined, // the separation between adjacent nodes in the same rank
      rankDir: undefined, // 'TB' for top to bottom flow, 'LR' for left to right
      minLen: function( edge ){ return 1; }, // number of ranks to keep between the source and target of the edge
      edgeWeight: function( edge ){ return 1; }, // higher weight edges are generally made shorter and straighter than lower weight edges

      // general layout options
      fit: true, // whether to fit to viewport
      padding: 30, // fit padding
      spacingFactor: undefined, // Applies a multiplicative factor (>0) to expand or compress the overall area that the nodes take up
      nodeDimensionsIncludeLabels: undefined, // whether labels should be included in determining the space used by a node (default true)
      animate: false, // whether to transition the node positions
      animateFilter: function( node, i ){ return true; }, // whether to animate specific nodes when animation is on; non-animated nodes immediately go to their final positions
      animationDuration: 500, // duration of animation in ms if enabled
      animationEasing: undefined, // easing of animation if enabled
      boundingBox: undefined, // constrain layout bounds; { x1, y1, x2, y2 } or { x1, y1, w, h }
      transform: function( node, pos ){ return pos; }, // a function that applies a transform to the final node position
      ready: function(){}, // on layoutready
      stop: function(){} // on layoutstop
    };

    // constructor
    // options : object containing layout options
    function DagreLayout( options ){
      var opts = this.options = {};
      for( var i in defaults ){ opts[i] = defaults[i]; }
      for( var i in options ){ opts[i] = options[i]; }
    }

    // runs the layout
    DagreLayout.prototype.run = function(){
      var options = this.options;
      var layout = this;

      var cy = options.cy; // cy is automatically populated for us in the constructor
      var eles = options.eles;

      var getVal = function( ele, val ){
        return isFunction(val) ? val.apply( ele, [ ele ] ) : val;
      };

      var bb = options.boundingBox || { x1: 0, y1: 0, w: cy.width(), h: cy.height() };
      if( bb.x2 === undefined ){ bb.x2 = bb.x1 + bb.w; }
      if( bb.w === undefined ){ bb.w = bb.x2 - bb.x1; }
      if( bb.y2 === undefined ){ bb.y2 = bb.y1 + bb.h; }
      if( bb.h === undefined ){ bb.h = bb.y2 - bb.y1; }

      var g = new dagre.graphlib.Graph({
        multigraph: true,
        compound: true
      });

      var gObj = {};
      var setGObj = function( name, val ){
        if( val != null ){
          gObj[ name ] = val;
        }
      };

      setGObj( 'nodesep', options.nodeSep );
      setGObj( 'edgesep', options.edgeSep );
      setGObj( 'ranksep', options.rankSep );
      setGObj( 'rankdir', options.rankDir );

      g.setGraph( gObj );

      g.setDefaultEdgeLabel(function() { return {}; });
      g.setDefaultNodeLabel(function() { return {}; });

      // add nodes to dagre
      var nodes = eles.nodes();
      for( var i = 0; i < nodes.length; i++ ){
        var node = nodes[i];
        var nbb = node.layoutDimensions( options );

        g.setNode( node.id(), {
          width: nbb.w,
          height: nbb.h,
          name: node.id()
        } );

        // console.log( g.node(node.id()) );
      }

      // set compound parents
      for( var i = 0; i < nodes.length; i++ ){
        var node = nodes[i];

        if( node.isChild() ){
          g.setParent( node.id(), node.parent().id() );
        }
      }

      // add edges to dagre
      var edges = eles.edges().stdFilter(function( edge ){
        return !edge.source().isParent() && !edge.target().isParent(); // dagre can't handle edges on compound nodes
      });
      for( var i = 0; i < edges.length; i++ ){
        var edge = edges[i];

        g.setEdge( edge.source().id(), edge.target().id(), {
          minlen: getVal( edge, options.minLen ),
          weight: getVal( edge, options.edgeWeight ),
          name: edge.id()
        }, edge.id() );

        // console.log( g.edge(edge.source().id(), edge.target().id(), edge.id()) );
      }

      dagre.layout( g );

      // let gEdgeIds = g.edges();
      // for( let i = 0; i < gEdgeIds.length; i++ ){
      //   let id = gEdgeIds[i];
      //   let e = g.edge( id );

      //   if (e && e.points) {
      //     if (e.points.length > 3) {
      //       console.log('More than 3 points', e.points);
      //       let distances = [];

      //       let pStart = e.points[0];
      //       let pEnd = e.points[e.points.length - 1];

      //       let slope = (pEnd.y - pStart.y) / (pEnd.x - pStart.x);
      //       let yIntercept = pStart.y - slope * pStart.x;
      //       let slopeOrthogonal = -1 * (1 / slope);

      //       let getDistance = function(pSegment) {
      //         let result = {
      //           'distance': pSegment.x - pStart.x,
      //           'weight': Math.abs(pSegment.y - pStart.y) / Math.abs(pEnd.y - pStart.y)
      //         };

      //         if (pEnd.x - pStart.x === 0) {
      //           return result;
      //         }

      //         //result.distance = (slope * pSegment.x - pSegment.y + yIntercept) / (Math.sqrt(Math.pow(slope, 2) + 1));

      //         let y2 = pEnd.y;
      //         let y1 = pStart.y;
      //         let x2 = pEnd.x;
      //         let x1 = pStart.x;
      //         let y3 = pSegment.y;
      //         let x3 = pSegment.x;
      //         let k = ((y2-y1) * (x3-x1) - (x2-x1) * (y3-y1)) / (Math.pow((y2-y1), 2) + (Math.pow((x2-x1), 2)));
      //         let x4 = x3 - k * (y2-y1);
      //         let y4 = y3 + k * (x2-x1);

      //         result.distance = (Math.sqrt(Math.pow((y4-y3), 2) + Math.pow((x4-x3), 2)));

      //         let d = (pSegment.x - pStart.x) * (pEnd.y - pStart.y) - (pSegment.y - pStart.y) * (pEnd.x - pStart.x);
      //         if(d > 0) {
      //           result.distance = -result.distance;
      //         }

      //         let yInterceptOrthogonal = pSegment.y - (slopeOrthogonal * pSegment.x);
      //         let distanceOrhthogonal = (slopeOrthogonal * pStart.x - pStart.y + yInterceptOrthogonal) / (Math.sqrt(Math.pow(slopeOrthogonal, 2) + 1));

      //         result.weight = distanceOrhthogonal / Math.sqrt(Math.pow(pEnd.x - pStart.x, 2) + Math.pow(pEnd.y - pStart.y, 2));

      //         if(result.weight < 0) {
      //           result.weight = -result.weight;
      //         }

      //         return result;
      //       };

      //       for ( var j = 1; j < e.points.length - 1; j++) {
      //         distances.push(getDistance(e.points[j]));
      //       }

      //       //distances[distances.length - 1].distance = 0;
      //       //distances[distances.length - 1].weight = 1;
      //       console.log('Distances calculated', distances);

      //       // cy.style().selector('edge#' + id.name).style('curve-style', 'segments').update();
      //       // cy.style().selector('edge#' + id.name).style('segment-distances', distances.map(distance => distance.distance).join(' '));
      //       // cy.style().selector('edge#' + id.name).style('segment-weights', distances.map(distance => distance.weight).join(' '));
      //     } else {
      //       console.log('Less than 3 points', e);
      //       cy.style().selector('edge#' + id.name).style('curve-style', 'bezier').update();
      //     }
      //   }
      // }

      let gNodeIds = g.nodes();
      for( let i = 0; i < gNodeIds.length; i++ ){
        let id = gNodeIds[i];
        let n = g.node( id );

        cy.getElementById(id).scratch().dagre = n;
      }

      let dagreBB;

      if( options.boundingBox ){
        dagreBB = { x1: Infinity, x2: -Infinity, y1: Infinity, y2: -Infinity };
        nodes.forEach(function( node ){
          let dModel = node.scratch().dagre;

          dagreBB.x1 = Math.min( dagreBB.x1, dModel.x );
          dagreBB.x2 = Math.max( dagreBB.x2, dModel.x );

          dagreBB.y1 = Math.min( dagreBB.y1, dModel.y );
          dagreBB.y2 = Math.max( dagreBB.y2, dModel.y );
        });

        dagreBB.w = dagreBB.x2 - dagreBB.x1;
        dagreBB.h = dagreBB.y2 - dagreBB.y1;
      } else {
        dagreBB = bb;
      }

      let constrainPos = function( p ){
        if( options.boundingBox ){
          let xPct = dagreBB.w === 0 ? 0 : (p.x - dagreBB.x1) / dagreBB.w;
          let yPct = dagreBB.h === 0 ? 0 : (p.y - dagreBB.y1) / dagreBB.h;

          return {
            x: bb.x1 + xPct * bb.w,
            y: bb.y1 + yPct * bb.h
          };
        } else {
          return p;
        }
      };

      nodes.layoutPositions(layout, options, function( ele ){
        ele = typeof ele === "object" ? ele : this;
        let dModel = ele.scratch().dagre;

        return constrainPos({
          x: dModel.x,
          y: dModel.y
        });
      });

      return this; // chaining
    };

    cytoscape('layout', 'dagre', DagreLayout);

  };

  if( typeof module !== 'undefined' && module.exports ){ // expose as a commonjs module
    module.exports = function( cytoscape, dagre ){
      register( cytoscape, dagre || require('dagre') );
    };
  } else if( typeof define !== 'undefined' && define.amd ){ // expose as an amd/requirejs module
    define('cytoscape-dagre', function(){
      return register;
    });
  }

  if( typeof cytoscape !== 'undefined' && typeof dagre !== 'undefined' ){ // expose to global cytoscape (i.e. window.cytoscape)
    register( cytoscape, dagre );
  }

})();
