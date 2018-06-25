package br.raffathamires.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Node {

	private int id;
	private List<Edge> edges;
	//private Node parent;
	//private boolean isVisited;

	public Node(int id) {
		this.id = id;
		this.edges = new ArrayList<Edge>();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<Edge> getEdges() {
		return edges;
	}

	public boolean addEdge(Node node, int weight) {
		if (hasEdge(node)) {
			return false;
		}
		return edges.add(new Edge(this, node, weight));
	}

	public boolean removeEdgeFromNode(Node node) {
		Optional<Edge> optional = findEdge(node);
		if (optional.isPresent()) {
			return edges.remove(optional.get());
		}
		return false;
	}

	public boolean hasEdge(Node node) {
		return findEdge(node).isPresent();
	}

	private Optional<Edge> findEdge(Node node) {
		return edges.stream().filter(edge -> edge.isBetween(this, node)).findFirst();
	}

	public void removeAllEdges() {
		this.edges = new ArrayList<Edge>();
	}

	public int getEdgeCount() {
		return edges.size();
	}
/*
	public Node parent() {
		return parent;
	}

	public boolean isVisited() {
		return isVisited;
	}

	public void setVisited(boolean isVisited) {
		this.isVisited = isVisited;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}
*/
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("Node id: ");
		sb.append(id);
		sb.append("\n");
		for (Edge e : edges) {
			sb.append("   ");
			sb.append(e.toString());
			sb.append("\n");
		}

		return sb.toString();
	}

}
