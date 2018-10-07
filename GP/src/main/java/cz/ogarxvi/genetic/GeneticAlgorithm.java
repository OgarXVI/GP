package cz.ogarxvi.genetic;

import cz.ogarxvi.model.DataHandler;
import cz.ogarxvi.model.Messenger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.commons.collections4.list.TreeList;

public class GeneticAlgorithm {

    private DataHandler dataHandler;
    private Messenger messenger;
    private List<Chromosome> population;
    //private boolean stop = false;
    private Editation editation = new Editation();
    private double probabilityOfCrossoverFunctionsInNodes = 0.00;

    public GeneticAlgorithm(Messenger m, DataHandler dh) {
        messenger = m;
        dataHandler = dh;
    }

    public void runGP(int numberOfGenerations, int initSizeOfPopulation, int initTreeMaxDepth, int treeMaxDepthAfterOperation, double reproductionProbability, double crossoverProbability, double mutationProbability, double crossoverInFunctionNodes, boolean elitismus, boolean decimation, boolean editable, int numberOfSteps, int selectionMethod) {

        dataHandler.setGpStop(false); 
        probabilityOfCrossoverFunctionsInNodes = crossoverInFunctionNodes;

        List<Gen> setOfTerminals = new ArrayList<Gen>();
        List<Gen> setOfFunctions = new ArrayList<Gen>();

        
        if (dataHandler!=null){
            //TERMINALS
            setOfTerminals.addAll(dataHandler.getLoadedTerminals());           
            //FUNCTIONS
            setOfFunctions.addAll(dataHandler.getLoadedFunctions());
        }
        
        /*
        Gen a = new Terminal("X");
        Gen b = new Terminal("Y");
        Gen c = new Terminal("Z");
        Gen const1 = new Terminal("1");
        Gen const2 = new Terminal("2");
        Gen const5 = new Terminal("5");

        setOfTerminals.add(a);
        setOfTerminals.add(b);
        setOfTerminals.add(c);
        setOfTerminals.add(const1);
        setOfTerminals.add(const2);
        setOfTerminals.add(const5);

        Gen plus2 = new Gen("+", 2);
        Gen plus3 = new Gen("+", 3);
        Gen plus4 = new Gen("+", 4);
        Gen minus2 = new Gen("-", 2);
        Gen minus3 = new Gen("-", 3);
        Gen krat2 = new Gen("*", 2);
        Gen deleno2 = new Gen("/", 2);

        setOfFunctions.add(plus2);
        setOfFunctions.add(plus3);
        setOfFunctions.add(plus3);
        setOfFunctions.add(plus3);
        setOfFunctions.add(plus4);
        setOfFunctions.add(plus4);
        setOfFunctions.add(minus2);
        setOfFunctions.add(minus3);
        setOfFunctions.add(krat2);
        setOfFunctions.add(deleno2);
        */
        population = new ArrayList<Chromosome>(initSizeOfPopulation);

        int numberOfReproduction = (int) (reproductionProbability * initSizeOfPopulation);
        int numberOfCrossover = (int) (crossoverProbability * initSizeOfPopulation) / 2;		
        int numberOfMutation = (int) (mutationProbability * initSizeOfPopulation);

        while ((numberOfReproduction + (numberOfCrossover * 2) + numberOfMutation) < initSizeOfPopulation) {
            numberOfReproduction++;
        }

        if (elitismus) {
            numberOfReproduction = numberOfReproduction - 1;
        }

     
        int generationOfDecimation = 10;
        int numberOfProgramBeforeDecimation = 10 * initSizeOfPopulation;

        if (decimation) {
            population = new ArrayList<Chromosome>(numberOfProgramBeforeDecimation);

            numberOfReproduction = (int) (reproductionProbability * numberOfProgramBeforeDecimation);
            numberOfCrossover = (int) (crossoverProbability * numberOfProgramBeforeDecimation) / 2;		
            numberOfMutation = (int) (mutationProbability * numberOfProgramBeforeDecimation);

            while ((numberOfReproduction + (numberOfCrossover * 2) + numberOfMutation) < numberOfProgramBeforeDecimation) {
                numberOfReproduction++;
            }

            if (elitismus) {
                numberOfReproduction = numberOfReproduction - 1;
            }

            InitStartPopulate(setOfTerminals, setOfFunctions, initTreeMaxDepth, numberOfProgramBeforeDecimation);

        } else {
            InitStartPopulate(setOfTerminals, setOfFunctions, initTreeMaxDepth, initSizeOfPopulation);
        }

        Chromosome bestChromosome = new Chromosome(population.get(0));

        for (int i = 0; i < numberOfGenerations; i++) {	
            if (dataHandler.isGpStop()) {
                break;
            }

            Chromosome bestChromosomeInGeneration = new Chromosome(population.get(0));

            for (int j = 0; j < population.size(); j++) {	

                if (editable) {
                    population.get(j).setRoot(editation.editRoot(population.get(j).getRoot()));	
                }

                // vzít genotyp, rozparsovat ho na vzoreček, do kterýho jde dosadit čísla za proměnný a vypočítat ho,
                // pak dosadit čísla do proměnných z tabulky (pro všechny řádky v tabulce), určit pro každý ten řádek výsledek,
                // porovnat pro každý ten řádek výsledek se sloupcem F, a rozdíl bude hrubá zdatnost. Tu převedeme na standardizovanou
                // (tzn. čím víc se blíží nule, tím je to lepší), a tu zapíšeme jako zdatnost. 
                //messenger.AddMesseage("P: " + j + " " + populace.get(j).getKoren().vypis());
                //Generace: " + i + " Jedinec:" + j + "   " + 
                
                // TODO - anglicky:
                List<Double> vysledky = new ArrayList<>();
                Map<String, Double> values = new HashMap<>();
                for (int k = 0; k < dataHandler.getMathData().length; k++) {
                    for (int l = 0; l < dataHandler.getMathData()[k].length; l++) {
                        values.put(dataHandler.getParams()[l], dataHandler.getMathData()[k][l]);
                    }
                }
                for (int m = 0; m < dataHandler.getLoadedTerminals().size(); m++) {
                    if (! Character.isLetter(dataHandler.getLoadedTerminals().get(m).command.charAt(0))) {
                        values.put(dataHandler.getLoadedTerminals().get(m).command, Double.valueOf(dataHandler.getLoadedTerminals().get(m).command));
                    }
                }
                
                for (int k = 0; k < dataHandler.getMathData().length; k++) {
                    vysledky.add(population.get(j).getRoot().provedSeBezZobrazeni(values));
                }
                
                
                population.get(j).getFitness().calculate(vysledky, dataHandler.getExpectedResults());
                
       
                
                /*
                String pomFormula = messenger.preToInfix(population.get(j).getRoot().print());
                System.out.println(population.get(j).getRoot().print());
                double originFitness = population.get(j).getFitness().getValue(); 
                population.get(j).getFitness().calculate(pomFormula, dataHandler.getParams(), dataHandler.getMathData(), dataHandler.getExpectedResults());
                */

                //messenger.AddMesseage("G: " + i + " J: " + j + "  " + populace.get(j).getKoren().vypis());
                // messenger.AddMesseage("Jeho úprava je:   " + messenger.preToInfix(populace.get(j).getKoren().vypis()));
                
                if (Math.abs(population.get(j).getFitness().getValue()) < Math.abs(bestChromosome.getFitness().getValue())) {
                    bestChromosome = new Chromosome(population.get(j));
                }
                
                if (Math.abs(population.get(j).getFitness().getValue()) < Math.abs(bestChromosomeInGeneration.getFitness().getValue())) {
                    bestChromosomeInGeneration = new Chromosome(population.get(j));
                }
            }
            messenger.AddMesseage(bestChromosomeInGeneration.getFitness().getValue() + "  " + bestChromosomeInGeneration.getRoot().print() );
            messenger.GetMesseage();
            //TODO: BETTER END
            // ukončovací podmínka - když je zdatnost nejlepšího jedince 0, ukončíme algoritmus
            if (bestChromosomeInGeneration.getFitness().getValue() == 0.00) {
                dataHandler.setGpStop(true);
                messenger.AddMesseage("The Best:  " +  bestChromosomeInGeneration.getRoot().print());
                messenger.GetMesseage();
                return;
            }
            
            

            if (decimation && (i == (generationOfDecimation - 1))) {				

                List<Chromosome> populationAfterDecimation = new ArrayList<Chromosome>(initSizeOfPopulation);
                for (int k = 0; k < initSizeOfPopulation; k++) {
                    Chromosome selectedChromosome = tournamentSelection3();
                    populationAfterDecimation.add(new Chromosome(selectedChromosome));
                    population.remove(selectedChromosome);
                }

                numberOfReproduction = (int) (reproductionProbability * initSizeOfPopulation);
                numberOfCrossover = (int) (crossoverProbability * initSizeOfPopulation) / 2;		
                numberOfMutation = (int) (mutationProbability * initSizeOfPopulation);

                while ((numberOfReproduction + (numberOfCrossover * 2) + numberOfMutation) < initSizeOfPopulation) {
                    numberOfReproduction++;
                }

                if (elitismus) {
                    numberOfReproduction = numberOfReproduction - 1;
                }

                population = populationAfterDecimation;

            }

            List<Chromosome> newPopulation = new ArrayList<Chromosome>();

            if (elitismus) {
                Chromosome bestChromosome2 = new Chromosome(bestChromosome);
                newPopulation.add(bestChromosome2);
            }

            // reproduction
            for (int j = 0; j < numberOfReproduction; j++) {

                Chromosome selectedChromosome = chooseSelectionMethod(selectionMethod);
                newPopulation.add(selectedChromosome);
            }

            // crossover
            for (int j = 0; j < numberOfCrossover; j++) {

                Chromosome selectedChromosome1 = chooseSelectionMethod(selectionMethod);
                Chromosome selectedChromosome2 = chooseSelectionMethod(selectionMethod);

                SelectedGen selectedGen1 = randomGen(selectedChromosome1);
                SelectedGen selectedGen2 = randomGen(selectedChromosome2);

                selectedGen1.getGenAbove().setDepth(0);
                selectedGen2.getGenAbove().setDepth(0);

                selectedGen1.getGenAbove().fixDepth();
                selectedGen2.getGenAbove().fixDepth();

                selectedGen1.getGenAbove().setMaxDepth(selectedGen1.getGenAbove());
                int maxDepthOfGen1 = Gen.maxDepth;

                selectedGen2.getGenAbove().setMaxDepth(selectedGen2.getGenAbove());
                int maxDepthOfGen2 = Gen.maxDepth;

                if ((maxDepthOfGen1 + maxDepthOfGen2) <= treeMaxDepthAfterOperation) {

                    Gen gen1 = selectedGen1.getGenAbove().gens.get(selectedGen1.genIndex());
                    Gen gen2 = selectedGen2.getGenAbove().gens.get(selectedGen2.genIndex());

                    selectedGen1.getGenAbove().gens.set(selectedGen1.genIndex(), gen2);
                    selectedGen2.getGenAbove().gens.set(selectedGen2.genIndex(), gen1);

                    selectedChromosome1.getRoot().fixDepth();
                    selectedChromosome2.getRoot().fixDepth();

                    newPopulation.add(selectedChromosome1);
                    newPopulation.add(selectedChromosome2);

                } else {
                    j--;
                }
            }

            // mutation:
            for (int j = 0; j < numberOfMutation; j++) {

                Chromosome selectedChromosome = tournamentSelection3();

                SelectedGen selectedGen = randomGen(selectedChromosome);

                Gen gen = selectedGen.getGenAbove().gens.get(selectedGen.genIndex());

                if ((gen.depth - 1) == treeMaxDepthAfterOperation) {	
                    gen = setOfTerminals.get(getRandomNumber(3));
                } else {
                    Gen f = setOfFunctions.get(getRandomNumber(3));
                    gen = new Function(f.command, f.arita, gen.depth, treeMaxDepthAfterOperation, setOfTerminals, setOfFunctions);
                }

                selectedGen.getGenAbove().gens.set(selectedGen.genIndex(), gen);
                selectedChromosome.getRoot().fixDepth();

                newPopulation.add(selectedChromosome);
            }

            
            
            population = newPopulation;			
        }
        messenger.GetAllMesseages();
    }

