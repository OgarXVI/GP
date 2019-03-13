package cz.ogarxvi.model.genetic;

import cz.ogarxvi.model.DataHandler;
import cz.ogarxvi.model.FileHandler;
import cz.ogarxvi.model.GPThread;
import cz.ogarxvi.model.Localizator;
import cz.ogarxvi.model.Messenger;
import java.awt.BorderLayout;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
    private GPThread gpc;
    /**
     * Poslední chromozomy, kontrola pro předčasnou konvergenci
     */
    private Chromosome[] lastChromosomes;
    /**
     * Kolik se má kontrolovat chromozomů
     */
    private int CHECKED_NUMBER_OF_CHROMOSOMES;
    /**
     * Jaký index má posledně přidaný prvek
     */
    private int indexOfLastAddedChromosome = 0;
    /**
     *
     */
    private List<String> reports;
    /**
     *
     */
    private List<String> outputPdf;

    private Chromosome lastUniqueChromosomeInGeneration;
    private int lastUniqueChromosomeInGenerationIndex;

    private long initTimeRun;
    private long initCategoryTimeRun;
    /**
     * Založení GA
     *
     */
    public GeneticAlgorithm() {
        editation = new Editation(DataHandler.getInstance().getParams());
        ListOfBestResults = new ArrayList<>();
        lastChromosomes = new Chromosome[CHECKED_NUMBER_OF_CHROMOSOMES];
        reports = new ArrayList<>();
        outputPdf = new ArrayList<>();
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
        reports = new ArrayList<>();
        outputPdf = new ArrayList<>();
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
        CHECKED_NUMBER_OF_CHROMOSOMES = initSizeOfPopulation / 2;
        initTimeRun = System.currentTimeMillis();
        //Reset List
        ListOfBestResults.clear();
        //RESER ARRAY
        lastChromosomes = new Chromosome[CHECKED_NUMBER_OF_CHROMOSOMES];
        boolean CheckConvergence = true;
        boolean didConvergence = false;
        if (isAutomat) {
            //Netvoř report při automatu
            DataHandler.getInstance().setCreateFile(false);
            DataHandler.getInstance().setOpenFileAfter(false);
        }

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

        JFrame pf;
        JProgressBar dpb;
        JDialog dlg;
        JLabel jl;
        int workPreg;
        int workDone = 0;
        workPreg = calculateEstimateTime(numberOfGenerations);

        pf = new JFrame();
        pf.setSize(500, 150);
        jl = new JLabel();
        jl.setText("Calculating...");
        pf.add(BorderLayout.CENTER, jl);
        pf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dlg = new JDialog(pf, "Progress Dialog", true);
        dpb = new JProgressBar(0, workPreg);
        dlg.add(BorderLayout.CENTER, dpb);
        dlg.add(BorderLayout.NORTH, jl);
        dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dlg.setSize(300, 75);
        dlg.setLocationRelativeTo(pf);
        if (!isAutomat) {
            //Thread
            Thread t = new Thread(() -> {
                dlg.setVisible(true);
            });

            t.start();
        }
        List<Gen> functions = new ArrayList<>();
        List<List<Gen>> category = DataHandler.getInstance().getLoadedFunctionsCategories();
        int categorySize = category.size();
        if (isAutomat) {
            categorySize = setOfFunctions.size();
        }

        outputPdf.add("Data Name: " + FileHandler.getInstance().file);
        outputPdf.add("Rows: " + DataHandler.getInstance().getTableRows());
        outputPdf.add("Terminals: " + DataHandler.getInstance().getLoadedTerminalsShort());
        outputPdf.add("Functions: " + DataHandler.getInstance().getAllFunctionsAsString());

        Map<String, Double> values = new HashMap<>();
        List<Double> results = new ArrayList();
//        for (BigDecimal[] mathData : DataHandler.getInstance().getMathData()) {
//            for (int l = 0; l < mathData.length; l++) {
//                if (mathData[l] != null) {
//                    values.put(DataHandler.getInstance().getParams()[l], mathData[l]);
//                }
//            }
//            for (int m = 0; m < setOfTerminals.size(); m++) {
//                if (!Character.isLetter(setOfTerminals.get(m).command.charAt(0))) {
//                    values.put(setOfTerminals.get(m).command, BigDecimal.valueOf(Double.valueOf(setOfTerminals.get(m).command)));
//                }
//            }
//            //results.add(population.get(j).getRoot().resolveCommand(values));
//        }
        //Read data before calc
//        Map<String, BigDecimal> values = new HashMap<>();
//        for (BigDecimal[] mathData : DataHandler.getInstance().getMathData()) {
//            for (int l = 0; l < mathData.length; l++) {
//                if (mathData[l] != null) {
//                    values.put(DataHandler.getInstance().getParams()[l], mathData[l]);
//                }
//            }
//            for (int m = 0; m < setOfTerminals.size(); m++) {
//                if (!Character.isLetter(setOfTerminals.get(m).command.charAt(0))) {
//                    values.put(setOfTerminals.get(m).command, BigDecimal.valueOf(Double.valueOf(setOfTerminals.get(m).command)));
//                }
//            }
//        }
        category:
        for (int c = 0; c < categorySize; c++) {
            initCategoryTimeRun = System.currentTimeMillis();
            lastUniqueChromosomeInGeneration = null;
            lastUniqueChromosomeInGenerationIndex = -1;
//            values.clear();
//            for (double[] mathData : DataHandler.getInstance().getMathData()) {
//
//                for (int l = 0; l < mathData.length; l++) {
//
//                    values.put(DataHandler.getInstance().getParams()[l], mathData[l]);
//
//                }
//                for (int m = 0; m < setOfTerminals.size(); m++) {
//                    if (!Character.isLetter(setOfTerminals.get(m).command.charAt(0))) {
//                        values.put(setOfTerminals.get(m).command, Double.valueOf(setOfTerminals.get(m).command));
//                    }
//                }
//                //results.add(population.get(j).getRoot().resolveCommand(values));
//            }
//            int numberIteartionInCategory = DataHandler.getInstance().getNumberIterationsCategory(c);
//            if (isAutomat) {
//                numberIteartionInCategory = setOfFunctions.size();
//            }
//            for (int f = 0; f < numberIteartionInCategory; f++) {
            // Založení populace
            population = new ArrayList<>(initSizeOfPopulation);
            didConvergence = false; //reset
            writeReport("Population size: " + initSizeOfPopulation);
            writeReport("Number of generation: " + numberOfGenerations);
            // Vyhodnocení šancí
            int numberOfReproduction = (int) (reproductionProbability * initSizeOfPopulation);
            writeReport("Number of chromosomes to reproduction: " + numberOfReproduction);
            int numberOfCrossover = (int) (crossoverProbability * initSizeOfPopulation) / 2;
            writeReport("Number of chromosomes to crossover: " + numberOfCrossover);
            int numberOfMutation = (int) (mutationProbability * initSizeOfPopulation);
            writeReport("Number of chromosomes to mutation: " + numberOfMutation);
            writeReport("Selection method: " + DataHandler.getInstance().getSelectionMethod());
            numberOfReproduction = decimationIncrement(numberOfReproduction, numberOfCrossover, numberOfMutation, initSizeOfPopulation, elitismus);
            if (isAutomat) {
                functions = setOfFunctions;
            } else {
                String report000 = "Functions " + Arrays.toString(category.get(c).toArray()) + " added";
                if (DataHandler.getInstance().isPrintGeneration()) {
                    Messenger.getInstance().AddMesseage(report000);
                    Messenger.getInstance().GetAllMesseages();
                }
                writeReport(report000);
                System.out.println(report000);
                functions.addAll(category.get(c));
            }
            if (!isAutomat) {
                if (category.get(c).isEmpty()) {
                    continue;
                }
            }

            int generationOfDecimation = 10;
            int numberOfProgramBeforeDecimation = 10 * initSizeOfPopulation;
            // Decimace populace vyžaduje jiný přístup založení
            if (decimation) {
                population = new ArrayList<>(numberOfProgramBeforeDecimation);

                numberOfReproduction = (int) (reproductionProbability * numberOfProgramBeforeDecimation);
                numberOfCrossover = (int) (crossoverProbability * numberOfProgramBeforeDecimation) / 2;
                numberOfMutation = (int) (mutationProbability * numberOfProgramBeforeDecimation);

                numberOfReproduction = decimationIncrement(numberOfReproduction, numberOfCrossover, numberOfMutation, numberOfProgramBeforeDecimation, elitismus);

                InitStartPopulate(setOfTerminals, functions, initTreeMaxDepth, numberOfProgramBeforeDecimation);
            } else {
                InitStartPopulate(setOfTerminals, functions, initTreeMaxDepth, initSizeOfPopulation);
            }
            // získání nejlepšího jedince na začátku
            Chromosome bestChromosome = new Chromosome(population.get(0));
            // Iterace přes generace
            for (int i = 1; i <= numberOfGenerations; i++) {
                String report00 = "Generation " + i + " started...";

                if (DataHandler.getInstance().isPrintGeneration()) {
                    Messenger.getInstance().AddMesseage(report00);
                    //Messenger.getInstance().GetMesseage();
                }
                writeReport(report00);
                workDone += 10;
                dpb.setValue(workDone);
                //Stop thread
                if (DataHandler.getInstance().isGpStop()) {
                    DataHandler.getInstance().setBestChromosome(bestChromosome);
                    return returnBestResult(dlg, jl, pf, dpb, workPreg, lastUniqueChromosomeInGenerationIndex, functions);
                }
                // Nejlepší jedinec je vždy první v generaci
                Chromosome bestChromosomeInGeneration = new Chromosome(population.get(0));
                if (lastUniqueChromosomeInGeneration == null || 
                        !bestChromosomeInGeneration.toString().equals(lastUniqueChromosomeInGeneration.toString())) {
                    lastUniqueChromosomeInGeneration = bestChromosomeInGeneration;
                    lastUniqueChromosomeInGenerationIndex = i;
                }
                //POPULACE
                for (int j = 0; j < population.size(); j++) {
                    if (DataHandler.getInstance().isGpStop()) {
                        DataHandler.getInstance().setBestChromosome(bestChromosome);
                        return returnBestResult(dlg, jl, pf, dpb, workPreg, lastUniqueChromosomeInGenerationIndex, functions);
                    }
                    values.clear();
                    results.clear();
                    for (double[] mathData : DataHandler.getInstance().getMathData()) {
                        for (int l = 0; l < mathData.length; l++) {
                            values.put(DataHandler.getInstance().getParams()[l], mathData[l]);
                        }
                        for (int m = 0; m < setOfTerminals.size(); m++) {
                            if (!Character.isLetter(setOfTerminals.get(m).command.charAt(0))) {
                                values.put(setOfTerminals.get(m).command, Double.valueOf(setOfTerminals.get(m).command));
                            }
                        }
                        results.add(population.get(j).getRoot().resolveCommand(values));
                    }
                    // výpočet fitness jedince
                    population.get(j).getFitness().calculate(results, DataHandler.getInstance().getExpectedResults());
                    String report01 = "Chromosome " + j + " in generation " + i + " has fitness " + population.get(j).getFitness().getValue();
                    String report02 = "Chromosome " + j + " is " + population.get(j).toString();
                    if (DataHandler.getInstance().isPrintPopulation()) {
                        Messenger.getInstance().AddMesseage(report01);
                        Messenger.getInstance().AddMesseage(report02);
                        //Messenger.getInstance().GetAllMesseages();
                    }
                    writeReport(report01);
                    // zjištění nejlepšího jedince
                    if (Math.abs(population.get(j).getFitness().getValue()) < Math.abs(bestChromosome.getFitness().getValue())) {
                        bestChromosome = new Chromosome(population.get(j));
                    }
                    // zjištění nejlešího jedince v populaci
                    if (Math.abs(population.get(j).getFitness().getValue()) < Math.abs(bestChromosomeInGeneration.getFitness().getValue())) {
                        bestChromosomeInGeneration = new Chromosome(population.get(j));
                        //Toleranční podmínka
                        if (Math.abs(bestChromosomeInGeneration.getFitness().getValue())
                                <= Math.abs(DataHandler.getInstance().getFitnessLimit())) {
                            String reportToleration00 = "Found solution! Toleration: " + DataHandler.getInstance().getFitnessLimit() + " found.";
                            writeReport(reportToleration00);
                            //System.out.println(reportToleration00);
                            ListOfBestResults.add(bestChromosomeInGeneration);
                            DataHandler.getInstance().setBestChromosome(bestChromosomeInGeneration);
                            return returnBestResult(dlg, jl, pf, dpb, workPreg, lastUniqueChromosomeInGenerationIndex, functions);
                        }
                    }
                }

                //ADD
                addChromosometoArray(bestChromosomeInGeneration);
                // Kontroluj možnou předčasnou konvergenci
                if (CheckConvergence) {
                    if (i > CHECKED_NUMBER_OF_CHROMOSOMES) {
                        if (checkChromosomes()) {
                            //TODO::
                            if (!didConvergence) { //Pouze jednou konvergenci!
                                didConvergence = true;
                            } else {
                                printCategoryReport(functions, lastUniqueChromosomeInGeneration, lastUniqueChromosomeInGenerationIndex);
                                continue category;
                            }
                            report00 = "Convergencion injection:";
                            writeReport(report00);
                            int newPopulationSize = population.size();
                            if (population.size() != initSizeOfPopulation) {
                                newPopulationSize /= 2;
                            } else {
                                newPopulationSize *= 2;
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
                }

                // dát zprávu o nejlepším jedinci v populaci
                if (!isAutomat) {
                    String report02 = "Best Chromosome in generation (" + i + ")";
                    String report03 = bestChromosomeInGeneration.getFitness().getValue() + "  " + bestChromosomeInGeneration.getRoot().print();
                    writeReport(report02);
                    writeReport(report03);
                    //TEST
                    System.out.println(report03);
                }
                // Ukončovací podmínka
                if (bestChromosomeInGeneration.getFitness().getValue() == 0 ||
                     (Math.abs(bestChromosomeInGeneration.getFitness().getValue())
                            < Math.abs(DataHandler.getInstance().getFitnessLimit())
                            || (numberOfGenerations == i))) { //WAS +1
                        DataHandler.getInstance().setBestChromosome(bestChromosome);
                        ListOfBestResults.add(bestChromosome);
                        printCategoryReport(functions, bestChromosomeInGeneration, lastUniqueChromosomeInGenerationIndex);
                        //rovnou utni výsledek
                        if ((bestChromosomeInGeneration.getFitness().getValue() == 0)) {
                            return returnBestResult(dlg, jl, pf, dpb, workPreg, lastUniqueChromosomeInGenerationIndex, functions);
                        }
                    
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

                    numberOfReproduction = decimationIncrement(numberOfReproduction,
                            numberOfCrossover, numberOfMutation, initSizeOfPopulation, elitismus);

                    population = populationAfterDecimation;

                }

                List<Chromosome> newPopulation = new ArrayList<Chromosome>();

                if (elitismus) {
                    Chromosome bestChromosome2 = new Chromosome(bestChromosome);
                    newPopulation.add(bestChromosome2);
                }
                //reproduction
                reproduction(numberOfReproduction, newPopulation);
                //crossover
                crossover(numberOfCrossover, treeMaxDepthAfterOperation, newPopulation);
                // mutace:
                mutation(numberOfMutation, treeMaxDepthAfterOperation, setOfTerminals, functions, newPopulation);
                population = newPopulation;
            }
            //}
        }
        return returnBestResult(dlg, jl, pf, dpb, workPreg, -1, functions);
    }

    /**
     * //Už jsme to jednou opravovali a pravděpodobně se ude už nenajde lepší
     * výsledek, čas vrátit zprávu a utnout
     *
     * @param functions f
     * @param bestChromosomeInGeneration ch
     * @param i i
     */
    private void printCategoryReport(List<Gen> functions, Chromosome bestChromosomeInGeneration, int i) {
        String line = "--------------------------------------------------";
        String stringF = "For functions: " + Arrays.toString(functions.toArray());
        String stringFound = "Found: " + bestChromosomeInGeneration;
        String stringFitness = "Fitness: " + bestChromosomeInGeneration.getFitness().getValue();
        String inGeneration = "In generation: " + i;
        long time = (System.currentTimeMillis() - initCategoryTimeRun);
        long seconds = (time / 1000) % 60;
        String inTime = "Elapsed seconds: " + seconds;

        Messenger.getInstance().AddMesseage(line);
        Messenger.getInstance().GetMesseage();
        Messenger.getInstance().AddMesseage(stringF);
        Messenger.getInstance().GetMesseage();
        Messenger.getInstance().AddMesseage(stringFound);
        Messenger.getInstance().GetMesseage();
        Messenger.getInstance().AddMesseage(stringFitness);
        Messenger.getInstance().GetMesseage();
        Messenger.getInstance().AddMesseage(inGeneration);
        Messenger.getInstance().GetMesseage();
        Messenger.getInstance().AddMesseage(inTime);
        Messenger.getInstance().GetMesseage();

        outputPdf.add(line);
        outputPdf.add(stringF);
        outputPdf.add(stringFound);
        outputPdf.add(stringFitness);
        outputPdf.add(inGeneration);
        outputPdf.add(inTime);
        outputPdf.add(line);
    }

    private void mutation(int numberOfMutation, int treeMaxDepthAfterOperation, List<Gen> setOfTerminals, List<Gen> functions, List<Chromosome> newPopulation) {
        for (int j = 0; j < numberOfMutation; j++) {

            Chromosome selectedChromosome = chooseSelectionMethod(DataHandler.getInstance().getSelectionMethod());
            writeReport("For mutation was selected: " + selectedChromosome.toString());
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
            writeReport("Mutation created: " + selectedChromosome.toString());
            newPopulation.add(selectedChromosome);
        }
    }

    private void crossover(int numberOfCrossover, int treeMaxDepthAfterOperation, List<Chromosome> newPopulation) {
        for (int j = 0; j < numberOfCrossover; j++) {

            Chromosome selectedChromosome1 = chooseSelectionMethod(DataHandler.getInstance().getSelectionMethod());
            Chromosome selectedChromosome2 = chooseSelectionMethod(DataHandler.getInstance().getSelectionMethod());
            writeReport("For Crossover was selected " + selectedChromosome1.toString());
            writeReport("For Crossover was selected " + selectedChromosome2.toString());
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

                writeReport("After CrossOver was addded " + selectedChromosome1.toString());
                writeReport("After CrossOver was addded " + selectedChromosome2.toString());
                newPopulation.add(selectedChromosome1);
                newPopulation.add(selectedChromosome2);

            } else {
                j--;
            }
        }
    }

    private void reproduction(int numberOfReproduction, List<Chromosome> newPopulation) {
        for (int j = 0; j < numberOfReproduction; j++) {
            Chromosome selectedChromosome = chooseSelectionMethod(DataHandler.getInstance().getSelectionMethod());
            writeReport("Reproduction created " + selectedChromosome.toString());
            newPopulation.add(selectedChromosome);
        }
    }

    private int decimationIncrement(int numberOfReproduction, int numberOfCrossover, int numberOfMutation, int numberOfProgramBeforeDecimation, boolean elitismus) {
        while ((numberOfReproduction + (numberOfCrossover * 2) + numberOfMutation) < numberOfProgramBeforeDecimation) {
            numberOfReproduction++;
        }
        if (elitismus) {
            numberOfReproduction = numberOfReproduction - 1;
        }
        return numberOfReproduction;
    }

    private Chromosome returnBestResult(JDialog dlg, JLabel jl, JFrame parentFrame, JProgressBar bar, int workReq, int i, List<Gen> functions) {
        //CRASH STATE
        if (DataHandler.getInstance().isGpStop()) {
            jl.setText("Stopped...");
            //
            dlg.setVisible(false);
            dlg.dispose();
            parentFrame.dispose();
            return population.get(0);
        }
        if (ListOfBestResults != null) {
            if (ListOfBestResults.isEmpty()) {
                return population.get(0);
            }
        } else {
            return population.get(0);
        }
        //Full
        bar.setValue(workReq);
        //SORT
        Collections.sort(ListOfBestResults);
        //POST
        printCategoryReport(functions, ListOfBestResults.get(0), lastUniqueChromosomeInGenerationIndex);
        //TIME
        long time = (System.currentTimeMillis() - initTimeRun);
        long seconds = (time / 1000) % 60;
        long minute = (time / (1000 * 60)) % 60;
        long hour = (time / (1000 * 60 * 60)) % 60;
        String timeR = "H:" + hour + " M:" + minute + " S:" + seconds;
        String reportTime = "Elapsed time: " + timeR;
        writeReport(reportTime);
        outputPdf.add(reportTime);
        jl.setText("Postprocessing...");
        DataHandler.getInstance().setBestChromosome(ListOfBestResults.get(0));
        String simplified = ListOfBestResults.get(0).toString();
        if (!isAutomat) {
            simplified = editation.simplify(ListOfBestResults.get(0)); // EDIT
        }
        String report00 = Localizator.getString(
                "output.gp.chromosome.best")
                + " " + simplified;
        String report01 = Localizator.getString(
                "output.gp.deflection")
                + " (" + ListOfBestResults.get(0).getFitness().getValue() + " "
                + Localizator.getString("output.gp.deflection.from") + ")";
        outputPdf.add(report00);
        outputPdf.add(report01);
        writeReport(report00);
        writeReport(report01);

        Messenger.getInstance().AddMesseage(report00);
        Messenger.getInstance().AddMesseage(report01);
        Messenger.getInstance().GetAllMesseages();
        //Report
        if (!isAutomat) {
            //Thread
            Thread t2 = new Thread(() -> {
                if (DataHandler.getInstance().isCreateFile()) {
                    FileHandler.getInstance().createReport(reports,
                            outputPdf,
                            DataHandler.getInstance().isOpenFileAfter(),
                            DataHandler.getInstance().isCreatePdfOutput(),
                            DataHandler.getInstance().getExpansionReportFile());
                }
            });
            t2.start();
        }

        //Dispose
        dlg.setVisible(false);
        dlg.dispose();
        parentFrame.dispose();
        //stop
        DataHandler.getInstance().setGpStop(true);
        //Create and open report
        //GET
        //DataHandler.getInstance().setBestChromosome(ListOfBestResults.get(0));
        return ListOfBestResults.get(0);
    }

    /**
     * Přidá záznam do reportu, pokud je potřeba
     *
     * @param s report
     */
    private void writeReport(String s) {
        if (DataHandler.getInstance().isCreateFile()) {
            reports.add(s);
        }
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
        double sumFitness = 0d;
        for (int i = 0; i < population.size(); i++) {
            //TODO: NEGATIVE NUMBERS??
            sumFitness = population.get(i).getFitness().getValue();
        }
        int random = getRandomNumber((int) sumFitness);
        double randomNumber = sumFitness * random;

        for (int i = 0; i < population.size(); i++) {
            randomNumber = randomNumber - population.get(i).getFitness().getValue();
            if (randomNumber > 0) {
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
            if (Math.abs(winner.getFitness().getValue()) > Math.abs(population.get(indexOfArray[i]).getFitness().getValue())) {
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
            if (Math.abs(winner.getFitness().getValue()) > Math.abs(population.get(indexOfArray[i]).getFitness().getValue())) {
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
            if (Math.abs(winner.getFitness().getValue()) > Math.abs(population.get(indexOfArray[i]).getFitness().getValue())) {
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
            String report00 = "Chromosome " + chromosome.getRoot().print() + " created";
            if (DataHandler.getInstance().isPrintPopulation()) {
                Messenger.getInstance().AddMesseage(report00);
            }
            writeReport(report00);
            population.add(chromosome);
        }
        Messenger.getInstance().GetAllMesseages();
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
//    public static void quicksort(List<Chromosome> population, int left, int right) {
//        if (left < right) {
//            int boundary = left;
//            for (int i = left + 1; i < right; i++) {
//                if (population.get(i).getFitness().getValue() > population.get(left).getFitness().getValue()) {
//                    swap(population, i, ++boundary);
//                }
//            }
//            swap(population, left, boundary);
//            quicksort(population, left, boundary);
//            quicksort(population, boundary + 1, right);
//        }
//    }
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
        double FirstValue = lastChromosomes[0].getFitness().getValue();
        for (int i = 1; i < CHECKED_NUMBER_OF_CHROMOSOMES; i++) {
            if (FirstValue == lastChromosomes[i].getFitness().getValue()) {
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
     * Vypočítá počet počítáných funkcí pro generace a populace a vynásobí 10.
     *
     * @return Vrátí odhad času
     */
    private int calculateEstimateTime(int numberOfGenerations) {
        return (numberOfGenerations * DataHandler.getInstance().getLoadedFunctionsCategories().size()) * 10;
    }

}
