package cz.ogarxvi.genetic;

/**
 * Třída pro uchování odkazu na právě vybraný gen. 
 * Zvolený gen je genem nadgenu s indexem podgenu.
 */
public class SelectedGen {
    /**
     * Odkaz na gen nad
     */
    private Gen genAbove;
    /**
     * Index genu
     */
    private int index;
    /**
     * Vrátí gen nad
     * @return Gen nad
     */
    public Gen getGenAbove() {
        return genAbove;
    }
    /**
     * Nastaví gen nad
     * @param genAbove Gen nad
     */
    public void setGenAbove(Gen genAbove) {
        this.genAbove = genAbove;
    }
    /**
     * Vrátí index
     * @return index
     */
    public int genIndex() {
        return index;
    }
    /**
     * Nastaví index
     * @param index index
     */
    public void setIndex(int index) {
        this.index = index;
    }
}
