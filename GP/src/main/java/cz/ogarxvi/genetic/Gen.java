package cz.ogarxvi.genetic;

import java.util.ArrayList;
import java.util.List;


/**
 * T��da p�edstavuj�c� geny. Ka�d� uzel stromu je Genem (funkc�, nebo termin�lem - t��dy Funkce a Terminal d�d� od t�to t��dy Gen).
 * Ka�d� gen um� vypsat sv� p��kazy a p��kazy sv�ch podgen� (poduzl�), a to ve tvaru �iteln�m pro u�ivatele a pou�iteln�m pro opakovan� p�ehr�n� �e�en�.
 */
public class Gen {
	
	public static int nejvyssiHloubka = 0;
	
	protected String prikaz;
	protected boolean jeFunkce = false;
	protected int arita;
	protected List<Gen> geny;
	protected int hloubka;
	
	public Gen(String prikaz, int arita) {
		this.prikaz = prikaz;
		this.arita = arita;
		if (arita != 0) geny = new ArrayList<Gen>(arita);
	}
	
	public Gen() {
	}
	
	public Gen(Gen gen)	{	// kop�rovac� konstruktor
		this.prikaz = gen.prikaz;
		this.jeFunkce = gen.jeFunkce;
		this.arita = gen.arita;
		this.hloubka = gen.hloubka;
		if (this.jeFunkce) this.geny = new ArrayList<Gen>(this.arita);
		
		for (int i = 0; i < this.arita; i++) {
			Gen podgen = new Gen(gen.geny.get(i));
			this.geny.add(podgen);
		}
	
	}
	
	public String vypis() {
                switch(arita){
                    case 0: return prikaz;
                        
                    case 1: return prikaz + geny.get(0).vypis();
                        
                    case 2: return prikaz + geny.get(0).vypis() + geny.get(1).vypis();
                        
                    case 3: return prikaz + geny.get(0).vypis() + geny.get(1).vypis() + geny.get(2).vypis();
                        
                    case 4: return prikaz + geny.get(0).vypis() + geny.get(1).vypis() + geny.get(2).vypis() + geny.get(3).vypis();
                        
                }
		return "";
	}
	

	public void opravHloubku() {
		// po k��en� jsou v gen.hloubka nespr�vn� �daje - napr. gen mel drive hloubku 4, po krizeni muze byt ve skutecnosti v hloubce 1 apod.
		
		if (hloubka == 0) {							// pokud je aktu�ln� opravovan� gen ko�en, m� hloubku 0 a nen� t�eba jej opravit
			for (int i = 0; i < geny.size(); i++) {
				geny.get(i).hloubka = 1;			// podgeny ko�enu mus� m�t hloubku 1
				geny.get(i).opravHloubku();
			}
		} else {									// pokud se jedn� o gen hlub�� ne� ko�en (libovoln� podgen)
			if (isJeFunkce()) {
				for (int i = 0; i < geny.size(); i++) {
					geny.get(i).hloubka = hloubka + 1;	// podgeny mus� m�t hloubku o 1 vy��� ne� aktu�ln� gen
					geny.get(i).opravHloubku();
				}
			}
		}	
	}

	
	public int getArita() {
		return arita;
	}

	public void setArita(int arita) {
		this.arita = arita;
	}

	public boolean isJeFunkce() {
		return jeFunkce;
	}

	public void setJeFunkce(boolean jeFunkce) {
		this.jeFunkce = jeFunkce;
	}

	public String getPrikaz() {
		return prikaz;
	}

	public void setPrikaz(String prikaz) {
		this.prikaz = prikaz;
	}
	
	public void setNejvyssiHloubka(Gen gen) {
		Gen.nejvyssiHloubka = 0;
		nejvyssiHloubka(gen);
	}
	
	private void nejvyssiHloubka(Gen gen) {
		if (gen.hloubka > nejvyssiHloubka) nejvyssiHloubka = gen.hloubka;
		for (int i = 0; i < gen.arita; i++) {
			gen.geny.get(i).nejvyssiHloubka(gen.geny.get(i));
		}
	}
	
	public int getHloubka() {
		return hloubka;
	}
	
	public void setHloubka(int hloubka) {
		this.hloubka = hloubka;
	}

}
