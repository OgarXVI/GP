/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.model;

import cz.ogarxvi.model.genetic.Chromosome;
import cz.ogarxvi.model.genetic.Function;
import cz.ogarxvi.model.genetic.Gen;
import cz.ogarxvi.model.genetic.Terminal;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Třída obsahující potřebná data pro aplikaci, navržená jako Singleton.
 *
 * @author OgarXVI
 */
public class DataHandler {

    /**
     *
     */
    private int rows;
    /**
     * Vybraná selekční metoda
     */
    private int selectionMethod = 0;
    /**
     * Hlavní terminály tabulky, vychází jako názvy sloupců tabulky, kromě
     * posledního výsledkového
     */
    private String[] params;
    /**
     * Tabulka dat, tedy obsah načteného souboru kromě výsledkového sloupce
     */
    private double[][] mathData;
    /**
     * Výsledkový sloupec, slouží pro porovnání vypočtených výsledků.
     */
    private double[] expectedResults;
    /**
     * Definuje, zda došlo k načtení souboru
     */
    private boolean loaded;
    /**
     * List načtených funkcí
     */
    private List<List<Gen>> loadedFunctionsCategories;
    /**
     * List načtených terminálů
     */
    private List<Gen> loadedTerminals;
    /**
     * Definuje, zda se má výpočet GA zastavit
     */
    private volatile boolean gpStop;
    /**
     * Pomocná proměnná, vybraný nejlepší jedinec
     */
    private Chromosome bestChromosome;
    /**
     * Určuje počet použití daných kategorií 0 - Binary, 1 - Unary, 2 -
     * Trigonometric
     */
    //private int[] numberIterationsCategory;
    /**
     * Reference na odkaz
     */
    private static DataHandler instance;
    /**
     *
     */
    private List<String> allTerminals;
    /**
     *
     */
    private List<String> allCheckedTerminals;
    /**
     *
     */
    private boolean printGeneration;
    /**
     *
     */
    private boolean printPopulation;
    /**
     *
     */
    private boolean createFile = true;
    /**
     *
     */
    private boolean openFileAfter = true;
    /**
     *
     */
    private double fitnessLimit;
    /**
     *
     */
    private double calculatedAritResults;
    /**
     *
     */
    private String expansionReportFile = "txt";
    /**
     *
     */
    private boolean createPdfOutput = true;

    /**
     * Založení uložiště dat
     */
    private DataHandler() {
        //numberIterationsCategory = new int[FunctionCategory.values().length];
        loadedTerminals = new ArrayList<>();
        allTerminals = new ArrayList<>();
        allCheckedTerminals = new ArrayList<>();
        loadedFunctionsCategories = new ArrayList<>();
        fitnessLimit = 0d;
        generateAllFunction();
    }

    /**
     * Přístup ke Singletonu
     *
     * @return Singleton Datahandler
     */
    public static synchronized DataHandler getInstance() {
        if (instance == null) {
            instance = new DataHandler();
        }
        return instance;
    }

    /**
     *
     * @return Vrátí všechny terminály jako list stringů
     */
    public List<String> getStringFromTerminals() {
        List<String> pom = new ArrayList<>();
        for (Gen loadedTerminal : loadedTerminals) {
            if (!Character.isAlphabetic(loadedTerminal.getCommand().charAt(0))) {
                pom.add(loadedTerminal.getCommand());
            }
        }
        return pom;
    }

    /**
     * Zpracuje data z načítače souboru na data potřebné pro aplikaci
     *
     * @param data Data z IReader
     */
    public void parseData(String[][] data) {
        //Vyčisti prostor
        params = data[0];
        mathData = new double[data.length - 1][params.length - 1];
        expectedResults = new double[data.length - 1];
        //Načti data pro tabulku a sloupec
        for (int i = 0; i < mathData.length; i++) {
            for (int j = 0; j < mathData[i].length; j++) {
                mathData[i][j] = resultMathData(data, i, j);
            }
            expectedResults[i] = resultMathData(data, i, mathData[i].length);
        }
        //uprav správně hlavičky na terminály, odstraň poslední sloupec
        params = Arrays.copyOf(params, params.length - 1);
        // DataHandler byl načten
        loaded = true;

        // přidá hlavičky jako terminály
        loadParamsAsTerminals();

        /*System.out.println("PARAM:");
        System.out.println(Arrays.toString(params));
        System.out.println("MATHDATA:");
        for (BigDecimal[] math : mathData) {
            System.out.println(Arrays.toString(math));
        }
        System.out.println("RESULTS:");
        System.out.println(Arrays.toString(expectedResults));
         */
        //System.out.println("Errors:");
        //System.out.println(Arrays.toString(errorRowPosition.toArray()));
    }

