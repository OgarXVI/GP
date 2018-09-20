/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.genetic;

/**
 * Rozhraní vypočítavající a určující zdatnost chromozonu.
 *
 * @author OgarXVI
 */
public class Fitness<C extends Chromosome<C>, T extends Comparable<T>> {

    private final int[] target = {10, 20, 30, 40, 50};

    public Double calculate(Gen chromosome) {
        double delta = 0;
        int[] v = chromosome.getVector();
        for (int i = 0; i < 5; i++) {
            delta += this.sqr(v[i] - this.target[i]);
        }
        return delta;
    }

    private double sqr(double x) {
        return x * x;
    }

}
