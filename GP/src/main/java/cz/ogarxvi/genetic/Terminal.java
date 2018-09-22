/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.genetic;

/**
 * Třída prezentující terminál, tedy genotyp bez argumentů, proměnné či
 * konstanty.
 *
 * @author OgarXVI
 */
public class Terminal extends Gen{
    
    public Terminal(String instruc){
        this.instruction = instruc;
        this.arity = 0;
    }
    
    public Terminal(String instruc, int depth){
        this.instruction = instruc;
        this.arity = 0;
        this.depth = depth;
    }

}