    private double resultMathData(String[][] data, int i, int j) {
        double pomValue = 0d;
        try {
            pomValue = Double.valueOf(data[i + 1][j]);
        } catch (NullPointerException | NumberFormatException e) {
            FileHandler.getInstance().errorsInFile = true; //registruj chybu
            FileHandler.getInstance().errorRowPositions.add(i);
            FileHandler.getInstance().errorColummPosition.add(j);
            //return null; //pracujeme se zástupnými znaky //TODO: POZOR NA NULU
        }
        return pomValue;
    }

    public String[] getParams() {
        return params;
    }

    public double[][] getMathData() {
        return mathData;
    }

    public double[] getExpectedResults() {
        return expectedResults;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public List<String> getAllCheckedTerminals() {
        return allCheckedTerminals;
    }

    public List<List<Gen>> getLoadedFunctionsCategories() {
        return loadedFunctionsCategories;
    }

    public List<Gen> getLoadedTerminals() {
        return loadedTerminals;
    }

    public boolean isGpStop() {
        return gpStop;
    }

    public Chromosome getBestChromosome() {
        return bestChromosome;
    }
//
//    public int[] getNumberIterationsCategory() {
//        return numberIterationsCategory;
//    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public void gpStop() {
        this.gpStop = true;
    }

    public void setGpStop(boolean gpStop) {
        this.gpStop = gpStop;
    }

    public void setBestChromosome(Chromosome bestChromosome) {
        this.bestChromosome = bestChromosome;
    }

    public int getSelectionMethod() {
        return selectionMethod;
    }

    public void setSelectionMethod(int selectionMethod) {
        this.selectionMethod = selectionMethod;
    }

    public int getTableRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public List<String> getAllTerminals() {
        return allTerminals;
    }

    public void setAllTerminals(List<String> allTerminals) {
        this.allTerminals = allTerminals;
    }

    public boolean isPrintGeneration() {
        return printGeneration;
    }

    public void setPrintGeneration(boolean printGeneration) {
        this.printGeneration = printGeneration;
    }

    public boolean isPrintPopulation() {
        return printPopulation;
    }

    public void setPrintPopulation(boolean printPopulation) {
        this.printPopulation = printPopulation;
    }

    public boolean isCreateFile() {
        return createFile;
    }

    public void setCreateFile(boolean createFile) {
        this.createFile = createFile;
    }

    public boolean isOpenFileAfter() {
        return openFileAfter;
    }

    public void setOpenFileAfter(boolean openFileAfter) {
        this.openFileAfter = openFileAfter;
    }

    public double getFitnessLimit() {
        return fitnessLimit;
    }

    public void setFitnessLimit(double fitnessLimit) {
        this.fitnessLimit = fitnessLimit;
    }

    public String getExpansionReportFile() {
        return expansionReportFile;
    }

    public void setExpansionReportFile(String expansionReportFile) {
        this.expansionReportFile = expansionReportFile;
    }

    public boolean isCreatePdfOutput() {
        return createPdfOutput;
    }

    public void setCreatePdfOutput(boolean createPdfOutput) {
        this.createPdfOutput = createPdfOutput;
    }

    /**
     * Vyhodnotí relativní omezení fitness funkce, u které se má zastavit.
     *
     * @param newValue Jakou mírou upravuje uživatel fitness treshold
     */
    public void resolveFitnessLimit(double newValue) {
        //Ideální by bylo zjistit arit průměr a poté dělit počtem řádků.
        //Tím se vymezí nějaká ok hodnota pro limit.
        //Např. když výsledky se pochybují 10-12 a mám 1000 řádků, tak limit by mohl být 0,011
//        if (calculatedAritResults == 0d) {
//            calculatedAritResults = calculateAritResults();
//        }
          fitnessLimit = newValue;
    }
    
    private double pdf(double x) {
        return Math.exp(-x*x / 2) / Math.sqrt(2 * Math.PI);
    }

    private double cdf(double z) {
        if (z < 0.0) return 0.0;
        if (z >  1.0) return 1.0;
        double sum = 0.0, term = z;
        for (int i = 3; sum + term != sum; i += 2) {
            sum  = sum + term;
            term = term * z * z / i;
        }
        return sum * pdf(z);
    }
    
    /**
     * Vypočte průměr všech načtených výsledků
     *
     * @return Průměr výsledků
     */
    private double calculateAritResults() {
        double pomAritResult = 0d;
        int realtSize = 0;
        if (expectedResults != null) {
            for (double expectedResult : expectedResults) {
                pomAritResult += expectedResult;
                realtSize++;
            }
        }
        if (realtSize == 0) {
            return 0;
        }
        return pomAritResult / realtSize;
    }

    /**
     * Načte hlavičky jako terminály
     */
    public void loadParamsAsTerminals() {
        if (loaded) {
            for (String string : params) {
                loadedTerminals.add(new Terminal(string));
            }
        }
    }

    /**
     * Vygeneruje terminály dle zadaného řetezce
     *
     * @param string Terminály
     * @return List terminálů
     */
    public List<Gen> generateTerminals(String string) {
        return Terminal.getSet(string);
    }

