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

    public Editation(DataHandler dh) {
        treesForReplace = new ArrayList<Gen>();
        substituteTrees = new ArrayList<Gen>();
        
        String[] params = dh.getParams();
        
        for (String param : params) {
            Gen prog = new Gen("*", 2);
            prog.gens.add(new Terminal(param));
            prog.gens.add(new Terminal("1"));
            prog.setIsFunction(true);
            treesForReplace.add(prog);
            Gen replaceProg1 = new Terminal(param);
            substituteTrees.add(replaceProg1);
            
            Gen prog1 = new Gen("*", 2);
            prog1.gens.add(new Terminal("1"));
            prog1.gens.add(new Terminal(param));
            prog1.setIsFunction(true);
            treesForReplace.add(prog1);
            Gen replaceProg2 = new Terminal(param);
            substituteTrees.add(replaceProg2);
            
            Gen prog3 = new Gen("+", 2);
            prog3.gens.add(new Terminal(param));
            prog3.gens.add(new Terminal("0"));
            prog3.setIsFunction(true);
            treesForReplace.add(prog3);
            Gen replaceProg3 = new Terminal(param);
            substituteTrees.add(replaceProg3);
            
            Gen prog4 = new Gen("+", 2);
            prog4.gens.add(new Terminal("0"));
            prog4.gens.add(new Terminal(param));
            prog4.setIsFunction(true);
            treesForReplace.add(prog4);
            Gen replaceProg4 = new Terminal(param);
            substituteTrees.add(replaceProg4);
            
            Gen prog5 = new Gen("-", 2);
            prog5.gens.add(new Terminal(param));
            prog5.gens.add(new Terminal(param));
            prog5.setIsFunction(true);
            treesForReplace.add(prog5);
            Gen replaceProg5 = new Terminal("0");
            substituteTrees.add(replaceProg5);
            
            Gen prog6 = new Gen("/", 2);
            prog6.gens.add(new Terminal(param));
            prog6.gens.add(new Terminal("1"));
            prog6.setIsFunction(true);
            treesForReplace.add(prog6);
            Gen replaceProg6 = new Terminal(param);
            substituteTrees.add(replaceProg6);
            
            Gen prog7 = new Gen("/", 2);
            prog7.gens.add(new Terminal(param));
            prog7.gens.add(new Terminal(param));
            prog7.setIsFunction(true);
            treesForReplace.add(prog7);
            Gen replaceProg7 = new Terminal("1");
            substituteTrees.add(replaceProg7);
        }
        
        
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
        
        Gen prog7 = new Gen("*", 2);
        prog7.gens.add(new Terminal("-1"));
        prog7.gens.add(new Terminal("-1"));
        prog7.setIsFunction(true);
        treesForReplace.add(prog7);
        Gen replaceProg7 = new Terminal("1");
        substituteTrees.add(replaceProg7);
        
        Gen prog8 = new Gen("*", 2);
        prog8.gens.add(new Terminal("1"));
        prog8.gens.add(new Terminal("-1"));
        prog8.setIsFunction(true);
        treesForReplace.add(prog8);
        Gen replaceProg8 = new Terminal("-1");
        substituteTrees.add(replaceProg8);
        
        Gen prog9 = new Gen("*", 2);
        prog9.gens.add(new Terminal("-1"));
        prog9.gens.add(new Terminal("1"));
        prog9.setIsFunction(true);
        treesForReplace.add(prog9);
        Gen replaceProg9 = new Terminal("-1");
        substituteTrees.add(replaceProg9);
        
        Gen prog10 = new Gen("+", 2);
        prog10.gens.add(new Terminal("1"));
        prog10.gens.add(new Terminal("0"));
        prog10.setIsFunction(true);
        treesForReplace.add(prog10);
        Gen replaceProg10 = new Terminal("1");
        substituteTrees.add(replaceProg10);
        
        Gen prog11 = new Gen("+", 2);
        prog11.gens.add(new Terminal("0"));
        prog11.gens.add(new Terminal("1"));
        prog11.setIsFunction(true);
        treesForReplace.add(prog11);
        Gen replaceProg11 = new Terminal("1");
        substituteTrees.add(replaceProg11);

        Gen prog12 = new Gen("-", 2);
        prog12.gens.add(new Terminal("0"));
        prog12.gens.add(new Terminal("1"));
        prog12.setIsFunction(true);
        treesForReplace.add(prog12);
        Gen replaceProg12 = new Terminal("-1");
        substituteTrees.add(replaceProg12);

        Gen prog13 = new Gen("+", 2);
        prog13.gens.add(new Terminal("1"));
        prog13.gens.add(new Terminal("0"));
        prog13.setIsFunction(true);
        treesForReplace.add(prog13);
        Gen replaceProg13 = new Terminal("1");
        substituteTrees.add(replaceProg13);

        
    }
    /**
     * Upraví kořen genu
     * @param g Kořen
     * @return Vrátí upravený kořen
     */
    public Gen editRoot(Gen g) {
        this.gen = g;
        editReapeable();
        this.gen.fixDepth();
        return this.gen;
    }
    /**
     * Opakuj editaci
     */
    private void editReapeable() {
        editGen(this.gen);
    }
    /**
     * Edituje kořen, vnitřní metoda
     * @param g Editovaný kořen
     */
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
    /**
     * Edituje
     * @param gen editovaný kořen
     * @return Vrací editovaný kořen
     */
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