    private SelectedGen randomGen(Chromosome chromosome) {

        Gen gen = chromosome.getRoot();	
        gen.setMaxDepth(gen);
        double p = 1.0 / Gen.maxDepth;

        
        if (Gen.maxDepth == 1) {
            SelectedGen vg = new SelectedGen();
            vg.setGenAbove(gen);
            vg.setIndex(getRandomNumber(gen.gens.size()));
            return vg;
        }

        boolean isFunction = false;
        if (getRandomDouble(1) < probabilityOfCrossoverFunctionsInNodes) {
            isFunction = true;
        }

        while (p <= 1) {
            if (getRandomDouble(1) < p) {							
                if (!isFunction) {												
                    List<Integer> indexs = new ArrayList<Integer>(0);
                    for (int i = 0; i < gen.gens.size(); i++) {
                        if (!gen.gens.get(i).isFunction()) {
                            indexs.add(i);
                        }
                    }
                    if (indexs.size() != 0) {
                        int i = indexs.get(getRandomNumber(indexs.size()));
                        SelectedGen vg = new SelectedGen();
                        vg.setGenAbove(gen);
                        vg.setIndex(i);
                        return vg;
                    }
                    p = p + p;

                    gen = gen.gens.get(getRandomNumber(gen.gens.size()));
                } else {														
                    List<Integer> indexs = new ArrayList<Integer>(0);
                    for (int i = 0; i < gen.gens.size(); i++) {
                        if (gen.gens.get(i).isFunction()) {
                            indexs.add(i);
                        }
                    }
                    if (indexs.size() != 0) {
                        int i = indexs.get(getRandomNumber(indexs.size()));
                        SelectedGen vg = new SelectedGen();
                        vg.setGenAbove(gen);
                        vg.setIndex(i);
                        return vg;
                    }

                    gen = chromosome.getRoot();
                    break;
                }
            }
        }

        SelectedGen vg = new SelectedGen();
        vg.setGenAbove(gen);
        vg.setIndex(getRandomNumber(gen.gens.size()));
        return vg;

    }
    