    /**
     * Vygeneruje funkce dle zadaného řetezce
     *
     * @param string Funkce
     * @param arita Arita funkcí
     * @return List fukncí
     */
    public List<Gen> generateFunctions(String string, int arita) {
        return Function.getSet(string, arita, false);
    }

    /**
     * Přidá funkce do seznamu
     *
     * @param type Jaký typ kategorie to je
     */
//    private void AddFunctions(int type) {
//        if (type >= loadedFunctionsCategories.size()) {
//            loadedFunctionsCategories.add(new ArrayList<>());
//        }
//        //loadedFunctionsCategories.get(type).clear();
//        loadedFunctionsCategories.get(type).addAll(getFunctionsByCategory(type));
//    }
    /**
     * Vrátí všechny funkce jako položky v poli
     *
     * @return Načtené funkce jako string
     */
    public String getAllFunctionsAsString() {
        String pom = "[";
        if (!loadedFunctionsCategories.isEmpty()) {
            for (List<Gen> loadedFunctionsCategory : loadedFunctionsCategories) {
                if (loadedFunctionsCategory != null) {
                    for (Gen gen : loadedFunctionsCategory) {
                        if (gen != null) {
                            pom += gen.getCommand() + ", ";
                        }
                    }
                }
            }
        }
        pom = pom.substring(0, pom.length() - 2); //REMOVE ", "
        return pom += "]";
    }

    /**
     * Vrátí funkce dle zadané kategorie
     *
     * @param category typ kategorie
     * @return List funkcí
     */
    private List<Gen> getFunctionsByCategory(int category) {
        List<Gen> list = new ArrayList<>();
        switch (category) {
            case 0: { // PLUS_MINUS
                list.addAll(Function.getSet("+", 2, false));
                list.addAll(Function.getSet("-", 2, false));
                break;
            }
            case 1: { //DIVINE_TIMES
                list.addAll(Function.getSet("*", 2, false));
                list.addAll(Function.getSet("/", 2, false));
                break;
            }
            case 2: { // EXP
                list.addAll(Function.getSet("^", 2, false));
                break;
            }
            case 3: { // SQRT
                list.addAll(Function.getSet("sqrt", 1, false));
                break;
            }
            case 4: { //LOG
                list.addAll(Function.getSet("log", 1, false));
                list.addAll(Function.getSet("ln", 1, false));
                break;
            }
            case 5: { //SINUS
                list.addAll(Function.getSet("sin", 1, false));
                break;
            }
            case 6: { //COS
                list.addAll(Function.getSet("cos", 1, false));
                break;
            }
            case 7: { //TAN
                list.addAll(Function.getSet("tan", 1, false));
                break;
            }
        }
        return list;
    }

    /**
     *
     * @param inputListString vstupní list stringů
     */
    public void setAllCheckedTerminals(List<String> inputListString) {
        allCheckedTerminals = inputListString;
        loadedTerminals.clear();
        for (String string : inputListString) {
            loadedTerminals.addAll(generateTerminals(string));
        }
        loadParamsAsTerminals();
    }

    /**
     * Zjistí, zda byla načtena aspoň jedna funkce v seznamu kategorií funkcí
     *
     * @return vrátí true, pokud je nějaká funkce v seznamu
     */
    public boolean isAnyFunctionsLoaded() {
        return loadedFunctionsCategories.stream().anyMatch((loadedFunctionsCategory) -> (!loadedFunctionsCategory.isEmpty()));
    }

    /**
     * Přidá všechny funkce dle kategorií
     */
    private void generateAllFunction() {
        loadedFunctionsCategories.clear();
        for (FunctionCategory category : FunctionCategory.values()) {
            loadedFunctionsCategories.add(new ArrayList<>());
            //loadedFunctionsCategories.get(type).clear();
            loadedFunctionsCategories.get(category.category).addAll(getFunctionsByCategory(category.category));
        }
    }

    /**
     * return
     *
     * @return return
     */
    public String getLoadedTerminalsShort() {
        String pom = "[";
        int limit = 10;
        int now = 0;
        for (Gen loadedTerminal : loadedTerminals) {
            pom += loadedTerminal.getCommand();
            now++;
            if (now >= limit) {
                pom += ",...]";
                return pom;
            } else {
                pom += ", ";
            }
        }
        pom = pom.substring(0, pom.length() - 2); //REMOVE ", "
        pom+="]";
        return pom;
    }

    public enum FunctionCategory {
        PLUS_MINUS(0), TIMES_DIVINE(1), EXP(2), SQRT(3), LOG(4), TRIGONOMETRIK_SINUS(5), TRIGONOMETRIK_COS(6), TRIGONOMETRIK_TAN(7);
        public final int category;

        private FunctionCategory(int category) {
            this.category = category;
        }

    }

}
