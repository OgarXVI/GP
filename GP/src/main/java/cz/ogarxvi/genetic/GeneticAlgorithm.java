package cz.ogarxvi.genetic;

import cz.ogarxvi.model.DataHandler;
import cz.ogarxvi.model.GPController;
import cz.ogarxvi.model.Messenger;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Třída pro výpočet GA
 *
 * @author OgarXVI
 */
public class GeneticAlgorithm {

    /**
     * Odkaz na data
     */
    private DataHandler dataHandler;
    /**
     * Odkaz na záznamník zpráv
     */
    private Messenger messenger;
    /**
     * Populace chromosomů, tedy jedinců představující programy
     */
    private List<Chromosome> population;
    /**
     * Odkaz na optimalizační mechanismus
     */
    private Editation editation;
    /**
     * Zda je puštěn program kongigurací
     */
    private boolean isAutomat;

    /**
     * Založení GA
     *
     * @param gpC Kontroler s potřebnými parametry
     */
    public GeneticAlgorithm(GPController gpC) {
        messenger = gpC.getM();
        dataHandler = gpC.getDh();
        editation = new Editation();
    }

    /**
     *
     * @param m
     * @param dh
     * @param isAutomat
     */
    public GeneticAlgorithm(Messenger m, DataHandler dh, boolean isAutomat) {
        messenger = m;
        dataHandler = dh;
        editation = new Editation();
        this.isAutomat = isAutomat;
    }