    private Chromosome chooseSelectionMethod(int selectionMethod){
        switch(selectionMethod){
            case 0: // tournament
                return tournamentSelection3();
            case 1: //roulette
                return rouletteSelection();
        }
        return null;
    }

    private Chromosome rouletteSelection(){
        double sumFitness = 0;
        for (int i = 0; i < population.size(); i++) {
            sumFitness = population.get(i).getFitness().getValue();
        }
        Random r = new Random();
        double randomNumber = (double) ((r.nextDouble()% 1000 / 9999.0f) * sumFitness);
        
        int memberIndex = 0;
        double partialSum = 0d;
        while (randomNumber > partialSum){
            partialSum+=population.get(memberIndex).getFitness().getValue();
            memberIndex++;
        }
        return population.get(memberIndex);
    }

    private Chromosome tournamentSelection3() {
        int pocet = 3;
        int[] poleIndexu = new int[pocet];

        for (int i = 0; i < pocet; i++) {
            poleIndexu[i] = getRandomNumber(population.size());

            for (int j = 0; j < i; j++) {
                if (poleIndexu[j] == poleIndexu[i]) {
                    poleIndexu[i] = getRandomNumber(population.size());
                    j = 0;
                }
            }
        }

        Chromosome vitez = population.get(poleIndexu[0]);

        for (int i = 0; i < pocet; i++) {
            if (Math.abs(vitez.getFitness().getValue()) > Math.abs(population.get(poleIndexu[i]).getFitness().getValue())) {
                vitez = population.get(poleIndexu[i]);
            }
        }

        return new Chromosome(vitez);
    }


    private void InitStartPopulate(List<Gen> mnozinaTerminalu, List<Gen> mnozinaFunkci, int maximalniHloubka, int velikostPopulace) {

        for (int i = 0; i < velikostPopulace; i++) {
            Chromosome genotyp = new Chromosome(maximalniHloubka, mnozinaTerminalu, mnozinaFunkci);
            population.add(genotyp);
        }

    }

    public int getRandomNumber(int horniHranice) {	
        return (int) (Math.random() * horniHranice);
    }

    public double getRandomDouble(int horniHranice) {
        return Math.random() * horniHranice;
    }

    public static void quicksort(List<Chromosome> populace, int left, int right) {
        if (left < right) {
            int boundary = left;
            for (int i = left + 1; i < right; i++) {
                if (populace.get(i).getFitness().getValue() > populace.get(left).getFitness().getValue()) {
                    swap(populace, i, ++boundary);
                }
            }
            swap(populace, left, boundary);
            quicksort(populace, left, boundary);
            quicksort(populace, boundary + 1, right);
        }
    }

    private static void swap(List<Chromosome> populace, int left, int right) {
        Chromosome tmp = new Chromosome(populace.get(right));
        populace.set(right, populace.get(left));
        populace.set(left, tmp);
    }

}
