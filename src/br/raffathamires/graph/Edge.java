package br.raffathamires.graph;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Edge {

	private Node node1, node2;
	private int weight;
	private BigDecimal txy, nxy, tnxy, pxy;

	public Edge(Node node1, Node node2, int weight) {
		this.node1 = node1;
		this.node2 = node2;
		this.weight = weight;
		this.txy = new BigDecimal("0").setScale(3, RoundingMode.HALF_UP);
		this.nxy = new BigDecimal("0").setScale(1, RoundingMode.HALF_UP);
		this.tnxy = new BigDecimal("0").setScale(3, RoundingMode.HALF_UP);
		this.pxy = new BigDecimal("0").setScale(3, RoundingMode.HALF_UP);
	}

	public Node getNode1() {
		return node1;
	}

	public void setNode1(Node node1) {
		this.node1 = node1;
	}

	public Node getNode2() {
		return node2;
	}

	public void setNode2(Node node2) {
		this.node2 = node2;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public BigDecimal getTxy() {
		return txy;
	}

	public void setTxy(BigDecimal txy) {
		this.txy = txy;
	}

	public BigDecimal getNxy() {
		return nxy;
	}

	public void setNxy(BigDecimal nxy) {
		this.nxy = nxy;
	}

	public BigDecimal getTnxy() {
		return tnxy;
	}

	public void setTnxy(BigDecimal tnxy) {
		this.tnxy = tnxy;
	}

	public BigDecimal getPxy() {
		return pxy;
	}

	public void setPxy(BigDecimal pxy) {
		this.pxy = pxy;
	}
	
	public boolean isBetween(Node node1, Node node2) {
		return (this.node1 == node1 && this.node2 == node2);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(node1.getId());
		sb.append(" - ");
		sb.append(node2.getId());
		sb.append("  Weight: ");
		sb.append(weight);

		return sb.toString();
	}

}
