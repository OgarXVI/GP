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
    private BigDecimal value;
    /**
     * Vytvoří Fitness, na začátku jí nastaví na velmi vysokou a necžádoucí 
     * hodnotu, aby nemohlo dojít k ovlivnění výpočtu
     */
    public Fitness() {
        this.value = new BigDecimal(Double.MAX_VALUE);
    }
    /**
     * Kopírovací konstruktor
     * @param f Originální Fitness
     */
    public Fitness(BigDecimal f) {
        this.value = f;
        // value = value.round(new MathContext(6)); 
    }
    /**
     * Vypočítá průměrnou odchylku vypočtených výsledků od žádoucích výsledků.
     * 
     * @param calcResult List výsledků programu
     * @param expectedResults Pole výsledků dat 
     */
    public void calculate(List<BigDecimal> calcResult, BigDecimal[] expectedResults) {
        BigDecimal val = new BigDecimal(BigInteger.ZERO);        
        
        for (int i = 0; i < calcResult.size(); i++) {
            if (expectedResults[i] != null)
            val = val.add(calcResult.get(i).subtract(expectedResults[i]));
        }
        val = val.divide(BigDecimal.valueOf(calcResult.size()), 6, RoundingMode.HALF_UP);
        value = val;
    }
    /**
     * Vrátí hodnotu fitness
     * @return Hodnota Fitness
     */
    public BigDecimal getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

}
