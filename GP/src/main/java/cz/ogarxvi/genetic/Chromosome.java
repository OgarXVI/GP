package cz.ogarxvi.genetic;

import java.util.List;

/**
 * Třída pro uchování jedince (programu).
 * Každý chromozon má vypočtenou vlastní zdatnost a obsahuje kořen stromu.
 */
public class Chromosome implements Comparable<Chromosome>{
    /**
     * Kořen jedince, gen na vrcholu stromu
     */
    private Gen root;
    /**
     * Vypočtená fitness jedince, tedy hodnota programu
     */
    private Fitness fitness;
    /**
     * Vytvoří instanci Chromosomu
     * @param maxDepth Maximální hloubka jedince
     * @param listOfTerminals List možných terimnálů
     * @param listOfFunctions List možných funkcí
     */
    public Chromosome(int maxDepth, List<Gen> listOfTerminals, List<Gen> listOfFunctions) {
        //Nalezení možné funkce
        int randomNumber = (int) (Math.random() * (listOfFunctions.size()));
        // kořen nikdy nebude terminál, založení hlavy jedince
        root = new Function(listOfFunctions.get(randomNumber).getCommand(), listOfFunctions.get(randomNumber).getArita(), 0, maxDepth, listOfTerminals, listOfFunctions);
        // přiřazení fitness
        fitness = new Fitness();
    }
    /**
     * Kopírovací konstruktor
     * @param chromosome Chromosome na kopírování
     */
    public Chromosome(Chromosome chromosome) {	
        this.root = new Gen(chromosome.root);
        this.fitness = new Fitness(chromosome.fitness.getValue());
    }
    /**
     * Vrátí fitness
     * @return Fitness
     */
    public Fitness getFitness() {
        return fitness;
    }
    /**
     * Nastaví Fitness
     * @param fitness Fitness
     */
    public void setFitness(Fitness fitness) {
        this.fitness = fitness;
    }
    /**
     * Vrátí kořen chromosomu
     * @return Kořen
     */
    public Gen getRoot() {
        return root;
    }
    /**
     * Nastaví kořen 
     * @param root Funkce 
     */
    public void setRoot(Function root) {
        this.root = root;
    }
    /**
     * Nastaví kořen
     * @param koren Gen
     */
    public void setRoot(Gen koren) {
        this.root = koren;
    }
    /**
     * Compare by fitness value
     * @param o Another Chromosome
     * @return Compare value
     */
    @Override
    public int compareTo(Chromosome o) {
        
        if (this.getFitness().getValue().abs().compareTo(o.getFitness().getValue().abs()) > 0){
            return 1;
        }
        if (this.getFitness().getValue().abs().compareTo(o.getFitness().getValue().abs()) == 0){
            return 0;
        }
        return -1;
    }

    @Override
    public String toString() {
        return this.getRoot().print();
    }

    
}
