    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.model.genetic;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;

/**
 * Prezentuje hodnotu jedince
 * @author OgarXVI
 */
public class Fitness {
    /**
     * Hodnota jedince
     */
    private double value;
    /**
     * Vytvoří Fitness, na začátku jí nastaví na velmi vysokou a necžádoucí 
     * hodnotu, aby nemohlo dojít k ovlivnění výpočtu
     */
    public Fitness() {
        this.value = Double.MAX_VALUE;
    }
    /**
     * Kopírovací konstruktor
     * @param f Originální Fitness
     */
    public Fitness(double f) {
        this.value = f;
        // value = value.round(new MathContext(6)); 
    }
    /**
     * Vypočítá průměrnou odchylku vypočtených výsledků od žádoucích výsledků.
     * 
     * @param calcResult List výsledků programu
     * @param expectedResults Pole výsledků dat 
     */
    public void calculate(List<Double> calcResult, double[] expectedResults) {
        double val = 0d;   
        for (int i = 0; i < calcResult.size(); i++) {
            val += calcResult.get(i) - expectedResults[i];
        }
        val = val / calcResult.size();
        value = val;
    }
    /**
     * Vrátí hodnotu fitness
     * @return Hodnota Fitness
     */
    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

}