    /**
     * Spustí celý proces výpočtu a vyhodnocenování populací. Založí na začátku
     * populace a nechává na na nich vypočítávat fitness. Na základě zadaných
     * parametrů a fitness se snaží dosáhnout co nejblíže k nule. Proces je
     * ukončen nalezením ideálního řešení nebo dovršením maximálního počtu
     * generací.
     *
     * @param numberOfGenerations Počet generací
     * @param initSizeOfPopulation Velikost populace
     * @param initTreeMaxDepth Maximální hloubka stromu
     * @param treeMaxDepthAfterOperation Maximální hloubka stromu po operaci
     * @param reproductionProbability Šance na reprodukci
     * @param crossoverProbability Šance na křížení
     * @param mutationProbability Šance na mutaci
     * @param elitismus Zachování nejlepších jedinců
     * @param decimation Rozšíření parametrů a následné smrštění populací
     * @param editable Puštění optimalizace stromů
     * @param selectionMethod Vybraná selekční metoda
     * @param listOfFunctions
     * @param listOfTerminals
     * @return Vrátí nejlepší nalezené řešení
     */
    public Chromosome runGP(int numberOfGenerations, int initSizeOfPopulation, int initTreeMaxDepth, int treeMaxDepthAfterOperation, double reproductionProbability, double crossoverProbability, double mutationProbability, boolean elitismus, boolean decimation, boolean editable, int selectionMethod, List<Gen> listOfFunctions, List<Gen> listOfTerminals) {
        //Reset STOP 
        dataHandler.setGpStop(false);

        List<Gen> setOfTerminals = null;
        List<Gen> setOfFunctions = null;

        if (listOfFunctions == null) {
            setOfFunctions = new ArrayList<>();

        } else {
            setOfFunctions = listOfFunctions;
        }
        if (listOfTerminals == null) {
            listOfTerminals = new ArrayList<>();

        } else {
            setOfTerminals = listOfTerminals;
        }
        if (dataHandler != null) {
            if (dataHandler.getLoadedTerminals() != null) //TERMINALS
            {
                setOfTerminals.addAll(dataHandler.getLoadedTerminals());
            }
            //FUNCTIONS
            if (dataHandler.getLoadedFunctions() != null) {
                setOfFunctions.addAll(dataHandler.getLoadedFunctions());
            }

        }

        // Založení populace
        population = new ArrayList<Chromosome>(initSizeOfPopulation);
        // Vyhodnocení šancí
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
        // Decimace populace vyžaduje jiný přístup založení
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
        // získání nejlepšího jedince na začátku
        Chromosome bestChromosome = new Chromosome(population.get(0));
        // Iterace přes generace
        for (int i = 0; i < numberOfGenerations; i++) {
            if (dataHandler.isGpStop()) {
                if (!isAutomat) {
                    messenger.AddMesseage("The last best: " + bestChromosome.getRoot().print());
                }
                break;
            }
            // Nejlepší jedinec je vždy první v generaci
            Chromosome bestChromosomeInGeneration = new Chromosome(population.get(0));

            population:
            for (int j = 0; j < population.size(); j++) {
                /*
                if (editable) {
                    System.out.println("Bude nahrazen tento strom: " + population.get(j).getRoot().print());
                    Gen g = editation.editRoot(population.get(j).getRoot());
                    System.out.println("Za tento strom: " + g.print());
                    population.get(j).setRoot(g);
                }
                 */
                //Namapování hodnot na klíče (X->4...)
                List<BigDecimal> results = new ArrayList<>();
                Map<String, BigDecimal> values = new HashMap<>();
                for (int k = 0; k < dataHandler.getMathData().length; k++) {
                    for (int l = 0; l < dataHandler.getMathData()[k].length; l++) {
                        values.put(dataHandler.getParams()[l], dataHandler.getMathData()[k][l]);
                    }
                    for (int m = 0; m < setOfTerminals.size(); m++) {
                        if (!Character.isLetter(setOfTerminals.get(m).command.charAt(0))) {
                            values.put(setOfTerminals.get(m).command, BigDecimal.valueOf(Double.valueOf(setOfTerminals.get(m).command)));
                        }
                    }
                    results.add(population.get(j).getRoot().resolveCommand(values));
                }
                // výpočet fitness jedince
                population.get(j).getFitness().calculate(results, dataHandler.getExpectedResults());
                // zjištění nejlepšího jedince
                if (population.get(j).getFitness().getValue().abs().compareTo(bestChromosome.getFitness().getValue().abs()) < 0) {
                    bestChromosome = new Chromosome(population.get(j));
                }
                // zjištění nejlešího jedince v populaci
                if (population.get(j).getFitness().getValue().abs().compareTo(bestChromosomeInGeneration.getFitness().getValue().abs()) < 0) {
                    bestChromosomeInGeneration = new Chromosome(population.get(j));
                }
            }
            // dát zprávu o nejlepším jedinci v populaci
            if (!isAutomat) {
                messenger.AddMesseage(bestChromosomeInGeneration.getFitness().getValue() + "  " + bestChromosomeInGeneration.getRoot().print());
                messenger.GetMesseage();
            }
            // Ukončovací podmínka
            if ((bestChromosomeInGeneration.getFitness().getValue().compareTo(BigDecimal.ZERO) == 0)
                    || (numberOfGenerations == i + 1)) {
                dataHandler.setGpStop(true);
                dataHandler.setBestChromosome(bestChromosome);
                if (!isAutomat) {
                    messenger.AddMesseage("The Best:  " + bestChromosome.getRoot().print());
                    messenger.GetMesseage();
                }

                return bestChromosome;
            }
            // zmenšení populace
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

            // reprodukce
            for (int j = 0; j < numberOfReproduction; j++) {

                Chromosome selectedChromosome = chooseSelectionMethod(selectionMethod);
                newPopulation.add(selectedChromosome);
            }

            // křížení
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

            // mutace:
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
        return bestChromosome;

    }

    /**
     * Vybere náhodný gen v chromosomu
     *
     * @param chromosome Chromosome
     * @return Náhodný gen
     */
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
        if (getRandomDouble(1) < 0.0f) {
            isFunction = true;
        }

        while (p <= 1) {
            if (getRandomDouble(1) < p) {
                if (!isFunction) {
                    List<Integer> indexs = new ArrayList<>(0);
                    for (int i = 0; i < gen.gens.size(); i++) {
                        if (!gen.gens.get(i).isFunction()) {
                            indexs.add(i);
                        }
                    }
                    if (!indexs.isEmpty()) {
                        int i = indexs.get(getRandomNumber(indexs.size()));
                        SelectedGen vg = new SelectedGen();
                        vg.setGenAbove(gen);
                        vg.setIndex(i);
                        return vg;
                    }
                    p = p + p;

                    gen = gen.gens.get(getRandomNumber(gen.gens.size()));
                } else {
                    List<Integer> indexs = new ArrayList<>(0);
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

    /**
     * Rozhodne o selekční metodě
     *
     * @param selectionMethod Selekční metoda
     * @return Vybraný Chromosom dle vybrané selekční mechaniky
     */
    private Chromosome chooseSelectionMethod(int selectionMethod) {
        switch (selectionMethod) {
            case 0: // tournament
                return tournamentSelection3();
            case 1: //roulette
                return rouletteSelection();
        }
        return null;
    }

    /**
     * Ruletová selekční metoda. Na základě celkové fitness rozvrhne plochu na
     * které bude vybírat a pak dle zvyšující se šance bude vybírat
     *
     * @return Vybraný Chromosome
     */
    private Chromosome rouletteSelection() {
        BigDecimal sumFitness = BigDecimal.ZERO;
        for (int i = 0; i < population.size(); i++) {
            sumFitness = population.get(i).getFitness().getValue();
        }
        int random = getRandomNumber(sumFitness.intValue());
        BigDecimal randomNumber = sumFitness.multiply(BigDecimal.valueOf(random % 1000 / 9999.0f));

        int memberIndex = 0;
        BigDecimal partialSum = BigDecimal.ZERO;
        while (randomNumber.compareTo(partialSum) > 0) {
            partialSum.add(population.get(memberIndex).getFitness().getValue());
            memberIndex++;
        }
        return population.get(memberIndex);
    }

    /**
     * Turnajová selekce. Vybere náhodně tři prvky a na nich provede porovnání.
     *
     * @return Vybraný Chromosome
     */
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

    /**
     * Založí populaci
     *
     * @param listOfTerminals Povolené terminály
     * @param listOfFunctions Povolené funkce
     * @param maxDepth Maximální povolená hloubka
     * @param sizeOfPopulation Velikost Populace
     */
    private void InitStartPopulate(List<Gen> listOfTerminals, List<Gen> listOfFunctions, int maxDepth, int sizeOfPopulation) {

        for (int i = 0; i < sizeOfPopulation; i++) {
            Chromosome chromosome = new Chromosome(maxDepth, listOfTerminals, listOfFunctions);
            population.add(chromosome);
        }

    }

    /**
     * Vrací náhodné číslo do limity
     *
     * @param limit Limit
     * @return Náhodné číslo do limity
     */
    public int getRandomNumber(int limit) {
        return (int) (Math.random() * limit);
    }

    /**
     * Vrací náhodné číslo do limity
     *
     * @param limit Limit
     * @return Náhodné číslo do limity
     */
    public double getRandomDouble(int limit) {
        return Math.random() * limit;
    }

    /**
     * Úprava quicksortu na vlastní porovnání
     *
     * @param population
     * @param left
     * @param right
     */
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

    /**
     * Prohození jedinců
     *
     * @param populace Populace na prohození
     * @param left Posun vlevo
     * @param right Posun vpravo
     */
    private static void swap(List<Chromosome> populace, int left, int right) {
        Chromosome tmp = new Chromosome(populace.get(right));
        populace.set(right, populace.get(left));
        populace.set(left, tmp);
    }

}
