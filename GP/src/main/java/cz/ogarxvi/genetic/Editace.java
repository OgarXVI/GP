package cz.ogarxvi.genetic;

import java.util.ArrayList;
import java.util.List;

/**
 * T��da pro prov�d�n� editace, tedy zkr�cen� ur�it�ch tvar� strom� tak, aby se zachovalo p�vodn� chov�n� programu, ale u�et�il se po�et krok�.
 */
public class Editace {
	
	private List<Gen> stromyProNahrazeni;
	private List<Gen> nahradniStromy;
	private Gen gen;
	private boolean opakuj;
	
	public Editace() {
		stromyProNahrazeni = new ArrayList<Gen>();
		nahradniStromy = new ArrayList<Gen>();
                
                //SEZNAM NÁHRADNÍCH STROMŮ
                
		// PROG1 - 1 + 1 = 2
		Gen strom1 = new Gen("+", 2);
		strom1.geny.add(new Terminal("1"));
		strom1.geny.add(new Terminal("1"));
		strom1.setJeFunkce(true);
		stromyProNahrazeni.add(strom1);
		// NAHRAZENÍ PROG1
		Gen nahradniStrom1 = new Terminal("2");
		nahradniStromy.add(nahradniStrom1);
		/*
		// PROGN3(RIGHT, RIGHT, RIGHT) -> LEFT
		Gen strom2 = new Gen("PROGN3", 3);
		strom2.geny.add(new Terminal("RIGHT"));
		strom2.geny.add(new Terminal("RIGHT"));
		strom2.geny.add(new Terminal("RIGHT"));
		strom2.setJeFunkce(true);
		stromyProNahrazeni.add(strom2);
		
		Gen nahradniStrom2 = new Terminal("LEFT");
		nahradniStromy.add(nahradniStrom2);
		
		// PROGN2(LEFT, RIGHT)
		Gen strom3 = new Gen("PROGN2", 2);
		strom3.geny.add(new Terminal("LEFT"));
		strom3.geny.add(new Terminal("RIGHT"));
		strom3.setJeFunkce(true);
		stromyProNahrazeni.add(strom3);
		
		Gen nahradniStrom3 = new Terminal("STOP");
		nahradniStromy.add(nahradniStrom3);
		
		// PROGN2(RIGHT, LEFT)
		Gen strom4 = new Gen("PROGN2", 2);
		strom4.geny.add(new Terminal("RIGHT"));
		strom4.geny.add(new Terminal("LEFT"));
		strom4.setJeFunkce(true);
		stromyProNahrazeni.add(strom4);
		
		Gen nahradniStrom4 = new Terminal("STOP");
		nahradniStromy.add(nahradniStrom4);
		
		// PROGN3(LEFT, RIGHT, MOVE)
		Gen strom5 = new Gen("PROGN3", 3);
		strom5.geny.add(new Terminal("LEFT"));
		strom5.geny.add(new Terminal("RIGHT"));
		strom5.geny.add(new Terminal("MOVE"));
		strom5.setJeFunkce(true);
		stromyProNahrazeni.add(strom5);
		
		Gen nahradniStrom5 = new Terminal("MOVE");
		nahradniStromy.add(nahradniStrom5);
		
		// PROGN3(RIGHT, LEFT, MOVE)
		Gen strom6 = new Gen("PROGN3", 3);
		strom6.geny.add(new Terminal("RIGHT"));
		strom6.geny.add(new Terminal("LEFT"));
		strom6.geny.add(new Terminal("MOVE"));
		strom6.setJeFunkce(true);
		stromyProNahrazeni.add(strom6);
		
		Gen nahradniStrom6 = new Terminal("MOVE");
		nahradniStromy.add(nahradniStrom6);
		
		// PROGN3(MOVE, RIGHT, LEFT)
		Gen strom7 = new Gen("PROGN3", 3);
		strom7.geny.add(new Terminal("MOVE"));
		strom7.geny.add(new Terminal("RIGHT"));
		strom7.geny.add(new Terminal("LEFT"));
		strom7.setJeFunkce(true);
		stromyProNahrazeni.add(strom7);
		
		Gen nahradniStrom7 = new Terminal("MOVE");
		nahradniStromy.add(nahradniStrom7);
		
		// PROGN3(MOVE, LEFT, RIGHT)
		Gen strom8 = new Gen("PROGN3", 3);
		strom8.geny.add(new Terminal("MOVE"));
		strom8.geny.add(new Terminal("LEFT"));
		strom8.geny.add(new Terminal("RIGHT"));
		strom8.setJeFunkce(true);
		stromyProNahrazeni.add(strom8);
		
		Gen nahradniStrom8 = new Terminal("MOVE");
		nahradniStromy.add(nahradniStrom8);
		
		// PROGN3(LEFT, RIGHT, RIGHT) -> RIGHT
		Gen strom9 = new Gen("PROGN3", 3);
		strom9.geny.add(new Terminal("LEFT"));
		strom9.geny.add(new Terminal("RIGHT"));
		strom9.geny.add(new Terminal("RIGHT"));
		strom9.setJeFunkce(true);
		stromyProNahrazeni.add(strom9);
		
		Gen nahradniStrom9 = new Terminal("RIGHT");
		nahradniStromy.add(nahradniStrom9);
		
		// PROGN3(LEFT, RIGHT, LEFT) -> LEFT
		Gen strom10 = new Gen("PROGN3", 3);
		strom10.geny.add(new Terminal("LEFT"));
		strom10.geny.add(new Terminal("RIGHT"));
		strom10.geny.add(new Terminal("LEFT"));
		strom10.setJeFunkce(true);
		stromyProNahrazeni.add(strom10);
		
		Gen nahradniStrom10 = new Terminal("LEFT");
		nahradniStromy.add(nahradniStrom10);
		
		// PROGN3(LEFT, LEFT, RIGHT) -> LEFT
		Gen strom11 = new Gen("PROGN3", 3);
		strom11.geny.add(new Terminal("LEFT"));
		strom11.geny.add(new Terminal("LEFT"));
		strom11.geny.add(new Terminal("RIGHT"));
		strom11.setJeFunkce(true);
		stromyProNahrazeni.add(strom11);
		
		Gen nahradniStrom11 = new Terminal("LEFT");
		nahradniStromy.add(nahradniStrom11);
		
		// PROGN3(RIGHT, RIGHT, LEFT) -> RIGHT
		Gen strom12 = new Gen("PROGN3", 3);
		strom12.geny.add(new Terminal("RIGHT"));
		strom12.geny.add(new Terminal("RIGHT"));
		strom12.geny.add(new Terminal("LEFT"));
		strom12.setJeFunkce(true);
		stromyProNahrazeni.add(strom12);
		
		Gen nahradniStrom12 = new Terminal("RIGHT");
		nahradniStromy.add(nahradniStrom12);
		
		// PROGN3(RIGHT, LEFT, RIGHT) -> RIGHT
		Gen strom13 = new Gen("PROGN3", 3);
		strom13.geny.add(new Terminal("RIGHT"));
		strom13.geny.add(new Terminal("LEFT"));
		strom13.geny.add(new Terminal("RIGHT"));
		strom13.setJeFunkce(true);
		stromyProNahrazeni.add(strom13);
		
		Gen nahradniStrom13 = new Terminal("RIGHT");
		nahradniStromy.add(nahradniStrom13);
		
		// PROGN3(RIGHT, LEFT, LEFT) -> LEFT
		Gen strom14 = new Gen("PROGN3", 3);
		strom14.geny.add(new Terminal("RIGHT"));
		strom14.geny.add(new Terminal("LEFT"));
		strom14.geny.add(new Terminal("LEFT"));
		strom14.setJeFunkce(true);
		stromyProNahrazeni.add(strom14);
		
		Gen nahradniStrom14 = new Terminal("LEFT");
		nahradniStromy.add(nahradniStrom14);
		*/
	}
	

