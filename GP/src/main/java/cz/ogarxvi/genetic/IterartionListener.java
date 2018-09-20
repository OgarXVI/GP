/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.genetic;

public interface IterartionListener<C extends Chromosome<C>, T extends Comparable<T>> {

    void update(GeneticAlgorithm<C, T> environment);

}
