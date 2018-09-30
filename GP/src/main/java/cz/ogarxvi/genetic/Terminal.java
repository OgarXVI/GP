package cz.ogarxvi.genetic;

/**
 * T��da reprezentuj�c� termin�l a d�d�c� od t��dy Gen.
 */
public class Terminal extends Gen {

    public Terminal(String prikaz) {
        this.prikaz = prikaz;
        this.arita = 0;
        this.jeFunkce = false;
    }

    public Terminal(String prikaz, int hloubka) {
        this.prikaz = prikaz;
        this.arita = 0;
        this.hloubka = hloubka;
        this.jeFunkce = false;
    }

}
