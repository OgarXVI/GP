package cz.ogarxvi.genetic;

import java.util.ArrayList;
import java.util.List;


/**
 * Třída představující geny. Každý uzel stromu je genem (fukncí nebo terminálem).
 * Každý gen umí vypsat svůj příkaz a příkazy svých podgenů a te ve tvaru použitelným při výpočtu vzorce.
 */
public class Gen {
	
	public static int maxDepth = 0;
	
	protected String command;
	protected boolean isFunction = false;
	protected int arita;
	protected List<Gen> gens;
	protected int depth;
	
	public Gen(String prikaz, int arita) {
		this.command = prikaz;
		this.arita = arita;
		if (arita != 0) gens = new ArrayList<Gen>(arita);
	}
	
	public Gen() {
	}
	
	public Gen(Gen gen)	{	
		this.command = gen.command;
		this.isFunction = gen.isFunction;
		this.arita = gen.arita;
		this.depth = gen.depth;
		if (this.isFunction) this.gens = new ArrayList<Gen>(this.arita);
		
		for (int i = 0; i < this.arita; i++) {
			Gen podgen = new Gen(gen.gens.get(i));
			this.gens.add(podgen);
		}
	
	}
	
	public String print() {
                switch(arita){
                    case 0: return command;
                        
                    case 1: return command + gens.get(0).print();
                        
                    case 2: return command + gens.get(0).print() + gens.get(1).print();
                        
                    case 3: return command + gens.get(0).print() + gens.get(1).print() + gens.get(2).print();
                        
                    case 4: return command + gens.get(0).print() + gens.get(1).print() + gens.get(2).print() + gens.get(3).print();
                        
                }
		return "";
	}
	

	public void fixDepth() {
	
		if (depth == 0) {							
			for (int i = 0; i < gens.size(); i++) {
				gens.get(i).depth = 1;			
				gens.get(i).fixDepth();
			}
		} else {									
			if (isFunction()) {
				for (int i = 0; i < gens.size(); i++) {
					gens.get(i).depth = depth + 1;	
					gens.get(i).fixDepth();
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

	public boolean isFunction() {
		return isFunction;
	}

	public void setIsFunction(boolean jeFunkce) {
		this.isFunction = jeFunkce;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String prikaz) {
		this.command = prikaz;
	}
	
	public void setMaxDepth(Gen gen) {
		Gen.maxDepth = 0;
		getMaxDepth(gen);
	}
	
	private void getMaxDepth(Gen gen) {
		if (gen.depth > maxDepth) maxDepth = gen.depth;
		for (int i = 0; i < gen.arita; i++) {
			gen.gens.get(i).getMaxDepth(gen.gens.get(i));
		}
	}
	
	public int getDepth() {
		return depth;
	}
	
	public void setDepth(int depth) {
		this.depth = depth;
	}

    @Override
    public String toString() {
        return command;
    }

        
        
}
