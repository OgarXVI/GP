package cz.ogarxvi.genetic;

import cz.ogarxvi.model.DataHandler;
import cz.ogarxvi.model.GPController;
import cz.ogarxvi.model.Localizator;
import cz.ogarxvi.model.Messenger;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.swing.JProgressBar;

/**
 * Třída pro výpočet GA
 *
 * @author OgarXVI
 */
public class GeneticAlgorithm {
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
     * Seznam vypočítáných výsledků, obvykle se bude využívat pouze prvního
     * nejlepšího výsledku
     */
    private List<Chromosome> ListOfBestResults;
    /**
     * Reference na Thread
     */
    private GPController gpc;
    /**
     * Poslední chromozomy, kontrola pro předčasnou konvergenci
     */
    private Chromosome[] lastChromosomes;
    /**
     * Kolik se má kontrolovat chromozomů
     */
    private final int CHECKED_NUMBER_OF_CHROMOSOMES = 5;
    /**
     * Jaký index má posledně přidaný prvek
     */
    private int indexOfLastAddedChromosome = 0;

    /**
     * Založení GA
     *
     */
    public GeneticAlgorithm() {
        editation = new Editation(DataHandler.getInstance().getParams());
        ListOfBestResults = new ArrayList<>();
        lastChromosomes = new Chromosome[CHECKED_NUMBER_OF_CHROMOSOMES];
    }

