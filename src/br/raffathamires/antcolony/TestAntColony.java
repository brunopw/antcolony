package br.raffathamires.antcolony;

public class TestAntColony {

	public static void main(String[] args) {

		AntColonyOptimization antColony = new AntColonyOptimization();
		
		antColony.start();
		//System.out.println(antColony.printGraph());

		// graph.shortestPath(1, 10).forEach(System.out::println);

		//System.out.println("\n_______________ Iteração 1: \n");
		//System.out.println(antColony.getProbabilities());
	}

}
