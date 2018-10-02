package cz.ogarxvi.genetic;

import java.util.ArrayList;
import java.util.List;

/**
 * Třída provádějící editaci, tedy zkrácení určitelných tvarů stromů tak, aby se zachovalo výsledné chování programu, ale zmenšil se počet kroků.
 * Přílišná editace může vést ke zmenšení variablity a tedy menší šance k nalezení potencionálně lepších programů.
 */
public class Editation {
	
	private List<Gen> treesForReplace;
	private List<Gen> substituteTrees;
	private Gen gen;
	private boolean repeat;
	
	public Editation() {
		treesForReplace = new ArrayList<Gen>();
		substituteTrees = new ArrayList<Gen>();
                
                //SEZNAM NÁHRADNÍCH STROMŮ
                
		// PROG1 - 1 + 1 = 2
		Gen strom1 = new Gen("+", 2);
		strom1.gens.add(new Terminal("1"));
		strom1.gens.add(new Terminal("1"));
		strom1.setIsFunction(true);
		treesForReplace.add(strom1);
		// NAHRAZENÍ PROG1
		Gen nahradniStrom1 = new Terminal("2");
		substituteTrees.add(nahradniStrom1);
		
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
		//g = removeStop(g);
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
			
			if (podgen.getCommand().equals(treesForReplace.get(i).getCommand()) &&
                                podgen.getArita() == treesForReplace.get(i).getArita()) {
					for (int j = 0; j < podgen.getArita(); j++) {	
						if (! podgen.gens.get(j)
                                                        .getCommand().equals(treesForReplace.get(i)
                                                                        .gens.get(j).getCommand())) {
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
	/*
	public Gen removeStop(Gen gen) {
		
	  for (int d = 0; d < gen.arita; d++) {
		Gen podgen = gen.geny.get(d);
		
		switch (podgen.prikaz) {
		
		case "IF-FOOD-AHEAD" :
			// IF-FOOD-AHEAD(STOP, X) -> IF-FOOD-AHEAD(MOVE, X)
			if (podgen.geny.get(0).prikaz.equals("STOP")) {
				podgen.geny.set(0, new Terminal("MOVE"));
				repeat = true;
				return gen;
			}
			
			// IF-FOOD-AHEAD(X, STOP) -> IF-FOOD-AHEAD(X, STOP -> n�hodn� z LEFT, RIGHT, MOVE)
			if (podgen.geny.get(1).prikaz.equals("STOP")) {
				double nahoda = Math.random();
				if (nahoda < 1/3) podgen.geny.set(1, new Terminal("MOVE"));
				if ((nahoda >= 1/3) && (nahoda <= 2/3)) podgen.geny.set(1, new Terminal("LEFT"));
				if (nahoda > 2/3) podgen.geny.set(1, new Terminal("RIGHT"));
				repeat = true;
				return gen;
			}
			
			break;
		
		case "PROGN2" :
			// PROGN2(STOP, X), X nemus� b�t termin�l, ale funkce
			if (podgen.geny.get(0).prikaz.equals("STOP")) {
				gen.geny.set(d, podgen.geny.get(1));
				repeat = true;
				return gen;
			}
			
			// PROGN2(X, STOP), X nemus� b�t termin�l, ale funkce
			if (podgen.geny.get(1).prikaz.equals("STOP")) {
				gen.geny.set(d, podgen.geny.get(0));
				repeat = true;
				return gen;
			}
			break;
		
		case "PROGN3" :
			// PROGN3(STOP, STOP, X) -> X
			if (podgen.geny.get(0).prikaz.equals("STOP") && podgen.geny.get(1).prikaz.equals("STOP")) {
				gen.geny.set(d, podgen.geny.get(2));
				repeat = true;
				return gen;
			}
			
			// PROGN3(STOP, X, STOP) -> X
			if (podgen.geny.get(0).prikaz.equals("STOP") && podgen.geny.get(2).prikaz.equals("STOP")) {
				gen.geny.set(d, podgen.geny.get(1));
				repeat = true;
				return gen;
			}
			
			// PROGN3(X, STOP, STOP) -> X
			if (podgen.geny.get(1).prikaz.equals("STOP") && podgen.geny.get(2).prikaz.equals("STOP")) {
				gen.geny.set(d, podgen.geny.get(0));
				repeat = true;
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
				repeat = true;
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
				repeat = true;
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
				repeat = true;
				return gen;
			}
			
			break;
		
		case "STOP" :
			
			switch (gen.prikaz) {
			
			case "IF-FOOD-AHEAD" :
					gen.geny.set(d, new Terminal("MOVE"));
					repeat = true;
					return gen;
			
			case "PROGN2" :
					gen.geny.set(d, new Terminal("MOVE"));
					repeat = true;
					return gen;
			
			case "PROGN3" :
					gen.geny.set(d, new Terminal("MOVE"));
					repeat = true;
					return gen;
			
		default :
			break;
		}
	   }
	  }	
	  return gen;
	}*/
}
