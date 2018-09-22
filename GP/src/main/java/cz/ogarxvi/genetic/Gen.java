/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.genetic;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Třída prezentující prvek ve stromu chromozomu.  
 *
 * @author OgarXVI
 */
public class Gen implements Chromosome<Gen>, Cloneable {

    private final Random random = new Random();

    protected final int[] vector = new int[5];
    protected String instruction; // CO VLASTNĚ GEN PREZENTUJE
    protected List<Gen> gens; // POD GENY
    protected int arity; // POČET ARGUMENTŮ
    protected int depth; // HLOUBKA VE STROMĚ
    protected double value; //HODNOTA GENU, V PŘÍPADĚ FUNKCÍ JDE O VÝSLEDEK FUNKCE 
    /**
     * Returns clone of current chromosome, which is mutated a bit
     */
    @Override
    public Gen mutate() {
        Gen result = this.clone();

        // just select random element of vector
        // and increase or decrease it on small value
        int index = random.nextInt(this.vector.length);
        int mutationValue = random.nextInt(3) - random.nextInt(3);
        result.vector[index] += mutationValue;

        return result;
    }

    /**
     * Returns list of siblings <br/>
     * Siblings are actually new chromosomes, <br/>
     * created using any of crossover strategy
     */
    @Override
    public List<Gen> crossover(Gen other) {
        Gen thisClone = this.clone();
        Gen otherClone = other.clone();

        // one point crossover
        int index = random.nextInt(this.vector.length - 1);
        for (int i = index; i < this.vector.length; i++) {
            int tmp = thisClone.vector[i];
            thisClone.vector[i] = otherClone.vector[i];
            otherClone.vector[i] = tmp;
        }

        return Arrays.asList(thisClone, otherClone);
    }

    @Override
    protected Gen clone() {
        Gen clone = new Gen();
        System.arraycopy(this.vector, 0, clone.vector, 0, this.vector.length);
        return clone;
    }

    public int[] getVector() {
        return this.vector;
    }

    @Override
    public String toString() {
        return Arrays.toString(this.vector);
    }
}