	public Gen editujKoren(Gen g) {
		this.gen = g;
		opakovaneEdituj();
		this.gen.opravHloubku();
		return this.gen;
	}
	
	private void opakovaneEdituj() {
		editujGen(this.gen);
	}
	
	private void editujGen(Gen g) {
		
		opakuj = false;
		g = edituj(g);
		if (opakuj) {
			opakovaneEdituj();
			return;
		}
		g = odstranStop(g);
		if (opakuj) {
			opakovaneEdituj();
			return;
		}
		
		for (int i = 0; i < g.getArita(); i++) {
			editujGen(g.geny.get(i));
		}
	}
	

	public Gen edituj(Gen gen) {
		
	  for (int d = 0; d < gen.arita; d++) {
		Gen podgen = gen.geny.get(d);
		
		for (int i = 0; i < stromyProNahrazeni.size(); i++) {	// pro v�echny stromyProNahrazeni prvn� hloubky (ko�en tohoto stromu)
			
			boolean nahradit = true;
			
			if (podgen.getPrikaz().equals(stromyProNahrazeni.get(i).getPrikaz()) &&
                                podgen.getArita() == stromyProNahrazeni.get(i).getArita()) {
				// pokud je p��kaz obdr�en�ho genu stejn� jako p��kaz stromu
					
					for (int j = 0; j < podgen.getArita(); j++) {	// pro v�echny podgeny obdr�en�ho genu
						if (! podgen.geny.get(j)
                                                        .getPrikaz().equals(
                                                                stromyProNahrazeni.get(i)
                                                                        .geny.get(j).getPrikaz())) {
							nahradit = false;
						}
					}
					
					if (nahradit) {
						gen.geny.set(d, (nahradniStromy.get(i)));
						opakuj = true;
						return gen;
					}
			}
		}
	  }
	  return gen;
	}
	

