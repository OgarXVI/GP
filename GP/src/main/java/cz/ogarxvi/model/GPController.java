/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.model;

import cz.ogarxvi.genetic.Chromosome;
import cz.ogarxvi.genetic.Fitness;
import cz.ogarxvi.genetic.Gen;
import cz.ogarxvi.genetic.GeneticAlgorithm;
import cz.ogarxvi.genetic.IterartionListener;
import cz.ogarxvi.genetic.Population;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 *
 * @author OgarXVI
 */
public class GPController {
    private Messenger m;
        
        public GPController(Messenger mm){
            m = mm;
        }
        
        
        public void run(){
                Population<Gen> population = createInitialPopulation(5);

		Fitness<Gen, Double> fitness = new Fitness<>();

		GeneticAlgorithm<Gen, Double> ga = new GeneticAlgorithm<Gen, Double>(population, fitness);

		addListener(ga);

		ga.evolve(500);
        
        }

	/**
	 * The simplest strategy for creating initial population <br/>
	 * in real life it could be more complex
	 */
	private Population<Gen> createInitialPopulation(int populationSize) {
		Population<Gen> population = new Population<Gen>();
		Gen base = new Gen();
		for (int i = 0; i < populationSize; i++) {
			// each member of initial population
			// is mutated clone of base chromosome
			Gen chr = base.mutate();
			population.addChromosome(chr);
		}
		return population;
	}

	/**
	 * After each iteration Genetic algorithm notifies listener
	 */
	private void addListener(GeneticAlgorithm<Gen, Double> ga) {
		// just for pretty print
		m.AddMesseage(String.format("%s\t%s\t%s", "iter", "fit", "chromosome"));
                m.GetMesseage();
		// Lets add listener, which prints best chromosome after each iteration
		ga.addIterationListener(new IterartionListener<Gen, Double>() {

			private final double threshold = 1e-5;

			@Override
			public void update(GeneticAlgorithm<Gen, Double> ga) {

				Gen best = ga.getBest();
				double bestFit = ga.fitness(best);
				int iteration = ga.getIteration();

				// Listener prints best achieved solution
				m.AddMesseage(String.format("%s\t%s\t%s", iteration, bestFit, best));
                                m.GetMesseage();
				// If fitness is satisfying - we can stop Genetic algorithm
				if (bestFit < this.threshold) {
					ga.terminate();
				}
			}
		});
	}       
}
