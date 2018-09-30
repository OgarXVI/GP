/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.genetic;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 *
 * @author OgarXVI
 */
public class Fitness {

    private double value;

    public Fitness() {
        this.value = Double.MAX_VALUE;
    }

    public Fitness(double f) {
        this.value = f;
    }

    public void calculate(String formula, String[] params, double[][] mathData, double[] expectedResults) {
        double val = 0d;
        //Prepare engine for calculate formula
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");
        //Edit formula and calculate formula's results
        double[] calcResult = new double[expectedResults.length];
        for (int i = 0; i < expectedResults.length; i++) {
            String pomF = formula;
            for (int j = 0; j < params.length; j++) {
                pomF = pomF.replace(params[j], String.valueOf(mathData[i][j]));
            }
            try {
                //WUT
                calcResult[i] = new Double(String.valueOf(engine.eval(pomF)));
                //TODO: FOR TESTING
                System.out.println(pomF + " = " + calcResult[i] + "(" + expectedResults[i] + ")");
            } catch (ScriptException ex) {
                Logger.getLogger(Fitness.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        //Compare expectedResults and calcResults and return final fitness
        for (int i = 0; i < calcResult.length; i++) {
            val += (calcResult[i] - expectedResults[i]);
        }
        val = val/calcResult.length;
        
        value = val;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

}
