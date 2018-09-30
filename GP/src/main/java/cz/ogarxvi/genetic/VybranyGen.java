package cz.ogarxvi.genetic;

/**
 * T��da pro uchov�n� odkazu na vybran� gen. Zvolen� gen je genem Nadgenu s
 * indexem IndexPodgenu.
 */
public class VybranyGen {

    private Gen nadgen;
    private int indexPodgenu;

    public VybranyGen() {
    }

    public Gen getNadgen() {
        return nadgen;
    }

    public void setNadgen(Gen nadgen) {
        this.nadgen = nadgen;
    }

    public int getIndexPodgenu() {
        return indexPodgenu;
    }

    public void setIndexPodgenu(int indexPodgenu) {
        this.indexPodgenu = indexPodgenu;
    }
}
