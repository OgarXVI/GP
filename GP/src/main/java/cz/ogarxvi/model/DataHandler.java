/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.model;

import cz.ogarxvi.genetic.Chromosome;
import cz.ogarxvi.genetic.Function;
import cz.ogarxvi.genetic.Gen;
import cz.ogarxvi.genetic.Terminal;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Třída obsahující potřebná data pro aplikaci.
 * @author OgarXVI
 */
public class DataHandler {
    /**
     * Hlavní terminály tabulky, vychází jako názvy sloupců tabulky, kromě posledního výsledkového
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
    private List<Gen> loadedFunctions;
    /**
     * List načtených terminálů
     */
    private List<Gen> loadedTerminals;
    /**
     * Definuje, zda se má výpočet GA zastavit
     */
    private boolean gpStop;
    /**
     * Pomocná proměnná, vybraný nejlepší jedinec
     */
    private Chromosome bestChromosome;
    /**
     * Založení uložiště dat
     */
    public DataHandler() {
        loaded = false;
        loadedFunctions = new ArrayList<>();
        loadedTerminals = new ArrayList<>();
    }
    /**
     * Zpracuje data z načítače souboru na data potřebné pro aplikaci
     * @param data Data z IReader
     */
    public void parseData(String[][] data) {
        //Vyčisti prostor
        params = data[0];
        mathData = new BigDecimal[data.length-1][params.length-1];
        expectedResults = new BigDecimal[data.length-1];
        //Načti data pro tabulku a sloupec
        for (int i = 0; i < mathData.length; i++) {
            for (int j = 0; j < mathData[i].length; j++) {
                mathData[i][j] = BigDecimal.valueOf(Double.valueOf(data[i + 1][j]));
            }
            expectedResults[i] = BigDecimal.valueOf(Double.valueOf(data[i + 1][mathData[i].length]));
        }
        //uprav správně hlavičky na terminály, odstraň poslední sloupec
        params = Arrays.copyOf(params, params.length-1);
        // DataHandler byl načten
        loaded = true;
        // přidá hlavičky jako terminály
        loadParamsAsTerminals();
        /*
        System.out.println("PARAM:");
        System.out.println(Arrays.toString(params));
        System.out.println("MATHDATA:");
        for (BigDecimal[] math : mathData) {
            System.out.println(Arrays.toString(math));
        }
        System.out.println("RESULTS:");
        System.out.println(Arrays.toString(expectedResults));
        */
    }
    /**
     * Vrátí pole načtených hlaviček
     * @return Hlavičky tabulky
     */
    public String[] getParams() {
        return params;
    }
    /**
     * Vrátí dvojrozměrné pole načtených dat
     * @return Načtená data
     */
    public BigDecimal[][] getMathData() {
        return mathData;
    }
    /**
     * Vrátí sloupec načtených výsledků.
     * @return Načtené výsledky
     */
    public BigDecimal[] getExpectedResults() {
        return expectedResults;
    }
    /**
     * Vrátí, zda je vše načteno.
     * @return Načtený stav
     */
    public boolean isLoaded() {
        return loaded;
    }
    /**
     * Vrátí list načtených funkcí
     * @return Načtené funkce
     */
    public List<Gen> getLoadedFunctions() {
        return loadedFunctions;
    }
    /**
     * Vrátí list načtených terminálů
     * @return Načtené terminály
     */
    public List<Gen> getLoadedTerminals() {
        return loadedTerminals;
    }
    /**
     * Vrátí, zda je výpočet GA zastaven
     * @return GP zastaveno
     */
    public boolean isGpStop() {
        return gpStop;
    }
    /**
     * Nastaví, zda je GP zastaveno
     * @param gpStop Zastavit GP
     */
    public void setGpStop(boolean gpStop) {
        this.gpStop = gpStop;
    }
    /**
     * Vrátí nejlepšího jedince z výpočtu
     * @return Nejlepší jedinec
     */
    public Chromosome getBestChromosome() {
        return bestChromosome;
    }
    /**
     * Nastaví nejlepšího jedince
     * @param bestChromosome Nejlepší jedinec ve výpočtu
     */
    public void setBestChromosome(Chromosome bestChromosome) {
        this.bestChromosome = bestChromosome;
    }
    /**
     * Načte hlavičky jako terminály
     */
    public void loadParamsAsTerminals() {
        if (isLoaded()) {
            String[] paramsDH = this.getParams();
            for (String string : paramsDH) {
                loadedTerminals.add(new Terminal(string));
            }
        }
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
         * Založí item nesoucí geny 
         * @param gens Seznam genů
         */
        public BoxDataItem(List<Gen> gens) {
            this.gens = gens;
            this.arita = 0;
        }
        /**
         * Založí item nesoucí geny a jejich aritu
         * @param gens Seznam genů
         * @param arita Arita genů
         */
        public BoxDataItem(List<Gen> gens, int arita) {
            this.gens = gens;
            this.arita = arita;
        }
        /**
         * Vrátí list genů
         * @return Geny
         */
        public List<Gen> getGens() {
            return gens;
        }

        @Override
        public String toString() {
            return gens.toString() + (arita == 0 ? "" : " (Arita: " + arita + ")");
        }
        /**
         * Vrátí předefinovaný list předmětů do checkComboBoxu
         * @return  List funkcí
         */
        public static ObservableList<DataHandler.BoxDataItem> generateFunctionBoxItems() {
            List ol = new ArrayList<>();
            ol.add(new DataHandler.BoxDataItem(Function.getSet("+", 2), 2));
            ol.add(new DataHandler.BoxDataItem(Function.getSet("-", 2), 2));
            ol.add(new DataHandler.BoxDataItem(Function.getSet("*", 2), 2));
            ol.add(new DataHandler.BoxDataItem(Function.getSet("/", 2), 2));
            ol.add(new DataHandler.BoxDataItem(Function.getSet("sin", 1), 1));
            ol.add(new DataHandler.BoxDataItem(Function.getSet("cos", 1), 1));
            ol.add(new DataHandler.BoxDataItem(Function.getSet("tan", 1), 1));
            ol.add(new DataHandler.BoxDataItem(Function.getSet("sqrt", 1), 1));
            ol.add(new DataHandler.BoxDataItem(Function.getSet("abs", 1), 1));
            ol.add(new DataHandler.BoxDataItem(Function.getSet("exp", 1), 1));
            ol.add(new DataHandler.BoxDataItem(Function.getSet("log", 1), 1));
            ol.add(new DataHandler.BoxDataItem(Function.getSet("!", 1), 1));

            return FXCollections.observableArrayList(ol);
        }
        /**
         * Vrátí předefinovaný list předmětů do checkComboBoxu
         * @return  List terminálů
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

}
