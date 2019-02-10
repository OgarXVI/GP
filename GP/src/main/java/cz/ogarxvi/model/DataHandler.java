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
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.lang.NullPointerException;
/**
 * Třída obsahující potřebná data pro aplikaci, navržená jako Singleton.
 *
 * @author OgarXVI
 */
public class DataHandler {

    private int rows;
    /**
     * Vybraná selekční metoda
     */
    private int selectionMethod = 0;
    /**
     *
     * Načtený soubor s daty
     */
    //private File loadedDataFile;
    /**
     * Hlavní terminály tabulky, vychází jako názvy sloupců tabulky, kromě
     * posledního výsledkového
     */
    private String[] params;
    /**
     * Tabulka dat, tedy obsah načteného souboru kromě výsledkového sloupce
     */
    private BigDecimal[][] mathData;
    /**
     * Výsledkový sloupec, slouží pro porovnání vypočtených výsledků.
     */
    private BigDecimal[] expectedResults;
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
    private int[] numberIterationsCategory;
    /**
     * Reference na odkaz
     */
    private static DataHandler instance;

    /**
     * Založení uložiště dat
     */
    private DataHandler() {
        numberIterationsCategory = new int[FunctionCategory.values().length];
        loadedTerminals = new ArrayList<>();
        loadedFunctionsCategories = new ArrayList<>();
        loadedFunctionsCategories.add(new ArrayList<>()); //0
        loadedFunctionsCategories.add(new ArrayList<>()); //1
        loadedFunctionsCategories.add(new ArrayList<>()); //2
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
     * Zpracuje data z načítače souboru na data potřebné pro aplikaci
     *
     * @param data Data z IReader
     */
    public void parseData(String[][] data) {
        //Vyčisti prostor
        params = data[0];
        mathData = new BigDecimal[data.length - 1][params.length - 1];
        expectedResults = new BigDecimal[data.length - 1];
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

    private BigDecimal resultMathData(String[][] data, int i, int j) throws NumberFormatException {
        BigDecimal pomValue;
        try{
            pomValue = BigDecimal.valueOf(Double.valueOf(data[i + 1][j]));
        }catch(NullPointerException | NumberFormatException e){
            FileHandler.getInstance().errorsInFile = true; //registruj chybu
            FileHandler.getInstance().errorRowPositions.add(i);
            return null; //pracujeme se zástupnými znaky
        }
        return pomValue;
    }

    public String[] getParams() {
        return params;
    }

    public BigDecimal[][] getMathData() {
        return mathData;
    }

    public BigDecimal[] getExpectedResults() {
        return expectedResults;
    }

    public boolean isLoaded() {
        return loaded;
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

    public int[] getNumberIterationsCategory() {
        return numberIterationsCategory;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
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
     * @param number kolkrát se bude se funkcema pracovat
     * @param type Jaký typ kategorie to je
     */
    public void AddFunctions(int number, int type) {
        loadedFunctionsCategories.get(type).clear();
        if (number > 0) {
            List<Gen> category = getFunctionsByCategory(type);
            loadedFunctionsCategories.get(type).addAll(category);
        }
        numberIterationsCategory[type] = number;
    }

    /**
     * Vrátí všechny funkce jako položky v poli
     *
     * @return Načtené funkce jako string
     */
    public String getAllFunctionsAsString() {
        String pom = "[";
        for (List<Gen> loadedFunctionsCategory : DataHandler.getInstance().getLoadedFunctionsCategories()) {
            for (Gen gen : loadedFunctionsCategory) {
                if (loadedFunctionsCategory != null) {
                    pom += gen.getCommand() + ",";
                }
            }
        }
        return pom+="]";
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
            //BINARY
            case 0: {
                list.addAll(Function.getSet("+", 2, false));
                list.addAll(Function.getSet("-", 2, false));
                list.addAll(Function.getSet("*", 2, false));
                list.addAll(Function.getSet("/", 2, false));
                list.addAll(Function.getSet("^", 2, false));
                //list.addAll(Function.getSet("min", 2, false));
                //list.addAll(Function.getSet("max", 2, false));
                break;
            }
            //UNARY
            case 1: {
                list.addAll(Function.getSet("sqrt", 1, false));
                //list.addAll(Function.getSet("log2", 1, false));
                //list.addAll(Function.getSet("exp", 1, false));
                list.addAll(Function.getSet("log", 1, false));
                list.addAll(Function.getSet("ln", 1, false));
                break;
            }
            //REST UNARY - TRIGONOMETRIC
            case 2: {
                list.addAll(Function.getSet("sin", 1, false));
                list.addAll(Function.getSet("cos", 1, false));
                list.addAll(Function.getSet("tan", 1, false));
                break;
            }
        }
        return list;
    }

    /**
     * Zjistí, zda byla načtena aspoň jedna funkce v seznamu kategorií funkcí
     *
     * @return
     */
    public boolean isAnyFunctionsLoaded() {
        return loadedFunctionsCategories.stream().anyMatch((loadedFunctionsCategory) -> (!loadedFunctionsCategory.isEmpty()));
    }
    /**
     * Třída sloužící jako box pro data v checkComboBoxu
     */
    public static class BoxDataItem {

        /**
         * List genů, který daný item nese
         */
        private final List<Gen> gens;
        /**
         * Jakou aritu daný gen nese
         */
        private final int arita;
        /**
         * Číselné označení kategorie
         */
        private final int category;

        /**
         * Založí item nesoucí geny
         *
         * @param gens Seznam genů
         */
        public BoxDataItem(List<Gen> gens) {
            this.gens = gens;
            this.arita = 0;
            this.category = 0;
        }

        /**
         * Založí item nesoucí geny a jejich aritu
         *
         * @param gens Seznam genů
         * @param arita Arita genů
         */
        public BoxDataItem(List<Gen> gens, int arita) {
            this.gens = gens;
            this.arita = arita;
            this.category = 0;
        }

        /**
         * Založí item nesoucí geny a jejich aritu
         *
         * @param gens Seznam genů
         * @param arita Arita genů
         * @param category Kategorie
         */
        public BoxDataItem(List<Gen> gens, int arita, int category) {
            this.gens = gens;
            this.arita = arita;
            this.category = category;
        }

        /**
         * Vrátí list genů
         *
         * @return Geny
         */
        public List<Gen> getGens() {
            return gens;
        }

        /**
         * Vrátí číselné označení kategorie funkce
         *
         * @return int
         */
        public int getCategory() {
            return category;
        }

        @Override
        public String toString() {
            return gens.toString() + (category == 0 ? "" : " (Category: " + category + ")");
        }

        /**
         * Vrátí předefinovaný list předmětů do checkComboBoxu
         *
         * @return List funkcí
         */
        public static ObservableList<DataHandler.BoxDataItem> generateFunctionBoxItems() {
            List ol = new ArrayList<>();
            ol.add(new DataHandler.BoxDataItem(Function.getSet("+", 2, false), 2, 1));
            ol.add(new DataHandler.BoxDataItem(Function.getSet("-", 2, false), 2, 1));
            ol.add(new DataHandler.BoxDataItem(Function.getSet("*", 2, false), 2, 1));
            ol.add(new DataHandler.BoxDataItem(Function.getSet("/", 2, false), 2, 1));
            ol.add(new DataHandler.BoxDataItem(Function.getSet("^", 2, false), 2, 1));
            ol.add(new DataHandler.BoxDataItem(Function.getSet("min", 2, true), 2, 1));
            ol.add(new DataHandler.BoxDataItem(Function.getSet("max", 2, true), 2, 1));
            ol.add(new DataHandler.BoxDataItem(Function.getSet("cotg", 1, false), 1, 1));
            ol.add(new DataHandler.BoxDataItem(Function.getSet("sin", 1, false), 1, 1));
            ol.add(new DataHandler.BoxDataItem(Function.getSet("cos", 1, false), 1, 1));
            ol.add(new DataHandler.BoxDataItem(Function.getSet("tan", 1, false), 1, 1));
            ol.add(new DataHandler.BoxDataItem(Function.getSet("sqrt", 1, false), 1, 1));
            ol.add(new DataHandler.BoxDataItem(Function.getSet("abs", 1, false), 1, 1));
            ol.add(new DataHandler.BoxDataItem(Function.getSet("exp", 1, false), 1, 1));
            ol.add(new DataHandler.BoxDataItem(Function.getSet("log2", 1, false), 1, 1));
            ol.add(new DataHandler.BoxDataItem(Function.getSet("log10", 1, false), 1, 1));
            ol.add(new DataHandler.BoxDataItem(Function.getSet("ln", 1, false), 1, 1));
            ol.add(new DataHandler.BoxDataItem(Function.getSet("!", 1, false), 1, 1));

            return FXCollections.observableArrayList(ol);
        }

        /**
         * Vrátí předefinovaný list předmětů do checkComboBoxu
         *
         * @return List terminálů
         */
        public static ObservableList<DataHandler.BoxDataItem> generateTerminalsBoxItems() {
            List ol = new ArrayList<>();
            ol.add(new DataHandler.BoxDataItem(Terminal.getSet("-5")));
            ol.add(new DataHandler.BoxDataItem(Terminal.getSet("-4")));
            ol.add(new DataHandler.BoxDataItem(Terminal.getSet("-3")));
            ol.add(new DataHandler.BoxDataItem(Terminal.getSet("-2")));
            ol.add(new DataHandler.BoxDataItem(Terminal.getSet("-1")));
            ol.add(new DataHandler.BoxDataItem(Terminal.getSet("0")));
            ol.add(new DataHandler.BoxDataItem(Terminal.getSet("1")));
            ol.add(new DataHandler.BoxDataItem(Terminal.getSet("2")));
            ol.add(new DataHandler.BoxDataItem(Terminal.getSet("3")));
            ol.add(new DataHandler.BoxDataItem(Terminal.getSet("4")));
            ol.add(new DataHandler.BoxDataItem(Terminal.getSet("5")));
            ol.add(new DataHandler.BoxDataItem(Terminal.getSet("-5,-4,-3,-2,-1")));
            ol.add(new DataHandler.BoxDataItem(Terminal.getSet("1,2,3,4,5")));
            ol.add(new DataHandler.BoxDataItem(Terminal.getSet("2,4,6,8")));
            ol.add(new DataHandler.BoxDataItem(Terminal.getSet("1,3,5,7,9")));

            return FXCollections.observableArrayList(ol);
        }

    }

    public enum FunctionCategory{
        BINARY(0), UNARY(1), TRIGONOMETRIK(2);
        public final int category;
        private FunctionCategory(int category) {
            this.category = category;
        }
    }
    
}
