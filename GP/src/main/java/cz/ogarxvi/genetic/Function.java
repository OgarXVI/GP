/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.genetic;

import java.util.List;

/**
 * Třída prezentující funkci, tedy genotypy s argumenty.
 *
 * @author OgarXVI
 */
public class Function extends Gen{
    
    public Function(String instruction, List<Gen> children, int numberOfArguments){
        this.instruction = instruction;
        this.gens = children;
        this.arity = numberOfArguments;
    }
}
