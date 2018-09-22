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
public interface Chromosome<C extends Chromosome<C>> {

    List<C> crossover(C anotherChromosome);

    C mutate();

}
