package br.raffathamires.antcolony;

import java.util.ArrayList;
import java.util.stream.Stream;

import br.raffathamires.graph.Edge;
import br.raffathamires.graph.Node;

public class Ant implements Runnable {

	private int id;
	private Node position;
	private Node startPosition;
	private ArrayList<Node> trail;
	private int totalTrailWeight;
	private AntColonyOptimization aco;

	public Ant(int id, Node startPosition, AntColonyOptimization aco) {
		this.id = id;
		this.position = startPosition;
		this.startPosition = startPosition;
		this.trail = new ArrayList<>();
		this.totalTrailWeight = 0;
		this.aco = aco;

		trail.add(startPosition);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Node getPosition() {
		return position;
	}

	public void setPosition(Node position) {
		this.position = position;
	}

	public ArrayList<Node> getTrail() {
		return trail;
	}

	public void setTrail(ArrayList<Node> trail) {
		this.trail = trail;
	}

	public int getTotalTrailWeight() {
		return totalTrailWeight;
	}

	public void setTotalTrailWeight(int totalTrailWeight) {
		this.totalTrailWeight = totalTrailWeight;
	}

	public void move() {
		ArrayList<Edge> edgesProbabilitie = new ArrayList<Edge>();
		StringBuilder sb = new StringBuilder();

		sb.append("@-o > Ant ");
		sb.append(getId());
		sb.append("\n");

		for (Edge e : position.getEdges()) {
			sb.append("Aresta ");
			sb.append(e.getNode1().getId());
			sb.append(" - ");
			sb.append(e.getNode2().getId());
			sb.append("\n");

			sb.append("Distância ");
			sb.append(e.getWeight());
			sb.append("\n");

			sb.append("Txy (Inverso distância) ");
			e.setTxy(aco.getTxy(e));
			sb.append(e.getTxy());
			sb.append("\n");

			sb.append("Nxy (Feromônio) ");
			e.setNxy(aco.getNxy());
			sb.append(e.getNxy());
			sb.append("\n");

			sb.append("Txy * Nxy (Inverso distância e Feromônio presente) ");
			e.setTnxy(aco.getTnxy(e));
			sb.append(e.getTnxy());
			sb.append("\n");

			sb.append("p(xy) ");
			e.setPxy(aco.getPxy(position, e));
			sb.append(e.getPxy());
			sb.append("\n");

			if (!trail.contains(e.getNode1()) || !trail.contains(e.getNode2()))
				edgesProbabilitie.add(e);

			sb.append("Probabilidade ");
			sb.append(aco.getPercent(e.getPxy()));
			sb.append("%\n");

			sb.append("\n");
		}
		Edge selected = null;
		
		if(edgesProbabilitie.isEmpty()) {
			Stream<Edge> se = position.getEdges().stream().filter(e -> e.getNode1() == startPosition || e.getNode2() == startPosition);
			totalTrailWeight += se.findFirst().get().getWeight();
			position = startPosition;
			return;
		}
		
		if(edgesProbabilitie.size() == 1) {
			selected = edgesProbabilitie.get(0);
		} else {
			selected = aco.rouletteWheel(edgesProbabilitie);
		}
		
		if (selected == null)
			System.err.println("Error in rouletteWheel in ant " + getId() + " at node " + position.getId());
		sb.append("Escolhido: Rota ");
		sb.append(selected.toString());
		sb.append("\n\n");
		
		totalTrailWeight += selected.getWeight();

		if (selected.getNode1() == position) {
			position = selected.getNode2();
		} else {
			position = selected.getNode1();
		}
		trail.add(position);

		//System.out.println(sb.toString());
	}

	@Override
	public void run() {
		while (totalTrailWeight == 0 || position != startPosition) {
			move();
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("@-o > Ant ");
		sb.append(getId());
		sb.append("\n");
		sb.append("Custo total da rota: ");
		sb.append(totalTrailWeight);
		sb.append("\n");
		
		sb.append("Rota: ");
		for(Node n : trail) {
			sb.append(n.getId());
			sb.append(" -> ");
		}
		sb.append(startPosition.getId());
		
		System.out.println(sb.toString());
	}

}
