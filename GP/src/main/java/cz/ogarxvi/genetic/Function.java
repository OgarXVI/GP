package cz.ogarxvi.genetic;

import java.util.ArrayList;
import java.util.List;

/**
 * Třída prezentující funkci, tedy gen s určitou aritou.
 */
public class Function extends Gen {
    /**
     * Vytvoří funkci, aplikuje se v případech naprosto jisté funkce a známých listů terminálů a funkcí (tedy hlavně při založení)
     * @param command Příkaz
     * @param arita Arita
     * @param depth Hloubka
     * @param maxDepth Maximální hloubka
     * @param listOfTerminals List terminálů
     * @param listOfFunctions List Funkcí
     */
    public Function(String command, int arita, int depth, int maxDepth, List<Gen> listOfTerminals, List<Gen> listOfFunctions) {

        this.gens = new ArrayList<>();
        this.command = command;
        this.arita = arita;
        this.depth = depth;
        this.isFunction = true;

        grow(maxDepth, listOfTerminals, listOfFunctions);

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

                int randomF = getRandomNumber(setOfFunctions.size());
                int randomT = getRandomNumber(setOfTerminals.size());
                int typeG = getRandomNumber(2);
                if (typeG == 0) {
                    gens.add(new Function(setOfFunctions.get(randomF).getCommand(), setOfFunctions.get(randomF).getArita(), depth + 1, maxDepth, setOfTerminals, setOfFunctions));
                } else {
                    gens.add(new Terminal(setOfTerminals.get(randomT).getCommand(), depth + 1));
                }
            }

        } else {
            for (int i = 0; i < arita; i++) {
                int nahodneCislo = getRandomNumber(setOfTerminals.size());
                gens.add(new Terminal(setOfTerminals.get(nahodneCislo).getCommand(), depth + 1));
            }

        }
    }
    /**
     * Vrátí náhodné číslo v intervalu
     * @param limit limita
     * @return Náhodné číslo v intervalu
     */
    private int getRandomNumber(int limit) {
        return (int) (Math.random() * limit);
    }
    /**
     * Vytvoří list genů podle zadaných parametrů
     * @param command String příkazů
     * @param arita Arita genů
     * @return List vytvořených genů
     */
    public static List<Gen> getSet(String command, int arita) {
        List<Gen> pomListGens = new ArrayList<>();
        String[] functions = command.split(",");
        for (String f : functions) {
            Gen gF = new Gen(f, arita);
            gF.setIsFunction(true);
            pomListGens.add(gF);
        }
        return pomListGens;

    }

    
    
}
