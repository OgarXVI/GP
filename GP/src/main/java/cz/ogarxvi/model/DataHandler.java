/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.model;

import cz.ogarxvi.genetic.Function;
import cz.ogarxvi.genetic.Gen;
import cz.ogarxvi.genetic.Terminal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

/**
 *
 * @author OgarXVI
 */
public class DataHandler {

    private String[] params;
    private double[][] mathData;
    private double[] expectedResults;
    private boolean loaded;

    private List<Gen> loadedFunctions;
    private List<Gen> loadedTerminals;

    private boolean gpStop;

    public DataHandler() {
        loaded = false;
        loadedFunctions = new ArrayList<>();
        loadedTerminals = new ArrayList<>();
    }

    public void parseData(String[][] data) {
        //Clear params for data
        params = data[0];
        params = Arrays.copyOf(params, params.length - 1);
        //Get mathData for calculation(without results), 
        mathData = new double[data.length - 1][params.length];
        //Get Results (F columm)
        expectedResults = new double[data.length - 1];
        //Parse data for MathData and Results
        for (int i = 0; i < mathData.length; i++) {
            for (int j = 0; j < mathData[i].length; j++) {
                mathData[i][j] = Double.valueOf(data[i + 1][j]);
            }
            expectedResults[i] = Double.valueOf(data[i + 1][mathData[i].length]);
        }
        loaded = true;

        loadParamsAsTerminals();

        System.out.println("PARAM:");
        System.out.println(Arrays.toString(params));
        System.out.println("MATHDATA:");
        for (double[] math : mathData) {
            System.out.println(Arrays.toString(math));
        }
        System.out.println("RESULTS:");
        System.out.println(Arrays.toString(expectedResults));

    }

    public String[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }

    public double[][] getMathData() {
        return mathData;
    }

    public void setMathData(double[][] mathData) {
        this.mathData = mathData;
    }

    public double[] getExpectedResults() {
        return expectedResults;
    }

    public void setMathResults(double[] expectedResults) {
        this.expectedResults = expectedResults;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public List<Gen> getLoadedFunctions() {
        return loadedFunctions;
    }

    public List<Gen> getLoadedTerminals() {
        return loadedTerminals;
    }

    public boolean isGpStop() {
        return gpStop;
    }

    public void setGpStop(boolean gpStop) {
        this.gpStop = gpStop;
    }

    public void loadParamsAsTerminals() {
        if (isLoaded()) {
            //VARIABLES
            String[] paramsDH = this.getParams();
            for (String string : paramsDH) {
                loadedTerminals.add(new Terminal(string));
            }
        }
    }

    public static class BoxDataItem {

        private List<Gen> gens;
        private int arita;
        private boolean selected;

        public BoxDataItem(List<Gen> gens) {
            this.gens = gens;
            this.arita = -1;
            this.selected = false;
        }

        public BoxDataItem(List<Gen> gens, int arita) {
            this.gens = gens;
            this.arita = arita;
            this.selected = false;
        }

        public List<Gen> getGens() {
            return gens;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        @Override
        public String toString() {
            return gens.toString() + (arita == -1 ? "" : " (Arita: " + arita + ")");
        }

        public static ObservableList<DataHandler.BoxDataItem> generateFunctionBoxItems() {
            List ol = new ArrayList<>();
            ol.add(new DataHandler.BoxDataItem(Function.getSet("+,-,*", 2), 2));
            ol.add(new DataHandler.BoxDataItem(Function.getSet("-,*,+", 3), 3));
            ol.add(new DataHandler.BoxDataItem(Function.getSet("+,-,*,/", 2), 2));
            return FXCollections.observableArrayList(ol);
        }

        public static ObservableList<DataHandler.BoxDataItem> generateTerminalsBoxItems() {
            List ol = new ArrayList<>();
            ol.add(new DataHandler.BoxDataItem(Terminal.getSet("0")));
            ol.add(new DataHandler.BoxDataItem(Terminal.getSet("1")));
            ol.add(new DataHandler.BoxDataItem(Terminal.getSet("0,1,2")));
            ol.add(new DataHandler.BoxDataItem(Terminal.getSet("0,1,2,3,4,5")));
            ol.add(new DataHandler.BoxDataItem(Terminal.getSet("2,4,6,8")));
            ol.add(new DataHandler.BoxDataItem(Terminal.getSet("1,3,5,7,9")));

            return FXCollections.observableArrayList(ol);
        }

    }

}
