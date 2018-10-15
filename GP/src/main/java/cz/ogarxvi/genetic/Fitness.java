/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.genetic;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 *
 * @author OgarXVI
 */
public class Fitness {

    private BigDecimal value;

    public Fitness() {
        this.value = new BigDecimal(Long.MAX_VALUE);
    }

    public Fitness(BigDecimal f) {
        this.value = f;
    }

    public void calculate(List<BigDecimal> calcResult, BigDecimal[] expectedResults) {
        BigDecimal val = new BigDecimal(BigInteger.ZERO);        
        
        for (int i = 0; i < expectedResults.length; i++) {
            val = val.add(calcResult.get(i).subtract(expectedResults[i]));
        }
        val = val.divide(BigDecimal.valueOf(calcResult.size()));
        value = val;
    }

    public BigDecimal getValue() {
        return value;
    }

   
    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

}
