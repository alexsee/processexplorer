package de.tk.processmining.data.model;

import java.util.List;

/**
 * @author Alexander Seeliger on 24.09.2019.
 */
public class Graph {

    private List<GraphEdge> edges;

    public List<GraphEdge> getEdges() {
        return edges;
    }

    public void setEdges(List<GraphEdge> edges) {
        this.edges = edges;
    }
}