    /**
     * Založení GA
     *
     * @param isAutomat Zda je povolen automat, obvykle v případě volání tohoto
     * konstruktoru to bude true
     */
    public GeneticAlgorithm(boolean isAutomat) {
        editation = new Editation(DataHandler.getInstance().getParams());
        this.isAutomat = isAutomat;
        ListOfBestResults = new ArrayList<>();
        lastChromosomes = new Chromosome[CHECKED_NUMBER_OF_CHROMOSOMES];
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
     * @param listOfFunctions seznam funkcí
     * @param listOfTerminals seznam terminálů
     * @return Vrátí nejlepší nalezené řešení
     */
    public Chromosome runGP(int numberOfGenerations, int initSizeOfPopulation, int initTreeMaxDepth, int treeMaxDepthAfterOperation, double reproductionProbability, double crossoverProbability, double mutationProbability, boolean elitismus, boolean decimation, List<Gen> listOfFunctions, List<Gen> listOfTerminals) {
        //Reset STOP 
        DataHandler.getInstance().setGpStop(false);
        //Reset List
        ListOfBestResults.clear();
        //RESER ARRAY
        lastChromosomes = new Chromosome[CHECKED_NUMBER_OF_CHROMOSOMES];

        List<Gen> setOfTerminals = new ArrayList<>();
        List<Gen> setOfFunctions = new ArrayList<>();

        if (listOfFunctions != null) {
            setOfFunctions = listOfFunctions;
        }
        if (listOfTerminals != null) {
            setOfTerminals = listOfTerminals;
        }

        if (DataHandler.getInstance().getLoadedTerminals() != null) {
            setOfTerminals.addAll(DataHandler.getInstance().getLoadedTerminals());
        }

        //EDIT: NAPROSTO PŘEDĚLAT ZPŮSOB ZÍSKÁVÁNÍ 
        //KROKY:
        //ZNÁME POŘADÍ FUNKCÍ:
        //NEJDŘÍVE JE BINAR, PAK UNAR, PAK TRIGO
        //PRVNĚ SE PROJEDE x-KRÁT BINAR, PAK UNAR...
        //KAŽDÝ PRŮCHOD SI MUSÍ ULOŽIT VÝSLEDEK DO POLE NEJLEPŠÍCH VÝSLEDKŮ
        //List<Gen> usedFunctions = new ArrayList<>();
        List<List<Gen>> category = DataHandler.getInstance().getLoadedFunctionsCategories();
        for (int c = 0; c < category.size(); c++) {
            for (int f = 0; f < DataHandler.getInstance().getNumberIterationsCategory()[c]; f++) {
                List<Gen> functions = category.get(c);
                if (functions.isEmpty()) {
                    continue;
                }
                // Založení populace
                population = new ArrayList<>(initSizeOfPopulation);
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

                    InitStartPopulate(setOfTerminals, functions, initTreeMaxDepth, numberOfProgramBeforeDecimation);

                } else {
                    InitStartPopulate(setOfTerminals, functions, initTreeMaxDepth, initSizeOfPopulation);
                }
                // získání nejlepšího jedince na začátku
                Chromosome bestChromosome = new Chromosome(population.get(0));
                // Iterace přes generace
                for (int i = 0; i < numberOfGenerations; i++) {
                    //Stop thread
                    if (DataHandler.getInstance().isGpStop()) {
                        DataHandler.getInstance().setBestChromosome(bestChromosome);
                        return bestChromosome;
                    }
                    // Nejlepší jedinec je vždy první v generaci
                    Chromosome bestChromosomeInGeneration = new Chromosome(population.get(0));
                    //POPULACE
                    for (int j = 0; j < population.size(); j++) {
                        //Namapování hodnot na klíče (X->4...)
                        List<BigDecimal> results = new ArrayList<>();
                        Map<String, BigDecimal> values = new HashMap<>();
                        for (BigDecimal[] mathData : DataHandler.getInstance().getMathData()) {
                            for (int l = 0; l < mathData.length; l++) {
                                values.put(DataHandler.getInstance().getParams()[l], mathData[l]);
                            }
                            for (int m = 0; m < setOfTerminals.size(); m++) {
                                if (!Character.isLetter(setOfTerminals.get(m).command.charAt(0))) {
                                    values.put(setOfTerminals.get(m).command, BigDecimal.valueOf(Double.valueOf(setOfTerminals.get(m).command)));
                                }
                            }
                            results.add(population.get(j).getRoot().resolveCommand(values));
                        }
                        // výpočet fitness jedince
                        population.get(j).getFitness().calculate(results, DataHandler.getInstance().getExpectedResults());
                        // zjištění nejlepšího jedince
                        if (population.get(j).getFitness().getValue().abs().compareTo(bestChromosome.getFitness().getValue().abs()) < 0) {
                            bestChromosome = new Chromosome(population.get(j));
                        }
                        // zjištění nejlešího jedince v populaci
                        if (population.get(j).getFitness().getValue().abs().compareTo(bestChromosomeInGeneration.getFitness().getValue().abs()) < 0) {
                            bestChromosomeInGeneration = new Chromosome(population.get(j));
                        }
                    }

                    //ADD
                    addChromosometoArray(bestChromosomeInGeneration);
                    // Kontroluj možnou předčasnou konvergenci
                    if (i > CHECKED_NUMBER_OF_CHROMOSOMES) {
                        if (checkChromosomes()) {
                            int newPopulationSize = population.size();
                            //NEZVYŠOVAT ČASOVO NÁROČNOST POKUD TO NENÍ NUTNÉ

                            if (!isAutomat) {
                                //Messenger.getInstance().AddMesseage(Localizator.getString("output.gp.prematureConvergence.warning"));
                                //Messenger.getInstance().GetMesseage();
                            }

                            if (population.size() != initSizeOfPopulation) {
                                newPopulationSize /= 10;
                            } else {
                                newPopulationSize *= 10;
                            }
                            //INJECTION METHOD
                            ArrayList<Chromosome> injectedChromosomes = new ArrayList<>(newPopulationSize);
                            for (int j = 0; j < newPopulationSize; j++) {
                                injectedChromosomes.add(new Chromosome(initTreeMaxDepth, setOfTerminals, functions));
                            }
                            //CLEAR ARRAY, INIT NEW FITNESS IN ARRAY FOR MORE TIME BEFORE ANOTHER KONVERGENT
                            for (int j = 0; j < CHECKED_NUMBER_OF_CHROMOSOMES; j++) {
                                lastChromosomes[j] = injectedChromosomes.get(new Random().nextInt(injectedChromosomes.size()));
                            }

                            population = injectedChromosomes;
                            continue;
                        }
                    }

                    // dát zprávu o nejlepším jedinci v populaci
                    if (!isAutomat) {
                        Messenger.getInstance().AddMesseage(bestChromosomeInGeneration.getFitness().getValue() + 
                                "  " + bestChromosomeInGeneration.getRoot().print());
                        Messenger.getInstance().GetMesseage();
                    }
                    // Ukončovací podmínka
                    if ((bestChromosomeInGeneration.getFitness().getValue().compareTo(BigDecimal.ZERO) == 0)
                            || (numberOfGenerations == i + 1)) {
                        //dataHandler.setGpStop(true);
                        //bestChromosome.setFitness(new Fitness(bestChromosome.getFitness().getValue().round(new MathContext(6))));
                        DataHandler.getInstance().setBestChromosome(bestChromosome);
                        if (!isAutomat) {
                            Messenger.getInstance().AddMesseage(Localizator.getString("output.gp.used.functions") + 
                                    Arrays.toString(functions.toArray()));
                            Messenger.getInstance().GetMesseage();
                            Messenger.getInstance().AddMesseage(Localizator.getString("output.gp.chromosome.best") +
                                    bestChromosome.getFitness().getValue() + " " + bestChromosome.getRoot().print());
                            Messenger.getInstance().GetMesseage();
                        }
                        ListOfBestResults.add(bestChromosome);
                        continue;
                    }
                    // zmenšení populace
                    if (decimation && (i == (generationOfDecimation - 1))) {

                        List<Chromosome> populationAfterDecimation = new ArrayList<>(initSizeOfPopulation);
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

                        Chromosome selectedChromosome = chooseSelectionMethod(DataHandler.getInstance().getSelectionMethod());
                        newPopulation.add(selectedChromosome);
                    }

                    // křížení
                    for (int j = 0; j < numberOfCrossover; j++) {

                        Chromosome selectedChromosome1 = chooseSelectionMethod(DataHandler.getInstance().getSelectionMethod());
                        Chromosome selectedChromosome2 = chooseSelectionMethod(DataHandler.getInstance().getSelectionMethod());

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

                        Chromosome selectedChromosome = chooseSelectionMethod(DataHandler.getInstance().getSelectionMethod());

                        SelectedGen selectedGen = randomGen(selectedChromosome);

                        Gen gen = selectedGen.getGenAbove().gens.get(selectedGen.genIndex());

                        if ((gen.depth - 1) == treeMaxDepthAfterOperation) {
                            gen = setOfTerminals.get(getRandomNumber(setOfTerminals.size()));
                        } else {
                            Gen ff = functions.get(getRandomNumber(functions.size()));
                            gen = new Function(ff.command, ff.arita, gen.depth, treeMaxDepthAfterOperation, setOfTerminals, functions);
                        }

                        selectedGen.getGenAbove().gens.set(selectedGen.genIndex(), gen);
                        selectedChromosome.getRoot().fixDepth();

                        newPopulation.add(selectedChromosome);
                    }

                    population = newPopulation;
                }

                //CRASH STATE
                if (ListOfBestResults.isEmpty()) {
                    return population.get(0);
                }

                //SORTListOfBestResults.get(0)
                Collections.sort(ListOfBestResults);
                DataHandler.getInstance().setBestChromosome(ListOfBestResults.get(0));
                editation.editRoot(ListOfBestResults.get(0).getRoot()); // EDIT
                Messenger.getInstance().AddMesseage(
                        Localizator.getString("output.gp.chromosome.best")
                        + ///ListOfBestResults.get(0).getFitness().getValue() +
                        " " + ListOfBestResults.get(0).getRoot().print());
                Messenger.getInstance().GetMesseage();
                //stop
                DataHandler.getInstance().setGpStop(true);
                //GET
                return ListOfBestResults.get(0);
            }
        }
        return null;
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
                    if (!indexs.isEmpty()) {
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
            case 1: // tournament
                return tournamentSelection2();
            case 2: // tournament
                return tournamentSelection5();
            case 3: //roulette
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
            //TODO: NEGATIVE NUMBERS??
            sumFitness = population.get(i).getFitness().getValue();
        }
        int random = getRandomNumber(sumFitness.intValue());
        BigDecimal randomNumber = sumFitness.multiply(BigDecimal.valueOf(random));

        for (int i = 0; i < population.size(); i++) {
            randomNumber = randomNumber.subtract(population.get(i).getFitness().getValue());
            if (randomNumber.compareTo(BigDecimal.ZERO) > 0) {
                return population.get(i);
            }
        }
        return population.get(population.size() - 1);

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
     * Turnajová selekce. Vybere náhodně dva prvky a na nich provede porovnání.
     *
     * @return Vybraný Chromosome
     */
    private Chromosome tournamentSelection2() {
        int number = 2;
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
     * Turnajová selekce. Vybere náhodně pět prvků a na nich provede porovnání.
     *
     * @return Vybraný Chromosome
     */
    private Chromosome tournamentSelection5() {
        int number = 5;
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
     * @param population populace
     * @param left posun vlevo
     * @param right posun vpravo
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

    /**
     * Vrátí true, pokud posledních několik chromozomů bylo stejných.
     *
     * @return Boolean value
     */
    private boolean checkChromosomes() {
        boolean b = false;
        BigDecimal FirstValue = lastChromosomes[0].getFitness().getValue();
        for (int i = 1; i < CHECKED_NUMBER_OF_CHROMOSOMES; i++) {
            if (FirstValue.compareTo(lastChromosomes[i].getFitness().getValue()) == 0) {
                b = true;
            } else {
                return false;
            }
        }
        return b;

    }

    /**
     * Přidá správně prvek do pole, pokud je index vyšší než povolená mez, pak
     * resetuje počítání pole
     *
     * @param bestChromosomeInGeneration Prvek do pole
     */
    private void addChromosometoArray(Chromosome bestChromosomeInGeneration) {
        lastChromosomes[indexOfLastAddedChromosome] = bestChromosomeInGeneration;
        indexOfLastAddedChromosome = CHECKED_NUMBER_OF_CHROMOSOMES - 1 == indexOfLastAddedChromosome ? 0 : indexOfLastAddedChromosome + 1;
    }

    /**
     * Vrátí nejvyšší nalezenou katogegorii
     *
     * @param selectedBoxDataItem Vybrané funkce
     * @return Nejvyšší kategorie
     */
    private int findTheMostCostedFunction(List<DataHandler.BoxDataItem> selectedBoxDataItem) {

        int i = 1;
        for (DataHandler.BoxDataItem boxDataItem : selectedBoxDataItem) {
            i = i < boxDataItem.getCategory() ? boxDataItem.getCategory() : i;
        }
        return i;
    }

}
