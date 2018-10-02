package cz.ogarxvi.genetic;

import java.util.List;

/**
 * Třída pro uchování jedince (programuú.
 * Každý chromozon má vypočtenou vlastní zdatnost a obsahuje kořen stromu.
 */
public class Chromosome {

    private Gen root;
    private Fitness fitness;

    public Chromosome(int maxDepth, List<Gen> setOfTerminals, List<Gen> setOfFunctions) {
        int randomNumber = (int) (Math.random() * (setOfFunctions.size()));
        root = new Function(setOfFunctions.get(randomNumber).getCommand(), setOfFunctions.get(randomNumber).getArita(), 0, maxDepth, setOfTerminals, setOfFunctions);
        fitness = new Fitness();
    }

    public Chromosome(Chromosome genotyp) {	
        this.root = new Gen(genotyp.root);
        this.fitness = new Fitness(genotyp.fitness.getValue());
    }

    public Fitness getFitness() {
        return fitness;
    }

    public void setFitness(Fitness fitness) {
        this.fitness = fitness;
    }
    /*
    public int nahodneKladneCislo(int horniHranice) {		
        return (int) (Math.random() * horniHranice);
    }
    */
    public Gen getRoot() {
        return root;
    }

    public void setRoot(Function root) {
        this.root = root;
    }

    public void setRoot(Gen koren) {
        this.root = koren;
    }

}
