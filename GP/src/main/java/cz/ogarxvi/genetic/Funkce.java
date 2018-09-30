package cz.ogarxvi.genetic;

import java.util.ArrayList;
import java.util.List;

/**
 * T��da reprezentuj�c� funkci a d�d�c� od t��dy Gen.
 */
public class Funkce extends Gen {

	public Funkce(String prikaz, int arita, int hloubka, int maximalniHloubka, List<Gen> mnozinaTerminalu, List<Gen> mnozinaFunkci, int metodaInicializace) {
		
		this.geny = new ArrayList<Gen>();
		this.prikaz = prikaz;
		this.arita = arita;
		this.hloubka = hloubka;
		this.jeFunkce = true;
		
		switch (metodaInicializace) {
			case 0 :
				grow(maximalniHloubka, mnozinaTerminalu, mnozinaFunkci, metodaInicializace);
				break;
			case 1 :
				full(maximalniHloubka, mnozinaTerminalu, mnozinaFunkci, metodaInicializace);
				break;
			case 2 :
				ramped(maximalniHloubka, mnozinaTerminalu, mnozinaFunkci, metodaInicializace);
				break;
			default :
				break;
		}
	}
	
	public Funkce(String prikaz, int arita, int hloubka, int maximalniHloubka, List<Gen> mnozinaTerminalu, List<Gen> mnozinaFunkci) {
		this.geny = new ArrayList<Gen>();
		this.prikaz = prikaz;
		this.arita = arita;
		this.hloubka = hloubka;
		this.jeFunkce = true;
	}
	
	public Funkce() {
	}
	
	private void full(int maximalniHloubka, List<Gen> mnozinaTerminalu, List<Gen> mnozinaFunkci, int metodaInicializace) {
		// metoda full - terminaly se pridaji vzdy az v maximalni hloubce
		
		if (hloubka < maximalniHloubka) {
			for (int i = 0; i < arita; i++) {
				
				int nahodneCislo = nahodneKladneCislo(mnozinaFunkci.size());		// nahodne cislo od 0 do poctu funkci
				geny.add(new Funkce(mnozinaFunkci.get(nahodneCislo).getPrikaz(), mnozinaFunkci.get(nahodneCislo).getArita(), hloubka + 1, maximalniHloubka, mnozinaTerminalu, mnozinaFunkci, metodaInicializace));
			}
			
		} else {
			for (int i = 0; i < arita; i++) {
				
				int nahodneCislo = nahodneKladneCislo(mnozinaTerminalu.size());
				geny.add(new Terminal(mnozinaTerminalu.get(nahodneCislo).getPrikaz(), hloubka + 1));
			}

		}
	}
		
	private void grow(int maximalniHloubka, List<Gen> mnozinaTerminalu, List<Gen> mnozinaFunkci, int metodaInicializace) {
			// metoda grow - terminaly se mohou pridat kdykoli, nejpozdeji vsak v maximalni hloubce
			
			if (hloubka < maximalniHloubka) {
				for (int i = 0; i < arita; i++) {
					
					int nahodneCisloF = nahodneKladneCislo(mnozinaFunkci.size());		// nahodne cislo od 0 do poctu funkci
                                        int nahodneCisloT = nahodneKladneCislo(mnozinaTerminalu.size()); //
					int druhGenu = nahodneKladneCislo(2); 								// vygeneruje 0 nebo 1
					if (druhGenu == 0) {
						geny.add(new Funkce(mnozinaFunkci.get(nahodneCisloF).getPrikaz(), mnozinaFunkci.get(nahodneCisloF).getArita(), hloubka + 1, maximalniHloubka, mnozinaTerminalu, mnozinaFunkci, metodaInicializace));
					} else {
						geny.add(new Terminal(mnozinaTerminalu.get(nahodneCisloT).getPrikaz(), hloubka + 1));
					}
				}
				
			} else {
				for (int i = 0; i < arita; i++) {
					
					int nahodneCislo = nahodneKladneCislo(mnozinaTerminalu.size());
					geny.add(new Terminal(mnozinaTerminalu.get(nahodneCislo).getPrikaz(), hloubka + 1));
				}

			}
	}
	
	private void ramped(int maximalniHloubka, List<Gen> mnozinaTerminalu, List<Gen> mnozinaFunkci, int metodaInicializace) {		// vytvori strom nahodne bud metodou "grow" nebo metodou "full"
		double p = nahodneKladneCisloDouble(1);
		if (p < 0.5) {
			grow(maximalniHloubka, mnozinaTerminalu, mnozinaFunkci, metodaInicializace);
		} else {
			full(maximalniHloubka, mnozinaTerminalu, mnozinaFunkci, metodaInicializace);
		}
		

}

	
	public int nahodneKladneCislo(int horniHranice) {		// vraci hodnoty 0 =< hodnota < horniHranice
		return (int)(Math.random() * horniHranice);
	}
	
	public double nahodneKladneCisloDouble(int horniHranice) {
		return Math.random() * horniHranice;
	}
	
}
