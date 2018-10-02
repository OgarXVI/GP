package cz.ogarxvi.genetic;

import java.util.ArrayList;
import java.util.List;

/**
 * Třída prezentujcí terminál.  
*/
public class Terminal extends Gen {

    public Terminal(String command) {
        this.command = command;
        this.arita = 0;
        this.isFunction = false;
    }

    public Terminal(String command, int depth) {
        this.command = command;
        this.arita = 0;
        this.depth = depth;
        this.isFunction = false;
    }

    public static List<Gen> getSet(String command){
        List<Gen> pomListGens = new ArrayList<>();
        String[] terminals = command.split(",");
        for (String terminal : terminals) {
            pomListGens.add(new Terminal(terminal));
        }        
        return pomListGens;
    }
    
}
