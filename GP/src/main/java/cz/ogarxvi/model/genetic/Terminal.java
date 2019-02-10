package cz.ogarxvi.model.genetic;

import java.util.ArrayList;
import java.util.List;

/**
 * Třída prezentujcí terminál.  
*/
public class Terminal extends Gen {
    /**
     * Vytvoří instanci Terminálu
     * @param command Příkaz představující terminál
     */
    public Terminal(String command) {
        this.command = command;
        this.arita = 0;
        this.isFunction = false;
    }
    /**
     * Vytvoří instanci Terminálů s hloubkou vyšší než nula
     * @param command Příkaz představující terminál
     * @param depth Hloubka
     */
    public Terminal(String command, int depth) {
        this.command = command;
        this.arita = 0;
        this.depth = depth;
        this.isFunction = false;
    }
    /**
     * Vytvoří a vrátí list terimnálů na základě zadaných parametrů
     * @param command Seznam příkazů
     * @return List terminálů
     */
    public static List<Gen> getSet(String command){
        List<Gen> pomListGens = new ArrayList<>();
        String[] terminals = command.split(",");
        for (String terminal : terminals) {
            pomListGens.add(new Terminal(terminal));
        }        
        return pomListGens;
    }
    
}
