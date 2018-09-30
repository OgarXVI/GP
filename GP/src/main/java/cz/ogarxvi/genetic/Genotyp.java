package cz.ogarxvi.genetic;

import java.util.List;

/**
 * T��da pro uchov�v�n� jedinc� (genotyp�) v populaci. Ka�d� genotyp m�
 * vypo��tanou vlastn� zdatnost (po�et nalezen�ch n�vnad) a obsahuje ko�en
 * cel�ho stromu p�edstavuj�c�ho konkr�tn� �e�en�.
 */
public class Genotyp {

    private Gen koren;
    private Fitness fitness;

    public Genotyp(int maximalniHloubka, List<Gen> mnozinaTerminalu, List<Gen> mnozinaFunkci, int metodaInicializace) {
        int nahodneCislo = nahodneKladneCislo(mnozinaFunkci.size());
        koren = new Funkce(mnozinaFunkci.get(nahodneCislo).getPrikaz(), mnozinaFunkci.get(nahodneCislo).getArita(), 0, maximalniHloubka, mnozinaTerminalu, mnozinaFunkci, metodaInicializace);
        fitness = new Fitness();
    }

    public Genotyp(Genotyp genotyp) {	// kop�rovac� konstruktor
        this.koren = new Gen(genotyp.koren);
        this.fitness = new Fitness(genotyp.fitness.getValue());
    }

    public Fitness getFitness() {
        return fitness;
    }

    public void setZdatnost(Fitness fitness) {
        this.fitness = fitness;
    }

    public int nahodneKladneCislo(int horniHranice) {		// vraci hodnoty 0 =< hodnota < horniHranice
        return (int) (Math.random() * horniHranice);
    }

    public Gen getKoren() {
        return koren;
    }

    public void setKoren(Funkce koren) {
        this.koren = koren;
    }

    public void setKoren(Gen koren) {
        this.koren = koren;
    }

}
