/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.model;

import cz.ogarxvi.model.genetic.GeneticAlgorithm;

/**
 * Vlákno udržující parametry pro výpočet GA
 * @author OgarXVI
 */
public class GPThread extends Thread {
    /**
     * Počet generací
     */
    private final int numberOfGeneration;
    /**
     * Velikost jedinců v populaci
     */
    private final int sizeOfInitPopulation;
    /**
     * Maximální počátečný hloubka stromu
     */
    private final int maxDepthTreeInit; 
    /**
     * Maximální hloubka stromu po křížení
     */
    private final int maxDepthTreeAfterCrossover;
    /**
     * Šance na křížení
     */
    private final double crossover;
    /**
     * Šance na reprodukci
     */
    private final double reproduction;
    /**
     * Šance na mutaci
     */
    private final double mutation;
    /**
     * Zachování nejzdatnějších jedinců v populaci
     */
    private final boolean elitism;
    /**
     * Zvýšení zadaných parametrů prozvýšení variablity a následné oříznutí
     */
    private final boolean decimation;
    /**
     * Založí vlákno s parametry pro výpočet GA
     * @param numberOfGeneration Počet generací
     * @param sizeOfInitPopulation Velikost generace
     * @param maxDepthTreeInit Max hloubka stromu při založení
     * @param maxDepthTreeAfterCrossover Max hloubka stromu po křížení stromu
     * @param crossover Šance na křížení
     * @param reproduction Šance na reprodukci
     * @param mutation Šance na mutaci
     * @param elitism Zachování nejlepších jedinců
     * @param decimation Rozšíření parametrů, následné snížení
     */
    public GPThread(int numberOfGeneration, 
            int sizeOfInitPopulation, int maxDepthTreeInit, int maxDepthTreeAfterCrossover, 
            double crossover, double reproduction, double mutation, boolean elitism,
            boolean decimation) {
        this.numberOfGeneration = numberOfGeneration;
        this.sizeOfInitPopulation = sizeOfInitPopulation;
        this.maxDepthTreeInit = maxDepthTreeInit;
        this.maxDepthTreeAfterCrossover = maxDepthTreeAfterCrossover;
        this.crossover = crossover;
        this.reproduction = reproduction;
        this.mutation = mutation;
        this.elitism = elitism;
        this.decimation = decimation;
    }

    @Override
    public void run() {
            GeneticAlgorithm ga = new GeneticAlgorithm();
            ga.runGP(numberOfGeneration, sizeOfInitPopulation, maxDepthTreeInit, maxDepthTreeAfterCrossover, reproduction, crossover, mutation, elitism, decimation, null, null);
            //Reconstruct
            FileHandler.getInstance().resolveGPReconstruct(DataHandler.getInstance().getBestChromosome().toString());
    } 
}
