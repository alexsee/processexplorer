package de.tk.processmining.webservice.controller;

import de.tk.processmining.data.model.Graph;
import de.tk.processmining.data.model.Log;
import de.tk.processmining.data.model.Variant;
import de.tk.processmining.data.query.QueryManager;
import de.tk.processmining.data.query.condition.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Alexander Seeliger on 23.09.2019.
 */
@RestController
public class QueryController {

    private final QueryManager queryManager;

    @Autowired
    public QueryController(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    @RequestMapping("/statistics")
    public Log getStatistics(String logName) {
        return queryManager.getLogStatistics(logName);
    }

    @RequestMapping("getallpaths")
    public List<Variant> getAllPaths(String logName, @RequestBody List<Condition> conditions) {
        return queryManager.getAllPaths(logName, conditions);
    }

    @RequestMapping(value = "/getprocessmap", method = RequestMethod.POST)
    public Graph getProcessMap(@RequestParam String logName, @RequestBody List<Condition> conditions) {
        return queryManager.getProcessMap(logName, conditions);
    }

    @RequestMapping(value = "/ranks", method = RequestMethod.POST)
    public String getRanks(@RequestParam String logName, @RequestBody List<Condition> conditions) {
        var variants = queryManager.getAllPaths(logName, conditions);

        var preprocessed = new ArrayList<List<Node>>();
        var visited = new ArrayList<Node>();

        for (var variant : variants) {
            var v = new ArrayList<Node>();

            for (int i = 0; i < variant.getPath().length - 1; i++) {
                var start = new Activity(variant.getPath()[i]);
                if (!visited.contains(start)) {
                    v.add(start);
                    visited.add(start);
                }

                var transition = new Transition(variant.getPath()[i], variant.getPath()[i + 1]);
                if (!visited.contains(transition)) {
                    v.add(transition);
                    visited.add(transition);
                }
            }

            var end = new Activity(variant.getPath()[variant.getPath().length - 1]);
            if (!visited.contains(end)) {
                v.add(end);
                visited.add(end);
            }

            if(v.size() > 0) {
                preprocessed.add(v);
            }
        }

        var tree = new Tree();

        for (var variant : preprocessed) {

            if (variant.get(0) instanceof Activity && variant.get(variant.size() - 1) instanceof Activity) {
                // starts and ends with a node
                var rank = 1;
                var component = new ArrayList<Node>();
                for (var elem : variant) {
                    tree.push(elem, rank);
                    component.add(elem);
                    rank++;
                }

                tree.pushComponent(component);
            } else if (variant.get(0) instanceof Transition && variant.size() == 1) {
                // single edge
                System.out.println("EDGE");

                var edge = (Transition) variant.get(0);
                var n = tree.getRank(new Activity(edge.source));
                var m = tree.getRank(new Activity(edge.target));

                if (n < m) {
                    // forward edge
                    tree.push(edge, n + 1);
                } else if (m > n) {
                    // backward edge, but do nothing
                    tree.push(edge, n + 1);
                } else if (tree.isSameComponent(new Activity(edge.source), new Activity(edge.target))) {
                    tree.push(edge, n + 1);
                    tree.pushDown(new Activity(edge.target), 1, new ArrayList<>());
                } else {
                    tree.push(edge, n + 1);
                    tree.pushDown(new Activity(edge.target), 1, new ArrayList<>());
                }
            } else if (variant.get(0) instanceof Activity && variant.get(variant.size() - 1) instanceof Transition) {
                // starts with activity and ends with transition
                System.out.println("NODE-EDGE");

                var lastEdge = (Transition) variant.get(variant.size() - 1);
                var rank = tree.getRank(new Activity(lastEdge.target)) - 1;

                var component = new ArrayList<Node>();
                for (var elem : variant) {
                    tree.push(elem, rank);
                    component.add(elem);
                    rank--;
                }

                tree.pushComponent(component);
            } else if (variant.get(0) instanceof Transition && variant.get(variant.size() - 1) instanceof Activity) {
                // starts with transition and ends with activity
                System.out.println("EDGE-NODE");

                var firstEdge = (Transition) variant.get(0);
                var rank = tree.getRank(new Activity(firstEdge.source)) + 1;

                var component = new ArrayList<Node>();
                for (var elem : variant) {
                    tree.push(elem, rank);
                    component.add(elem);
                    rank++;
                }

                tree.pushComponent(component);
            } else if (variant.get(0) instanceof Transition && variant.get(variant.size() - 1) instanceof Transition) {
                // starts with transition and ends with transition
                System.out.println("EDGE-EDGE");

                var firstEdge = (Transition) variant.get(0);
                var lastEdge = (Transition) variant.get(variant.size() - 1);

                var n = tree.getRank(new Activity(firstEdge.source));
                var m = tree.getRank(new Activity(lastEdge.target));

                if (!tree.isSameComponent(new Activity(firstEdge.source), new Activity(lastEdge.target))) {
                    var rank = tree.getRank(new Activity(firstEdge.source)) + 1;
                    for (var elem : variant) {
                        tree.push(elem, rank);
                        rank++;
                    }

                    tree.pushDown(new Activity(lastEdge.target), variant.size(), new ArrayList<>());
                } else if (m >= n) {
                    var rank = n + 1;
                    for (var elem : variant) {
                        tree.push(elem, rank);
                        rank++;
                    }

                    if (rank - m + 1 > 0) {
                        tree.pushDown(variant.get(variant.size() - 1), rank - m + 1, new ArrayList<>());
                    }
                } else {
                    System.out.println("TDOO");
                }
            }
        }

        return tree.toString();
    }

    class Tree {
        private Map<Node, Integer> level = new HashMap<Node, Integer>();
        private List<List<Node>> components = new ArrayList<>();

        public void push(Node node, Integer level) {
            this.level.put(node, level);
        }

        public void pushComponent(List<Node> nodes) {
            this.components.add(nodes);
        }

        public void pushDown(Node node, Integer plus, List<Node> visited) {
            System.out.println("Push down: " + node);

            // first, push down this node
            this.level.put(node, this.level.get(node) + plus);

            // second, push all successors down
            for (var n : this.level.keySet()) {
                if (n.isSuccessor(node) && !visited.contains(n) && level.get(n) > level.get(node)) {
                    var vis = new ArrayList<Node>();
                    vis.add(n);
                    vis.addAll(visited);

                    this.pushDown(n, plus, vis);
                }
            }
        }

        public Integer getRank(Node node) {
            return this.level.get(node);
        }

        public boolean isSameComponent(Node a, Node b) {
            for (var component : this.components) {
                if (component.contains(a) && component.contains(b)) {
                    return true;
                }
            }

            return false;
        }

        public String toString() {
            var reverse = new HashMap<Integer, List<Node>>();

            for (var key : this.level.keySet()) {
                var level = this.level.get(key);

                var list = reverse.get(level);
                if (list == null) {
                    list = new ArrayList<>();
                }

                list.add(key);

                reverse.put(level, list);
            }

            var str = "";
            for (var level : reverse.keySet()) {
                str += "<p>" + reverse.get(level).stream().map(Object::toString).collect(Collectors.joining(";")) + "</p>\r\n";
            }

            return str;
        }
    }

    abstract class Node {
        abstract boolean isSuccessor(Node node);
    }

    class Activity extends Node {
        public String activity;

        Activity(String activity) {
            super();
            this.activity = activity;
        }

        public boolean isSuccessor(Node node) {
            return false;
        }

        @Override
        public int hashCode() {
            return this.activity.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Activity) {
                var other = (Activity) obj;
                return other.activity.equals(activity);
            }
            return false;
        }

        public String toString() {
            return this.activity;
        }
    }

    class Transition extends Node {
        public String source;
        public String target;

        Transition(String source, String target) {
            super();
            this.source = source;
            this.target = target;
        }

        @Override
        public int hashCode() {
            return source.hashCode() + target.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Transition) {
                var other = (Transition) obj;
                return other.source.equals(source) && other.target.equals(target);
            }
            return false;
        }

        public boolean isSuccessor(Node node) {
            if (node instanceof Activity) {
                return this.source.equals(((Activity) node).activity);
            } else if (node instanceof Transition) {
                return this.source.equals(((Transition) node).target);
            } else {
                return false;
            }
        }

        public String toString() {
            return this.source + " -> " + this.target;
        }
    }


}