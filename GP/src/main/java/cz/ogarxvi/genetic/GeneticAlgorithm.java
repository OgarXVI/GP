package cz.ogarxvi.genetic;

import cz.ogarxvi.model.DataHandler;
import cz.ogarxvi.model.Messenger;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GeneticAlgorithm {

    private DataHandler dataHandler;
    private Messenger messenger;
    private List<Chromosome> population;
    private Editation editation = new Editation();
    private double probabilityOfCrossoverFunctionsInNodes;
    private double lastBestChromosomeInGenerationFitness;
    private int lastIteration;

    public GeneticAlgorithm(Messenger m, DataHandler dh) {
        messenger = m;
        dataHandler = dh;
    }

    public void runGP(int numberOfGenerations, int initSizeOfPopulation, int initTreeMaxDepth, int treeMaxDepthAfterOperation, double reproductionProbability, double crossoverProbability, double mutationProbability, double crossoverInFunctionNodes, boolean elitismus, boolean decimation, boolean editable, int numberOfSteps, int selectionMethod) {

        dataHandler.setGpStop(false);
        probabilityOfCrossoverFunctionsInNodes = crossoverInFunctionNodes;

        List<Gen> setOfTerminals = new ArrayList<Gen>();
        List<Gen> setOfFunctions = new ArrayList<Gen>();

        if (dataHandler != null) {
            //TERMINALS
            setOfTerminals.addAll(dataHandler.getLoadedTerminals());
            //FUNCTIONS
            setOfFunctions.addAll(dataHandler.getLoadedFunctions());
        }

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
            population = new ArrayList<>(numberOfProgramBeforeDecimation);

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

                List<BigDecimal> results = new ArrayList<>();
                Map<String, BigDecimal> values = new HashMap<>();
                for (int k = 0; k < dataHandler.getMathData().length; k++) {
                    for (int l = 0; l < dataHandler.getMathData()[k].length; l++) {
                        values.put(dataHandler.getParams()[l], dataHandler.getMathData()[k][l]);
                    }
                }
                for (int m = 0; m < dataHandler.getLoadedTerminals().size(); m++) {
                    if (!Character.isLetter(dataHandler.getLoadedTerminals().get(m).command.charAt(0))) {
                        values.put(dataHandler.getLoadedTerminals().get(m).command, BigDecimal.valueOf(Double.valueOf(dataHandler.getLoadedTerminals().get(m).command)));
                    }
                }

                for (int k = 0; k < dataHandler.getMathData().length; k++) {
                    results.add(population.get(j).getRoot().resolveCommand(values));
                }

                population.get(j).getFitness().calculate(results, dataHandler.getExpectedResults());

                if (population.get(j).getFitness().getValue().abs().compareTo(bestChromosome.getFitness().getValue().abs()) < 0) {
                    bestChromosome = new Chromosome(population.get(j));
                }

                if (population.get(j).getFitness().getValue().abs().compareTo(bestChromosomeInGeneration.getFitness().getValue().abs()) < 0) {
                    bestChromosomeInGeneration = new Chromosome(population.get(j));
                }
            }
            messenger.AddMesseage(bestChromosomeInGeneration.getFitness().getValue() + "  " + bestChromosomeInGeneration.getRoot().print());
            messenger.GetMesseage();
            //END //TODO::
            if ((bestChromosomeInGeneration.getFitness().getValue().compareTo(BigDecimal.ZERO) == 0)
                    || (numberOfGenerations == i + 1)) {
                dataHandler.setGpStop(true);
                dataHandler.setBestChromosome(bestChromosome);
                messenger.AddMesseage("The Best:  " + bestChromosomeInGeneration.getRoot().print());
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
                    gen = setOfTerminals.get(getRandomNumber(setOfTerminals.size()));
                } else {
                    Gen f = setOfFunctions.get(getRandomNumber(setOfFunctions.size()));
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

    private Chromosome chooseSelectionMethod(int selectionMethod) {
        switch (selectionMethod) {
            case 0: // tournament
                return tournamentSelection3();
            case 1: //roulette
                return rouletteSelection();
        }
        return null;
    }

    private Chromosome rouletteSelection() {
        BigDecimal sumFitness = BigDecimal.ZERO;
        for (int i = 0; i < population.size(); i++) {
            sumFitness = population.get(i).getFitness().getValue();
        }
        Random r = new Random();
        BigDecimal randomNumber = sumFitness.multiply(BigDecimal.valueOf((r.nextDouble() % 1000 / 9999.0f)));

        int memberIndex = 0;
        BigDecimal partialSum = BigDecimal.ZERO;
        while (randomNumber.compareTo(partialSum) > 0) {
            partialSum.add(population.get(memberIndex).getFitness().getValue());
            memberIndex++;
        }
        return population.get(memberIndex);
    }

    private Chromosome tournamentSelection3() {
        int number = 3;
        int[] indexOfArray = new int[number];

        for (int i = 0; i < number; i++) {
            indexOfArray[i] = getRandomNumber(population.size());

            for (int j = 0; j < i; j++) {
                if (indexOfArray[j] == indexOfArray[i]) {
                    indexOfArray[i] = getRandomNumber(population.size());
                    j = 0;
                }
            }
        }

        Chromosome winner = population.get(indexOfArray[0]);

        for (int i = 0; i < number; i++) {
            if (winner.getFitness().getValue().abs().compareTo(population.get(indexOfArray[i]).getFitness().getValue().abs()) > 0) {
                winner = population.get(indexOfArray[i]);
            }
        }

        return new Chromosome(winner);
    }

    private void InitStartPopulate(List<Gen> mnozinaTerminalu, List<Gen> mnozinaFunkci, int maximalniHloubka, int velikostPopulace) {

        for (int i = 0; i < velikostPopulace; i++) {
            Chromosome chromosome = new Chromosome(maximalniHloubka, mnozinaTerminalu, mnozinaFunkci);
            population.add(chromosome);
        }

    }

    public int getRandomNumber(int horniHranice) {
        return (int) (Math.random() * horniHranice);
    }

    public double getRandomDouble(int horniHranice) {
        return Math.random() * horniHranice;
    }

    public static void quicksort(List<Chromosome> population, int left, int right) {
        if (left < right) {
            int boundary = left;
            for (int i = left + 1; i < right; i++) {
                if (population.get(i).getFitness().getValue().compareTo(population.get(left).getFitness().getValue()) > 0) {
                    swap(population, i, ++boundary);
                }
            }
            swap(population, left, boundary);
            quicksort(population, left, boundary);
            quicksort(population, boundary + 1, right);
        }
    }

    private static void swap(List<Chromosome> populace, int left, int right) {
        Chromosome tmp = new Chromosome(populace.get(right));
        populace.set(right, populace.get(left));
        populace.set(left, tmp);
    }

}
