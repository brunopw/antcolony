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
	private double alpha = 1;
	private double beta = 1;
	private double pheromoneEvaporation = 0.01; //sigma
	private BigDecimal initialPheromone = new BigDecimal("0.1"); //feromônio inicial
	private BigDecimal Q = new BigDecimal("10"); //constante de atualização do feromônio

	private int numOfNodes = 5;
	private int numOfAnts = 5;

	private int iterations = 6;
	private boolean iterationFinished = false;
	private int returnedAntsInIteration = 0;

	// private double antFactor = 0.8;
	// private double randomFactor = 0.01;

	public AntColonyOptimization() {
		createGraph();
		createAnts();
	}
	
	public void start() {
		try {
			startAnts();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
		
		// Aplica o pheromonio inicial em todas arestas
		graph.getAdjacencyList().forEach(n -> n.getEdges().forEach(e -> e.setPheromone(initialPheromone)));
	}

	private void createAnts() {
		//Random r = new Random();
		//int randomNumber = r.nextInt(1000);
		//[r.nextInt(numOfNodes)]
		ants = new ArrayList<Ant>();
		for (int i = 1; i <= numOfAnts; i++) {
			Ant ant = new Ant(i, (Node)graph.getAdjacencyList().toArray()[i-1], this);
			ants.add(ant);
		}
	}

	private void startAnts() throws InterruptedException {
		for(int i = 1; i <= iterations; i++) {
			//System.out.println("___________ITERAÇÃO " + i + "_____________");
			for (Ant ant : ants) {
				ant.run();
			}
			while(!iterationFinished) {
				Thread.sleep(10);
			}
			//System.out.println("__________________________________\n\n");
			iterationFinished = false;
			
			getProbabilities();
			
			//atualizar os feromonios
			updatePheromone();
			
			for (Ant ant : ants) {
				ant.resetAnt();
			}
		}
	}
	
	public void antReturned() {
		returnedAntsInIteration++;
		if(returnedAntsInIteration == numOfAnts) {
			iterationFinished = true;
			returnedAntsInIteration = 0;
		}
	}

	public String printGraph() {
		return graph.toString();
	}
	
	public void updatePheromone() {
	
		for(Edge e : graph.getEdges()) {
			BigDecimal oldPheromoneWithEvaporation = new BigDecimal(1-pheromoneEvaporation).multiply(e.getNxy()).setScale(3, RoundingMode.HALF_UP);
			BigDecimal newPheromone = new BigDecimal("0");
			for(Ant a : ants) {
				if(a.haveEdge(e)) {
					newPheromone = newPheromone.add(Q.divide(new BigDecimal(a.getTotalTrailWeight()), 3, RoundingMode.HALF_UP));
				}
			}
			
			e.setPheromone(oldPheromoneWithEvaporation.add(newPheromone));
			//System.out.println(e.toString());
			//System.out.println(e.getPheromone());
			//System.out.println("\n");
		}
		
		//System.out.println("Iteration compleate");
		//pega as rotas(edges)
		//calcula (1-pheromoneEvaporation) x Txy (0.1)
		//                0.99             x 0.1 = 0,099
		// fereomonio liberado pelas formigas que passaram em cada rota
		// ROTA 1-2 teve formigas F1, F4 e F5
		// feromonio solto = Q / dF1 = 10 / 130
		//  F1 soltou 0,077 na ROTA A-B
		// TOTAL DE FEROMONIOS + feromonio presente após a evaporação (0.99)
	}

	// Distancia
	public BigDecimal getTxy(Edge e) {
		BigDecimal txy = new BigDecimal("1.0");
		BigDecimal distance = new BigDecimal(e.getWeight());
		return txy.divide(distance, 3, RoundingMode.HALF_UP);
	}

	// Feromonio
	public BigDecimal getNxy(Edge e) {
		//BigDecimal nxy = new BigDecimal("0.1");
		//BigDecimal feromonio = e.getPheromone();
		//return nxy.divide(feromonio, 1, RoundingMode.HALF_UP);
		return e.getPheromone();
	}

	// Feromonio * Distancia (Inverso distância e Feromônio presente)
	public BigDecimal getTnxy(Edge e) {
		return getTxy(e).multiply(getNxy(e)).setScale(3, RoundingMode.HALF_UP);
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
		BigDecimal sumPxy = new BigDecimal(values.stream().mapToDouble(e -> e.getPxy().doubleValue()).sum()).setScale(3, RoundingMode.HALF_UP);
		
		for(Edge e : values) {
			normalizedValues.add(e.getPxy().divide(sumPxy, 3, RoundingMode.HALF_UP).multiply(new BigDecimal("1000")).setScale(1, RoundingMode.HALF_UP).intValue());
		}
		
		int sum = sumPxy.multiply(new BigDecimal("1000")).intValue();
		
		Random r = new Random();
		int randomNumber = r.nextInt(sum);
		
		int posicaoEscolhida = -1;
		do {
			posicaoEscolhida++;
			sum -= normalizedValues.get(posicaoEscolhida);
		} while (sum > 0);

		return values.get(posicaoEscolhida);
	}

	public String getProbabilities() {
		System.out.println("Rotas | Distância | t(xy) | n(xy) | t(xy) * n(xy) | P(xy) | P(xy)%");
		
		StringBuilder sb = new StringBuilder();

		for (Node n : graph.getAdjacencyList()) {
			ArrayList<Edge> edgesProbabilitie = new ArrayList<Edge>();

			for (Edge e : n.getEdges()) {
				System.out.print(e.toString(n) + "      ");
				System.out.print(e.getWeight() + "       ");
				e.setTxy(getTxy(e));
				System.out.print(e.getTxy() + "   ");
				e.setNxy(getNxy(e));
				System.out.print(e.getNxy() + "       ");
				e.setTnxy(getTnxy(e));
				System.out.print(e.getTnxy() + "       ");
				e.setPxy(getPxy(n, e));
				edgesProbabilitie.add(e);
				System.out.print(e.getPxy() + "   ");
				System.out.println(getPercent(e.getPxy())+"%");
				
			}
			
			/*for (Edge e : n.getEdges()) {
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
				e.setNxy(getNxy(e));
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
			}*/

			Edge selected = rouletteWheel(edgesProbabilitie);
			if (selected == null)
				System.err.println("Error in rouletteWheel to node");
			sb.append("Escolhido: Rota ");
			sb.append(selected.toString());
			sb.append("\n\n");
		}
		System.out.println("\n");
		return sb.toString();
	}

}
