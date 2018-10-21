package cz.ogarxvi.genetic;

import cz.ogarxvi.model.DataHandler;
import java.util.ArrayList;
import java.util.List;

/**
 * Třída provádějící editaci, tedy zkrácení určitelných tvarů stromů tak, aby se
 * zachovalo výsledné chování programu, ale zmenšil se výsledný vzorec. Přílišná
 * editace může vést ke zmenšení variablity a tedy menší šance k nalezení
 * potencionálně lepších programů.
 * Nahrazování funguje na nalezení přesné kopie předem nadefinovaného stromu a 
 * jeho nahrazení předem deklarovaným jiným stromem, popř. jedním terminálem.
 * 2.10.2018 - Momentálně je optimalizace nastavena na false.
 */
public class Editation {
    /**
     * List stromů, které se nahradí
     */
    private List<Gen> treesForReplace;
    /**
     * List stromů, které nahrazují
     */
    private List<Gen> substituteTrees;
    /**
     * Gen pro nahrazení
     */
    private Gen gen;
    /**
     * Opakovat v případě nalezení shody
     */
    private boolean repeat;
    /**
     * Vytvoří instanci pro editaci stromů
     */
    public Editation() {
        treesForReplace = new ArrayList<Gen>();
        substituteTrees = new ArrayList<Gen>();
        //Založení vrchního genu
        Gen prog1 = new Gen("+", 2);
        prog1.gens.add(new Terminal("1"));
        prog1.gens.add(new Terminal("1"));
        prog1.setIsFunction(true);
        treesForReplace.add(prog1);
        Gen replaceProg1 = new Terminal("2");
        substituteTrees.add(replaceProg1);

        Gen prog2 = new Gen("*", 2);
        prog2.gens.add(new Terminal("0"));
        prog2.gens.add(new Terminal("0"));
        prog2.setIsFunction(true);
        treesForReplace.add(prog2);
        Gen replaceProg2 = new Terminal("0");
        substituteTrees.add(replaceProg2);
        
        Gen prog3 = new Gen("*", 2);
        prog3.gens.add(new Terminal("1"));
        prog3.gens.add(new Terminal("1"));
        prog3.setIsFunction(true);
        treesForReplace.add(prog3);
        Gen replaceProg3 = new Terminal("1");
        substituteTrees.add(replaceProg3);
        
        Gen prog4 = new Gen("*", 2);
        prog4.gens.add(new Terminal("0"));
        prog4.gens.add(new Terminal("1"));
        prog4.setIsFunction(true);
        treesForReplace.add(prog4);
        Gen replaceProg4 = new Terminal("0");
        substituteTrees.add(replaceProg4);
        
        Gen prog5 = new Gen("*", 2);
        prog5.gens.add(new Terminal("1"));
        prog5.gens.add(new Terminal("0"));
        prog5.setIsFunction(true);
        treesForReplace.add(prog5);
        Gen replaceProg5 = new Terminal("0");
        substituteTrees.add(replaceProg5);
        
        Gen prog6 = new Gen("/", 2);
        prog6.gens.add(new Terminal("1"));
        prog6.gens.add(new Terminal("1"));
        prog6.setIsFunction(true);
        treesForReplace.add(prog5);
        Gen replaceProg6 = new Terminal("1");
        substituteTrees.add(replaceProg5);

    }
    /**
     * 
     * @param g
     * @return 
     */
    public Gen editRoot(Gen g) {
        this.gen = g;
        editReapeable();
        this.gen.fixDepth();
        return this.gen;
    }

    private void editReapeable() {
        editGen(this.gen);
    }

    private void editGen(Gen g) {

        repeat = false;
        g = edit(g);
        if (repeat) {
            editReapeable();
            return;
        }
        if (repeat) {
            editReapeable();
            return;
        }

        for (int i = 0; i < g.getArita(); i++) {
            editGen(g.gens.get(i));
        }
    }

    public Gen edit(Gen gen) {

        for (int d = 0; d < gen.arita; d++) {
            Gen podgen = gen.gens.get(d);

            for (int i = 0; i < treesForReplace.size(); i++) {

                boolean replace = true;

                if (podgen.getCommand().equals(treesForReplace.get(i).getCommand())
                        && podgen.getArita() == treesForReplace.get(i).getArita()) {
                    for (int j = 0; j < podgen.getArita(); j++) {
                        if (!podgen.gens.get(j)
                                .getCommand().equals(treesForReplace.get(i).gens.get(j).getCommand())) {
                            replace = false;
                        }
                    }

                    if (replace) {
                        gen.gens.set(d, (substituteTrees.get(i)));
                        repeat = true;
                        return gen;
                    }
                }
            }
        }
        return gen;
    }

}