	// zjist�, jestli gen nen� funkc� obsahuj�c� STOP:
	public Gen odstranStop(Gen gen) {
		
	  for (int d = 0; d < gen.arita; d++) {
		Gen podgen = gen.geny.get(d);
		
		switch (podgen.prikaz) {
		
		case "IF-FOOD-AHEAD" :
			// IF-FOOD-AHEAD(STOP, X) -> IF-FOOD-AHEAD(MOVE, X)
			if (podgen.geny.get(0).prikaz.equals("STOP")) {
				podgen.geny.set(0, new Terminal("MOVE"));
				opakuj = true;
				return gen;
			}
			
			// IF-FOOD-AHEAD(X, STOP) -> IF-FOOD-AHEAD(X, STOP -> n�hodn� z LEFT, RIGHT, MOVE)
			if (podgen.geny.get(1).prikaz.equals("STOP")) {
				double nahoda = Math.random();
				if (nahoda < 1/3) podgen.geny.set(1, new Terminal("MOVE"));
				if ((nahoda >= 1/3) && (nahoda <= 2/3)) podgen.geny.set(1, new Terminal("LEFT"));
				if (nahoda > 2/3) podgen.geny.set(1, new Terminal("RIGHT"));
				opakuj = true;
				return gen;
			}
			
			break;
		
		case "PROGN2" :
			// PROGN2(STOP, X), X nemus� b�t termin�l, ale funkce
			if (podgen.geny.get(0).prikaz.equals("STOP")) {
				gen.geny.set(d, podgen.geny.get(1));
				opakuj = true;
				return gen;
			}
			
			// PROGN2(X, STOP), X nemus� b�t termin�l, ale funkce
			if (podgen.geny.get(1).prikaz.equals("STOP")) {
				gen.geny.set(d, podgen.geny.get(0));
				opakuj = true;
				return gen;
			}
			break;
		
		case "PROGN3" :
			// PROGN3(STOP, STOP, X) -> X
			if (podgen.geny.get(0).prikaz.equals("STOP") && podgen.geny.get(1).prikaz.equals("STOP")) {
				gen.geny.set(d, podgen.geny.get(2));
				opakuj = true;
				return gen;
			}
			
			// PROGN3(STOP, X, STOP) -> X
			if (podgen.geny.get(0).prikaz.equals("STOP") && podgen.geny.get(2).prikaz.equals("STOP")) {
				gen.geny.set(d, podgen.geny.get(1));
				opakuj = true;
				return gen;
			}
			
			// PROGN3(X, STOP, STOP) -> X
			if (podgen.geny.get(1).prikaz.equals("STOP") && podgen.geny.get(2).prikaz.equals("STOP")) {
				gen.geny.set(d, podgen.geny.get(0));
				opakuj = true;
				return gen;
			}
			
			// PROGN3(STOP, X, X) -> PROGN2(X, X)
			if (podgen.geny.get(0).prikaz.equals("STOP")) {
				Gen podgen1 = podgen.geny.get(1);
				Gen podgen2 = podgen.geny.get(2);
				Gen novyGen = new Gen("PROGN2", 2);
				novyGen.geny.add(podgen1);
				novyGen.geny.add(podgen2);
				novyGen.setJeFunkce(true);
				gen.geny.set(d, novyGen);
				opakuj = true;
				return gen;
			}
			
			// PROGN3(X, STOP, X) -> PROGN2(X, X)
			if (podgen.geny.get(1).prikaz.equals("STOP")) {
				Gen podgen1 = podgen.geny.get(0);
				Gen podgen2 = podgen.geny.get(2);
				Gen novyGen = new Gen("PROGN2", 2);
				novyGen.geny.add(podgen1);
				novyGen.geny.add(podgen2);
				novyGen.setJeFunkce(true);
				gen.geny.set(d, novyGen);
				opakuj = true;
				return gen;
			}
			
			// PROGN3(X, X, STOP) -> PROGN2(X, X)
			if (podgen.geny.get(2).prikaz.equals("STOP")) {
				Gen podgen1 = podgen.geny.get(0);
				Gen podgen2 = podgen.geny.get(1);
				Gen novyGen = new Gen("PROGN2", 2);
				novyGen.geny.add(podgen1);
				novyGen.geny.add(podgen2);
				novyGen.setJeFunkce(true);
				gen.geny.set(d, novyGen);
				opakuj = true;
				return gen;
			}
			
			break;
		
		case "STOP" :
			
			switch (gen.prikaz) {
			
			case "IF-FOOD-AHEAD" :
					gen.geny.set(d, new Terminal("MOVE"));
					opakuj = true;
					return gen;
			
			case "PROGN2" :
					gen.geny.set(d, new Terminal("MOVE"));
					opakuj = true;
					return gen;
			
			case "PROGN3" :
					gen.geny.set(d, new Terminal("MOVE"));
					opakuj = true;
					return gen;
			
		default :
			break;
		}
	   }
	  }	
	  return gen;
	}
}
