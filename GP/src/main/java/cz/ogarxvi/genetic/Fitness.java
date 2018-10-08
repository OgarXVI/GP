/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.genetic;

import java.util.List;

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

    public void calculate(List<Double> calcResult, double[] expectedResults) {
        double val = 0;        
        
        for (int i = 0; i < expectedResults.length; i++) {
            val += (calcResult.get(i) - expectedResults[i]);
        }
        val = val / calcResult.size();

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
