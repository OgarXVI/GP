/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.genetic;

import java.util.List;

/**
 * Třída prezentující prvek ve stromu chromozomu. 
 * @author OgarXVI
 */
public class Gen {
    
    protected String instruction; //CO VLASTNĚ GEN PREZENTUJE
    protected List<Gen> gens; // POD GENY
    protected int arity; // POČET ARGUMENTŮ
    protected int depth; // HLOUBKA VE STROMĚ
    
    
}
