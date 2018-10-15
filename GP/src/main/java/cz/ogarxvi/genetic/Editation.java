package cz.ogarxvi.genetic;

import cz.ogarxvi.model.DataHandler;
import java.util.ArrayList;
import java.util.List;

/**
 * Třída provádějící editaci, tedy zkrácení určitelných tvarů stromů tak, aby se
 * zachovalo výsledné chování programu, ale zmenšil se počet kroků. Přílišná
 * editace může vést ke zmenšení variablity a tedy menší šance k nalezení
 * potencionálně lepších programů.
 */
public class Editation {

    private List<Gen> treesForReplace;
    private List<Gen> substituteTrees;
    private Gen gen;
    private boolean repeat;
    private DataHandler dataHandler;

    public Editation(DataHandler dh) {
        treesForReplace = new ArrayList<Gen>();
        substituteTrees = new ArrayList<Gen>();

        dataHandler = dh;
        
        //REPLACEMENT
        //Programs with ? * 0 = 0 && 0 * ? = 0 && ? - ? = 0
        /*if (dataHandler != null) {
            if (dataHandler.getParams() != null) {
                String[] paramsTerminalsForReplacement = dataHandler.getParams();
                for (String string : paramsTerminalsForReplacement) {
                    Gen pG = new Gen("*", 2);
                    pG.gens.add(new Terminal(string));
                    pG.gens.add(new Terminal("0"));
                    pG.setIsFunction(true);
                    treesForReplace.add(pG);
                    Gen rG = new Terminal("0");
                    substituteTrees.add(rG);

                    Gen pG2 = new Gen("*", 2);
                    pG2.gens.add(new Terminal("0"));
                    pG2.gens.add(new Terminal(string));
                    pG2.setIsFunction(true);
                    treesForReplace.add(pG2);
                    Gen rG2 = new Terminal("0");
                    substituteTrees.add(rG2);

                    Gen pG3 = new Gen("-", 2);
                    pG3.gens.add(new Terminal(string));
                    pG3.gens.add(new Terminal(string));
                    pG3.setIsFunction(true);
                    treesForReplace.add(pG3);
                    Gen rG3 = new Terminal("0");
                    substituteTrees.add(rG3);
                }

            }
        }
*/
        Gen prog1 = new Gen("+", 2);
        prog1.gens.add(new Terminal("1"));
        prog1.gens.add(new Terminal("1"));
        prog1.setIsFunction(true);
        treesForReplace.add(prog1);
        Gen replaceProg1 = new Terminal("2");
        substituteTrees.add(replaceProg1);

        Gen prog2 = new Gen("+", 3);
        prog2.gens.add(new Terminal("1"));
        prog2.gens.add(new Terminal("1"));
        prog2.gens.add(new Terminal("1"));
        prog2.setIsFunction(true);
        treesForReplace.add(prog1);
        Gen replaceProg2 = new Terminal("3");
        substituteTrees.add(replaceProg2);

        /*
        for (int i = 0; i < 5; i++) {
            Gen prog3 = new Gen("*", 2);
            prog3.gens.add(new Terminal(String.valueOf(i)));
            prog3.gens.add(new Terminal("0"));
            prog3.setIsFunction(true);
            treesForReplace.add(prog3);
            // NAHRAZENÍ PROG1
            Gen replaceProg3 = new Terminal("0");
            substituteTrees.add(replaceProg3);

            Gen prog4 = new Gen("*", 2);
            prog4.gens.add(new Terminal("0"));
            prog4.gens.add(new Terminal(String.valueOf(i)));
            prog4.setIsFunction(true);
            treesForReplace.add(prog3);
            // NAHRAZENÍ PROG1
            Gen replaceProg4 = new Terminal("0");
            substituteTrees.add(replaceProg4);

        }
        */
    }

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
