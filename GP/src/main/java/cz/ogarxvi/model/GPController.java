/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.model;

import cz.ogarxvi.genetic.GeneticAlgorithm;
import javax.swing.JOptionPane;

/**
 * Vlákno udržující parametry pro výpočet GA
 * @author OgarXVI
 */
public class GPController extends Thread {
    /**
     * Odkaz na data
     */
    private final DataHandler dh;
    /**
     * Odkaz na zapisovař zpráv
     */
    private final Messenger m;
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
     * Optimalizace stromů, zjednodušeních jejich struktur
     */
    private final boolean editation;
    /**
     * Vybraná selekce mezi turnajem (0) a ruletou (1)
     */
    private final int selectionMethod;
    /**
     * Založí vlákno s parametry pro výpočet GA
     * @param m Záznamník
     * @param dh Data
     * @param numberOfGeneration Počet generací
     * @param sizeOfInitPopulation Velikost generace
     * @param maxDepthTreeInit Max hloubka stromu při založení
     * @param maxDepthTreeAfterCrossover Max hloubka stromu po křížení stromu
     * @param crossover Šance na křížení
     * @param reproduction Šance na reprodukci
     * @param mutation Šance na mutaci
     * @param elitism Zachování nejlepších jedinců
     * @param decimation Rozšíření parametrů, následné snížení
     * @param editation Optimalizace stromů
     * @param selectionMethod Výběrová metoda genů
     */
    public GPController(Messenger m, DataHandler dh, int numberOfGeneration, 
            int sizeOfInitPopulation, int maxDepthTreeInit, int maxDepthTreeAfterCrossover, 
            double crossover, double reproduction, double mutation, boolean elitism,
            boolean decimation, boolean editation, int selectionMethod) {
        this.m = m;
        this.dh = dh;
        this.numberOfGeneration = numberOfGeneration;
        this.sizeOfInitPopulation = sizeOfInitPopulation;
        this.maxDepthTreeInit = maxDepthTreeInit;
        this.maxDepthTreeAfterCrossover = maxDepthTreeAfterCrossover;
        this.crossover = crossover;
        this.reproduction = reproduction;
        this.mutation = mutation;
        this.elitism = elitism;
        this.decimation = decimation;
        this.editation = editation;
        this.selectionMethod = selectionMethod;
    }

    @Override
    public void run() {
            GeneticAlgorithm ga = new GeneticAlgorithm(this);
            ga.runGP(numberOfGeneration, sizeOfInitPopulation, maxDepthTreeInit, maxDepthTreeAfterCrossover, reproduction, crossover, mutation, elitism, decimation, editation, selectionMethod, null, null);
    }
    /***
     * Způsobí zastavení vlákna
     */
    public void myStop(){
        this.stop();
    }
    /**
     * Vrátí odkaz na DataHandler
     * @return DataHandler
     */
    public DataHandler getDh() {
        return dh;
    }
    /**
     * Vrátí odkaz na Messenger
     * @return Messenger
     */
    public Messenger getM() {
        return m;
    }
    
    
}
