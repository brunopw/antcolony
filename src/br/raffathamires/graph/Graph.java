package br.raffathamires.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Graph {
	// undirectedGraph
	private int nodes;
	//private int edges;
	private Map<Integer, Node> adjacencyList;
	private ArrayList<Edge> edges;

	public Graph() {
		nodes = 0;
		//edges = 0;
		adjacencyList = new HashMap<>();
		edges = new ArrayList<Edge>();
	}
	
	public Node getStartNode() {
		return getAdjacencyList().iterator().next();
	}
	
	public Collection<Node> getAdjacencyList() {
		return adjacencyList.values();
	}

	public boolean addNode(int id) {
		if (adjacencyList.containsKey(id)) {
			return false;
		}

		if (adjacencyList.put(id, new Node(id)) == null) {
			nodes++;
			return true;
		}
		return false;
	}

	public boolean removeNode(int id) {
		if (!adjacencyList.containsKey(id)) {
			return false;
		}

		// remove all incoming edges to node
		adjacencyList.values().forEach(n -> n.removeEdgeFromNode(getNode(id)));

		// remove the node
		if (adjacencyList.remove(id) != null) {
			nodes--;
			return true;
		}
		return false;
	}

	public boolean addEdge(int id1, int id2, int weight) {
		if (!adjacencyList.containsKey(id1) || !adjacencyList.containsKey(id2)) {
			throw new RuntimeException("Node doesn't exist! Edge: (" + id1 + " - " + id2 + ")");
		}
		
		// add the edge
		Node node1 = getNode(id1);
		Node node2 = getNode(id2);

		Edge newEdge = new Edge(node1, node2, weight);
		
		if (node1.addEdge(newEdge) && node2.addEdge(newEdge)) {
			//edges++;
			return edges.add(newEdge);
		}

		return false;
	}

	public boolean removeEdge(int id1, int id2) {
		if (!adjacencyList.containsKey(id1) || !adjacencyList.containsKey(id2)) {
			return false;
		}

		Node node1 = getNode(id1);
		Node node2 = getNode(id2);

		if (node1.removeEdgeFromNode(node2) && node2.removeEdgeFromNode(node1)) {
			//edges--;
			for(Edge e : edges) {
				if(e.isBetween(node1, node2)) {
					edges.remove(e);
					break;
				}
			}
			return true;
		}
		return false;
	}

	private Node getNode(int id) {
		return adjacencyList.get(id);
	}

	public int nodeCount() {
		return nodes;
	}

	/*public int edgeCount() {
		return edges;
	}
*/
	public ArrayList<Edge> getEdges() {
		return edges;
	}

	public void setEdges(ArrayList<Edge> edges) {
		this.edges = edges;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();

		for (Node n : adjacencyList.values())
			sb.append(n.toString());
		
		sb.append("\n--Nodes: ");
		sb.append(nodeCount());
		
		sb.append("\n--Edges: ");
		//sb.append(edgeCount());
		sb.append(edges.size());
		
		return sb.toString();
	}
	
	/*
		private void resetGraph() {
			adjacencyList.keySet().forEach(key -> {
				Node node = getNode(key);
				node.setParent(null);
				node.setVisited(false);
			});
		}
	*/
	/*
	 * //https://codereview.stackexchange.com/questions/67970/graph-implementation-
	 * in-java-8
	 * 
	 * public List<Integer> shortestPath(int startNode, int endNode) { // if nodes
	 * not found, return empty path if (!adjacencyList.containsKey(startNode) ||
	 * !adjacencyList.containsKey(endNode)) { return null; } // run bfs on the graph
	 * runBFS(startNode);
	 * 
	 * List<Integer> path = new ArrayList<Integer>(); // trace path back from end
	 * vertex to start Node end = getNode(endNode); while (end != null && end !=
	 * getNode(startNode)) { path.add(end.getId()); end = end.parent(); } // if end
	 * is null, node not found if (end == null) { return null; } else {
	 * Collections.reverse(path); } return path; }
	 * 
	 * private void runBFS(int startNode) { if
	 * (!adjacencyList.containsKey(startNode)) { throw new
	 * RuntimeException("Node doesn't exist."); }
	 * 
	 * // reset the graph resetGraph();
	 * 
	 * // init the queue Queue<Node> queue = new LinkedList<>(); Node start =
	 * getNode(startNode); queue.add(start);
	 * 
	 * // explore the graph while (!queue.isEmpty()) { Node first = queue.remove();
	 * first.setVisited(true); first.getEdges().forEach(edge -> { Node neighbour =
	 * edge.getNode2(); if (!neighbour.isVisited() && !queue.contains(neighbour)) {
	 * neighbour.setParent(first); queue.add(neighbour); } }); } }
	 */
}
