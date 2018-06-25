package br.raffathamires.antcolony;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Random;

import br.raffathamires.graph.Edge;
import br.raffathamires.graph.Graph;
import br.raffathamires.graph.Node;

public class AntColonyOptimization {

	private Graph graph;
	private ArrayList<Ant> ants;

	// Parameters
	// private double c = 1.0;
	private double alpha = 1;
	private double beta = 1;
	private double pheromoneEvaporation = 0.01;
	private double initialPheromone = 0.1;
	private double Q = 10;

	private int numOfNodes = 5;
	private int numOfAnts = 10;

	private int iterations = 0;

	// private double antFactor = 0.8;
	// private double randomFactor = 0.01;

	public AntColonyOptimization() {
		createGraph();
		createAnts();
	}
	
	public void start() {
		//atualizar os feromonios
		//faz um for usando as iterations
		//quando a ultima ant voltar, ai imprime a iteração e começa uma nova até o max de iterations
		startAnts();
	}

	private void createGraph() {
		graph = new Graph();

		for (int i = 1; i <= numOfNodes; i++) {
			graph.addNode(i);
		}

		// Generate all edges
		graph.addEdge(1, 2, 22);
		graph.addEdge(1, 3, 50);
		graph.addEdge(1, 4, 48);
		graph.addEdge(1, 5, 29);

		graph.addEdge(2, 3, 30);
		graph.addEdge(2, 4, 34);
		graph.addEdge(2, 5, 32);

		graph.addEdge(3, 4, 22);
		graph.addEdge(3, 5, 23);

		graph.addEdge(4, 5, 35);
	}

	private void createAnts() {
		ants = new ArrayList<Ant>();
		for (int i = 1; i <= numOfAnts; i++) {
			Ant ant = new Ant(i, graph.getStartNode(), this);
			ants.add(ant);
		}
	}

	private void startAnts() {
		for (Ant ant : ants) {
			ant.run();
		}
	}

	public String printGraph() {
		return graph.toString();
	}

	// Distancia
	public BigDecimal getTxy(Edge e) {
		BigDecimal txy = new BigDecimal("1.0");
		BigDecimal distance = new BigDecimal(e.getWeight());
		return txy.divide(distance, 3, RoundingMode.HALF_UP);
	}

	// Feromonio
	public BigDecimal getNxy() {
		BigDecimal nxy = new BigDecimal("0.1");
		BigDecimal feromonio = new BigDecimal(1);
		return nxy.divide(feromonio, 1, RoundingMode.HALF_UP);
	}

	// Feromonio * Distancia (Inverso distância e Feromônio presente)
	public BigDecimal getTnxy(Edge e) {
		return getTxy(e).multiply(getNxy()).setScale(3, RoundingMode.HALF_UP);
	}

	// Probabilidada da formiga de escolher a aresta =
	// (Feromonio * Distancia) / (Somatório do "Feromonio * Distancia" de todas
	// arestas que partem da atual)
	public BigDecimal getPxy(Node n, Edge e) {
		BigDecimal allTnxy = new BigDecimal("0").setScale(3);
		for (Edge edg : n.getEdges()) {
			BigDecimal bd = getTnxy(edg);
			allTnxy = allTnxy.add(bd);
		}
		return getTnxy(e).divide(allTnxy, 3, RoundingMode.HALF_UP);
	}

	// Percentual da probabilidade
	public BigDecimal getPercent(BigDecimal bg) {
		return bg.multiply(new BigDecimal("100")).setScale(1, RoundingMode.HALF_UP);
	}

	public Edge rouletteWheel(ArrayList<Edge> values) {
		ArrayList<Integer> normalizedValues = new ArrayList<Integer>();
		BigDecimal sum = new BigDecimal(values.stream().mapToDouble(e -> e.getPxy().doubleValue()).sum()).setScale(3, RoundingMode.HALF_UP);
		for(Edge e : values) {
			normalizedValues.add(e.getPxy().divide(sum, 3, RoundingMode.HALF_UP).multiply(new BigDecimal("1000")).setScale(1, RoundingMode.HALF_UP).intValue());
		}
		if(normalizedValues.stream().mapToInt(v -> v).sum() < 1000) {
			int val = normalizedValues.get(normalizedValues.size()-1) + 1;
			normalizedValues.set(normalizedValues.size()-1, val);
		}
		
		Random r = new Random();
		int randomNumber = r.nextInt(1000);
		int valueSum = 0;
		
		for (int i = 0; i < normalizedValues.size(); i++) {
			Edge e = values.get(i);
			valueSum += normalizedValues.get(i);
			if (randomNumber <= valueSum)
				return e;
		}

		return null;
	}

	public String getProbabilities() {
		StringBuilder sb = new StringBuilder();

		for (Node n : graph.getAdjacencyList()) {
			ArrayList<Edge> edgesProbabilitie = new ArrayList<Edge>();

			for (Edge e : n.getEdges()) {
				sb.append("Rota ");
				sb.append(e.getNode1().getId());
				sb.append(" - ");
				sb.append(e.getNode2().getId());
				sb.append("\n");

				sb.append("Distância ");
				sb.append(e.getWeight());
				sb.append("\n");

				sb.append("Txy (Inverso distância) ");
				e.setTxy(getTxy(e));
				sb.append(e.getTxy());
				sb.append("\n");

				sb.append("Nxy (Feromônio) ");
				e.setNxy(getNxy());
				sb.append(e.getNxy());
				sb.append("\n");

				sb.append("Txy * Nxy (Inverso distância e Feromônio presente) ");
				e.setTnxy(getTnxy(e));
				sb.append(e.getTnxy());
				sb.append("\n");

				sb.append("p(xy) ");
				e.setPxy(getPxy(n, e));
				sb.append(e.getPxy());
				sb.append("\n");

				edgesProbabilitie.add(e);

				sb.append("Probabilidade ");
				sb.append(getPercent(e.getPxy()));
				sb.append("%\n");

				sb.append("\n");
			}

			Edge selected = rouletteWheel(edgesProbabilitie);
			if (selected == null)
				System.err.println("Error in rouletteWheel to node");
			sb.append("Escolhido: Rota ");
			sb.append(selected.toString());
			sb.append("\n\n");
		}

		return sb.toString();
	}

}
