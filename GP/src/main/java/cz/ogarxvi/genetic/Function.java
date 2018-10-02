package cz.ogarxvi.genetic;

import java.util.ArrayList;
import java.util.List;

/**
 * Třída prezentující funkci, tedy gen s určitou aritou.
 */
public class Function extends Gen {

    public Function(String command, int arita, int depth, int maxDepth, List<Gen> setOfTerminals, List<Gen> setOfFunctions) {

        this.gens = new ArrayList<Gen>();
        this.command = command;
        this.arita = arita;
        this.depth = depth;
        this.isFunction = true;

        grow(maxDepth, setOfTerminals, setOfFunctions);

    }

    public Function() {
    }

    /**
     * Metoda grow - terminaly se mohou pridat kdykoli, nejpozdeji vsak v
     * maximalni hloubce
     *
     * @param maxDepth - maximální hloubka stromu
     * @param setOfTerminals - využitelná množina terminálů
     * @param setOfFunctions - využitelná množina funkcí
     */
    private void grow(int maxDepth, List<Gen> setOfTerminals, List<Gen> setOfFunctions) {

        if (depth < maxDepth) {
            for (int i = 0; i < arita; i++) {

                int nahodneCisloF = getRandomNumber(setOfFunctions.size());
                int nahodneCisloT = getRandomNumber(setOfTerminals.size());
                int druhGenu = getRandomNumber(2);
                if (druhGenu == 0) {
                    gens.add(new Function(setOfFunctions.get(nahodneCisloF).getCommand(), setOfFunctions.get(nahodneCisloF).getArita(), depth + 1, maxDepth, setOfTerminals, setOfFunctions));
                } else {
                    gens.add(new Terminal(setOfTerminals.get(nahodneCisloT).getCommand(), depth + 1));
                }
            }

        } else {
            for (int i = 0; i < arita; i++) {

                int nahodneCislo = getRandomNumber(setOfTerminals.size());
                gens.add(new Terminal(setOfTerminals.get(nahodneCislo).getCommand(), depth + 1));
            }

        }
    }

    private int getRandomNumber(int limit) {
        return (int) (Math.random() * limit);
    }

    private double getRandomDouble(int limit) {
        return Math.random() * limit;
    }

    public static List<Gen> getSet(String command, int arita) {
        List<Gen> pomListGens = new ArrayList<>();
        String[] functions = command.split(",");
        for (String f : functions) {
            pomListGens.add(new Gen(f, arita));
        }
        return pomListGens;

    }

    
    
}
