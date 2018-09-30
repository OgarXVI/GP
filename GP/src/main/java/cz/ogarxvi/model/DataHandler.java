/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.model;

import java.util.Arrays;

/**
 *
 * @author OgarXVI
 */
public class DataHandler {
    
    private String[] params;
    private double[][] mathData;
    private double[] expectedResults;
    private boolean loaded;

    public DataHandler() {
        loaded = false;
    }
    
    
    
    public void parseData(String[][] data){
    //Clear params for data
        params = data[0];
        params = Arrays.copyOf(params, params.length-1);
        //Get mathData for calculation(without results), TODO: should this do before and just once too!
        mathData = new double[data.length-1][params.length];
        //Get Results (F columm)
        expectedResults = new double[data.length-1];
        //Parse data for MathData and Results
        for (int i = 0; i < mathData.length; i++) {
            for (int j = 0; j < mathData[i].length; j++) {
                mathData[i][j] = Double.valueOf(data[i+1][j]);
            }
            expectedResults[i] = Double.valueOf(data[i+1][mathData[i].length]);
        }
        loaded = true;
        /*
        System.out.println("PARAM:");
        System.out.println(Arrays.toString(params));
        System.out.println("MATHDATA:");
        for (double[] math : mathData) {
            System.out.println(Arrays.toString(math));
        }
        System.out.println("RESULTS:");
        System.out.println(Arrays.toString(expectedResults));
        */
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
    
    
    
}
