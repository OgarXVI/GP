package cz.ogarxvi.genetic;

/**
 * Třída pro uchování odkazu na právě vybraný gen. Zvolený gen je genem nadgenu s indexem podgenu.
 * T��da pro uchov�n� odkazu na vybran� gen. Zvolen� gen je genem Nadgenu s
 * indexem IndexPodgenu.
 */
public class SelectedGen {

    private Gen genAbove;
    private int index;

    public SelectedGen() {
    }

    public Gen getGenAbove() {
        return genAbove;
    }

    public void setGenAbove(Gen genAbove) {
        this.genAbove = genAbove;
    }

    public int genIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
