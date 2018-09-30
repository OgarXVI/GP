package cz.ogarxvi.genetic;

import cz.ogarxvi.model.DataHandler;
import cz.ogarxvi.model.Messenger;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Button;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

public class GenetickyAlgoritmus {

    private DataHandler dataHandler;
    private Messenger messenger;
    private List<Genotyp> populace;
    private Button buttonZastavit;
    private boolean stop = false;
    private Editace editace = new Editace();
    private double pravdepodobnostKrizeniVUzluFunkce = 0.00;

    public GenetickyAlgoritmus(Button buttonZastavit) {
        // TODO - dodělat button zastavit, aby nastavilo stop = true
    }

    public GenetickyAlgoritmus(Button stopButton, Messenger m, DataHandler dh) {
        messenger = m;
        dataHandler = dh;
    }

    public void provadejGenetickyAlgoritmus(int pocetGeneraci, int velikostPocatecniPopulace, int maximalniInicializacniHloubkaStromu, int maximalniHloubkaStromuPoKrizeni, double reprodukce, double krizeni, double mutace, double krizeniVUzluFunkce, boolean zachovavatNejzdatnejsihoJedince, boolean decimace, boolean boolEditace, int pocetKroku, int metodaInicializace) {

        stop = false;
        pravdepodobnostKrizeniVUzluFunkce = krizeniVUzluFunkce;

        List<Gen> mnozinaTerminalu = new ArrayList<Gen>();
        List<Gen> mnozinaFunkci = new ArrayList<Gen>();

        Gen a = new Terminal("X");
        Gen b = new Terminal("Y");
        Gen c = new Terminal("Z");
        Gen const1 = new Terminal("1");
        Gen const2 = new Terminal("2");
        Gen const5 = new Terminal("5");

        mnozinaTerminalu.add(a);
        mnozinaTerminalu.add(b);
        mnozinaTerminalu.add(c);
        mnozinaTerminalu.add(const1);
        mnozinaTerminalu.add(const2);
        mnozinaTerminalu.add(const5);

        Gen plus2 = new Gen("+", 2);
        Gen plus3 = new Gen("+", 3);
        Gen plus4 = new Gen("+", 4);
        Gen minus2 = new Gen("-", 2);
        Gen minus3 = new Gen("-", 3);
        Gen krat2 = new Gen("*", 2);
        Gen deleno2 = new Gen("/", 2);

        mnozinaFunkci.add(plus2);
        mnozinaFunkci.add(plus3);
        mnozinaFunkci.add(plus3);
        mnozinaFunkci.add(plus3);
        mnozinaFunkci.add(plus4);
        mnozinaFunkci.add(plus4);
        mnozinaFunkci.add(minus2);
        mnozinaFunkci.add(minus3);
        mnozinaFunkci.add(krat2);
        mnozinaFunkci.add(deleno2);

        populace = new ArrayList<Genotyp>(velikostPocatecniPopulace);

        int pocetReprodukci = (int) (reprodukce * velikostPocatecniPopulace);
        int pocetKrizeni = (int) (krizeni * velikostPocatecniPopulace) / 2;		// /2 - po 1 krizeni vzniknou 2 jedinci
        int pocetMutaci = (int) (mutace * velikostPocatecniPopulace);

        while ((pocetReprodukci + (pocetKrizeni * 2) + pocetMutaci) < velikostPocatecniPopulace) {
            pocetReprodukci++;
        }

        if (zachovavatNejzdatnejsihoJedince) {
            pocetReprodukci = pocetReprodukci - 1;
        }

        // DECIMACE - parametry:
        int generaceDecimace = 10;
        int pocetJedincuPredDecimaci = 10 * velikostPocatecniPopulace;

        // DECIMACE - inicializa�n� populace
        if (decimace) {
            populace = new ArrayList<Genotyp>(pocetJedincuPredDecimaci);

            pocetReprodukci = (int) (reprodukce * pocetJedincuPredDecimaci);
            pocetKrizeni = (int) (krizeni * pocetJedincuPredDecimaci) / 2;		// /2 - po 1 krizeni vzniknou 2 jedinci
            pocetMutaci = (int) (mutace * pocetJedincuPredDecimaci);

            while ((pocetReprodukci + (pocetKrizeni * 2) + pocetMutaci) < pocetJedincuPredDecimaci) {
                pocetReprodukci++;
            }

            if (zachovavatNejzdatnejsihoJedince) {
                pocetReprodukci = pocetReprodukci - 1;
            }

            vytvorPocatecniPopulaci(mnozinaTerminalu, mnozinaFunkci, maximalniInicializacniHloubkaStromu, pocetJedincuPredDecimaci, metodaInicializace);

        } else {
            vytvorPocatecniPopulaci(mnozinaTerminalu, mnozinaFunkci, maximalniInicializacniHloubkaStromu, velikostPocatecniPopulace, metodaInicializace);
        }

        Genotyp nejlepsiGenotyp = new Genotyp(populace.get(0));

        for (int i = 0; i < pocetGeneraci; i++) {		// pro kazdou generaci
            if (stop) {
                break;
            }

            Genotyp nejlepsiGenotypVGeneraci = new Genotyp(populace.get(0));

            // hodnot�c� funkce, ka�d�mu genotypu p�i�ad� zdatnost
            for (int j = 0; j < populace.size(); j++) {	// pro kazdeho jedince v populaci

                if (boolEditace) {
                    populace.get(j).setKoren(editace.editujKoren(populace.get(j).getKoren()));	// editace ko�enu
                }

                // TODO - nastavit každému genotypu jeho zdatnost => vypočítat to
                // vzít genotyp, rozparsovat ho na vzoreček, do kterýho jde dosadit čísla za proměnný a vypočítat ho,
                // pak dosadit čísla do proměnných z tabulky (pro všechny řádky v tabulce), určit pro každý ten řádek výsledek,
                // porovnat pro každý ten řádek výsledek se sloupcem F, a rozdíl bude hrubá zdatnost. Tu převedeme na standardizovanou
                // (tzn. čím víc se blíží nule, tím je to lepší), a tu zapíšeme jako zdatnost. 
                // TODO - dále v kódu udělat, aby byla jako lepší zdatnost braná ta nižší (ideálně nula), néé ta vyšší, jako to bylo!!
                //messenger.AddMesseage("P: " + j + " " + populace.get(j).getKoren().vypis());
                //Generace: " + i + " Jedinec:" + j + "   " + 
                String pomFormula = messenger.preToInfix(populace.get(j).getKoren().vypis());
                double originFitness = populace.get(j).getFitness().getValue(); // !! TODO - není to tady nějaký blbě? Jaká je ta originální hodnota? (Možná to není třeba řešit.)
                populace.get(j).getFitness().calculate(pomFormula, dataHandler.getParams(), dataHandler.getMathData(), dataHandler.getExpectedResults());
                //messenger.AddMesseage("G: " + i + " J: " + j + "  " + populace.get(j).getKoren().vypis());
                //TODO: Upravit z prefix na infix, který snad bude lépe počítatelný pro PC?
                // messenger.AddMesseage("Jeho úprava je:   " + messenger.preToInfix(populace.get(j).getKoren().vypis()));
                
                if (Math.abs(populace.get(j).getFitness().getValue()) < Math.abs(nejlepsiGenotyp.getFitness().getValue())) {
                    nejlepsiGenotyp = new Genotyp(populace.get(j));
                }
                
                if (Math.abs(populace.get(j).getFitness().getValue()) < Math.abs(nejlepsiGenotypVGeneraci.getFitness().getValue())) {
                    nejlepsiGenotypVGeneraci = new Genotyp(populace.get(j));
                }
            }
            
            messenger.AddMesseage(nejlepsiGenotypVGeneraci.getFitness().getValue() + "  " + messenger.preToInfix(nejlepsiGenotypVGeneraci.getKoren().vypis()) );
            messenger.GetMesseage();
            // ukončovací podmínka - když je zdatnost nejlepšího jedince 0, ukončíme algoritmus
            if (nejlepsiGenotypVGeneraci.getFitness().getValue() == 0.00) {
                stop = true;
                messenger.AddMesseage("JAJ");
                messenger.GetMesseage();
                return;
            }
            
            

            // DECIMACE - provedeni
            if (decimace && (i == (generaceDecimace - 1))) {				// decimace v n-t� generaci

                // zp�sob A: v�b�r jedinc� turnajovou selekc�
                List<Genotyp> populacePoDecimaci = new ArrayList<Genotyp>(velikostPocatecniPopulace);
                for (int k = 0; k < velikostPocatecniPopulace; k++) {
                    Genotyp vybranyGenotyp = turnajovaSelekce3();
                    populacePoDecimaci.add(new Genotyp(vybranyGenotyp));
                    populace.remove(vybranyGenotyp);
                }

                pocetReprodukci = (int) (reprodukce * velikostPocatecniPopulace);
                pocetKrizeni = (int) (krizeni * velikostPocatecniPopulace) / 2;		// /2 - po 1 krizeni vzniknou 2 jedinci
                pocetMutaci = (int) (mutace * velikostPocatecniPopulace);

                while ((pocetReprodukci + (pocetKrizeni * 2) + pocetMutaci) < velikostPocatecniPopulace) {
                    pocetReprodukci++;
                }

                if (zachovavatNejzdatnejsihoJedince) {
                    pocetReprodukci = pocetReprodukci - 1;
                }

                populace = populacePoDecimaci;

            }

            List<Genotyp> novaPopulace = new ArrayList<Genotyp>();

            // zachov�n� nejlep��ho jedince v populaci
            if (zachovavatNejzdatnejsihoJedince) {
                Genotyp nejlepsiGenotyp2 = new Genotyp(nejlepsiGenotyp);
                novaPopulace.add(nejlepsiGenotyp2);
            }

            // reprodukce
            for (int j = 0; j < pocetReprodukci; j++) {

                Genotyp vybranyGenotyp = turnajovaSelekce3();
                novaPopulace.add(vybranyGenotyp);
            }

            // k��en�
            for (int j = 0; j < pocetKrizeni; j++) {

                Genotyp vybranyGenotyp1 = turnajovaSelekce3();
                Genotyp vybranyGenotyp2 = turnajovaSelekce3();

                VybranyGen vybranyGen1 = nahodnyGen(vybranyGenotyp1);
                VybranyGen vybranyGen2 = nahodnyGen(vybranyGenotyp2);

                vybranyGen1.getNadgen().setHloubka(0);
                vybranyGen2.getNadgen().setHloubka(0);

                vybranyGen1.getNadgen().opravHloubku();
                vybranyGen2.getNadgen().opravHloubku();

                vybranyGen1.getNadgen().setNejvyssiHloubka(vybranyGen1.getNadgen());
                int maximalniHloubkaNadgenu1 = Gen.nejvyssiHloubka;

                vybranyGen2.getNadgen().setNejvyssiHloubka(vybranyGen2.getNadgen());
                int maximalniHloubkaNadgenu2 = Gen.nejvyssiHloubka;

                if ((maximalniHloubkaNadgenu1 + maximalniHloubkaNadgenu2) <= maximalniHloubkaStromuPoKrizeni) {

                    Gen gen1 = vybranyGen1.getNadgen().geny.get(vybranyGen1.getIndexPodgenu());
                    Gen gen2 = vybranyGen2.getNadgen().geny.get(vybranyGen2.getIndexPodgenu());

                    vybranyGen1.getNadgen().geny.set(vybranyGen1.getIndexPodgenu(), gen2);
                    vybranyGen2.getNadgen().geny.set(vybranyGen2.getIndexPodgenu(), gen1);

                    vybranyGenotyp1.getKoren().opravHloubku();
                    vybranyGenotyp2.getKoren().opravHloubku();

                    novaPopulace.add(vybranyGenotyp1);
                    novaPopulace.add(vybranyGenotyp2);

                } else {
                    j--;
                }
            }

            // mutace:
            for (int j = 0; j < pocetMutaci; j++) {

                Genotyp vybranyGenotyp = turnajovaSelekce3();

                VybranyGen vybranyGen = nahodnyGen(vybranyGenotyp);

                Gen gen = vybranyGen.getNadgen().geny.get(vybranyGen.getIndexPodgenu());

                // gen se zm�n� �pln� - v�etn� jeho podgen�.
                if ((gen.hloubka - 1) == maximalniHloubkaStromuPoKrizeni) {	// termin�l lze nahradit jen termin�lem, pokud dos�hneme maxim�ln� hloubky
                    gen = mnozinaTerminalu.get(nahodneKladneCislo(3));
                } else {
                    Gen f = mnozinaFunkci.get(nahodneKladneCislo(3));
                    gen = new Funkce(f.prikaz, f.arita, gen.hloubka, maximalniHloubkaStromuPoKrizeni, mnozinaTerminalu, mnozinaFunkci, metodaInicializace);
                }

                vybranyGen.getNadgen().geny.set(vybranyGen.getIndexPodgenu(), gen);
                vybranyGenotyp.getKoren().opravHloubku();

                novaPopulace.add(vybranyGenotyp);
            }

            
            
            populace = novaPopulace;			// nahrazen� p�vodn� populace novou populac� (generac�)
        }
        messenger.GetAllMesseages();
    }

    /**
     * Metoda vracej�c� z genotypu n�hodn� gen jako nadgen a pro n�j index
     * vybran�ho genu. Jako nadgen m��e vr�tit i ko�en.
     */
    private VybranyGen nahodnyGen(Genotyp genotyp) {

        Gen gen = genotyp.getKoren();		// v�b�r ko�ene
        gen.setNejvyssiHloubka(gen);
        double p = 1.0 / Gen.nejvyssiHloubka;

        // p��pad, kdy je nejvy��� hloubka == 1, znamen�, �e mus�me vybrat termin�l
        if (Gen.nejvyssiHloubka == 1) {
            VybranyGen vg = new VybranyGen();
            vg.setNadgen(gen);
            vg.setIndexPodgenu(nahodneKladneCislo(gen.geny.size()));
            return vg;
        }

        boolean funkce = false;
        if (nahodneKladneCisloDouble(1) < pravdepodobnostKrizeniVUzluFunkce) {
            funkce = true;
        }

        while (p <= 1) {
            if (nahodneKladneCisloDouble(1) < p) {							// pokud zva�ujeme tento uzel
                if (!funkce) {												// m�-li b�t vybr�n termin�l
                    List<Integer> indexy = new ArrayList<Integer>(0);
                    for (int i = 0; i < gen.geny.size(); i++) {
                        if (!gen.geny.get(i).isJeFunkce()) {
                            indexy.add(i);
                        }
                    }
                    if (indexy.size() != 0) {
                        int i = indexy.get(nahodneKladneCislo(indexy.size()));
                        VybranyGen vg = new VybranyGen();
                        vg.setNadgen(gen);
                        vg.setIndexPodgenu(i);
                        return vg;
                    }
                    p = p + p;

                    gen = gen.geny.get(nahodneKladneCislo(gen.geny.size()));
                } else {														// m�-li b�t vybr�na funkce
                    List<Integer> indexy = new ArrayList<Integer>(0);
                    for (int i = 0; i < gen.geny.size(); i++) {
                        if (gen.geny.get(i).isJeFunkce()) {
                            indexy.add(i);
                        }
                    }
                    if (indexy.size() != 0) {
                        int i = indexy.get(nahodneKladneCislo(indexy.size()));
                        VybranyGen vg = new VybranyGen();
                        vg.setNadgen(gen);
                        vg.setIndexPodgenu(i);
                        return vg;
                    }

                    gen = genotyp.getKoren();
                    break;
                }
            }
        }

        VybranyGen vg = new VybranyGen();
        vg.setNadgen(gen);
        vg.setIndexPodgenu(nahodneKladneCislo(gen.geny.size()));
        return vg;

    }

    /**
     * Metoda turnajov� selekce pro 2 jedince
     */
    private Genotyp turnajovaSelekce() {
        int index1 = nahodneKladneCislo(populace.size());
        int index2 = nahodneKladneCislo(populace.size());
        int index3 = nahodneKladneCislo(populace.size());
        while (index1 == index2 || index1 == index3 || index2 == index3) {
            index2 = nahodneKladneCislo(populace.size());
            index3 = nahodneKladneCislo(populace.size());
        }
        if (populace.get(index1).getFitness().getValue() >= populace.get(index2).getFitness().getValue()) {
            return new Genotyp(populace.get(index1));
        } else {
            return new Genotyp(populace.get(index2));
        }
    }

    /**
     * Metoda turnajov� selekce pro 3 jedince
     */
    private Genotyp turnajovaSelekce3() {
        int pocet = 3;
        int[] poleIndexu = new int[pocet];

        for (int i = 0; i < pocet; i++) {
            poleIndexu[i] = nahodneKladneCislo(populace.size());

            for (int j = 0; j < i; j++) {
                if (poleIndexu[j] == poleIndexu[i]) {
                    poleIndexu[i] = nahodneKladneCislo(populace.size());
                    j = 0;
                }
            }
        }

        Genotyp vitez = populace.get(poleIndexu[0]);

        for (int i = 0; i < pocet; i++) {
            if (Math.abs(vitez.getFitness().getValue()) > Math.abs(populace.get(poleIndexu[i]).getFitness().getValue())) {
                vitez = populace.get(poleIndexu[i]);
            }
        }

        return new Genotyp(vitez);
    }

    /**
     * Metoda pro vytvo�en� po��te�n� populace v z�vislosti na zadan� metod�
     * inicializace.
     */
    private void vytvorPocatecniPopulaci(List<Gen> mnozinaTerminalu, List<Gen> mnozinaFunkci, int maximalniHloubka, int velikostPopulace, int metodaInicializace) {

        for (int i = 0; i < velikostPopulace; i++) {
            Genotyp genotyp = new Genotyp(maximalniHloubka, mnozinaTerminalu, mnozinaFunkci, metodaInicializace);
            populace.add(genotyp);
        }

    }

    /**
     * Metoda pro prov�d�n� zadan�ho �e�en�. Postupn� zpracuje textov� zadan�
     * �e�en� na funk�n� genotyp (strom gen�) a n�sledn� opakuje jeho prov�d�n�,
     * dokud nen� po�et krok� mravence nulov�.
     */
    public void provedeniGenotypu(int maximalniHloubka, int pocetKroku, JTextArea textAreaReseni) throws BadLocationException {

        List<Gen> mnozinaTerminalu = new ArrayList<Gen>();
        List<Gen> mnozinaFunkci = new ArrayList<Gen>();

        Gen left = new Terminal("LEFT");
        Gen right = new Terminal("RIGHT");
        Gen move = new Terminal("MOVE");

        mnozinaTerminalu.add(left);
        mnozinaTerminalu.add(right);
        mnozinaTerminalu.add(move);

        Gen ifFoodAhead = new Gen("IF-FOOD-AHEAD", 2);
        Gen progn2 = new Gen("PROGN2", 2);
        Gen progn3 = new Gen("PROGN3", 3);

        mnozinaFunkci.add(ifFoodAhead);
        mnozinaFunkci.add(progn2);
        mnozinaFunkci.add(progn3);

        String retezec = "";

        // p�i zad�n� pr�zdn�ho �et�zce
        if (textAreaReseni.getText().length() == 0) {
            System.out.println("Nebyl zad�n ��dn� program pro p�ehr�n�.");
            return;
        }

        // odstran�n� z�vorek, mezer a ��rek
        for (int i = 0; i < textAreaReseni.getText().length(); i++) {
            if (!(textAreaReseni.getText().charAt(i) == ' ' | textAreaReseni.getText().charAt(i) == ',' | textAreaReseni.getText().charAt(i) == '(' | textAreaReseni.getText().charAt(i) == ')')) {
                retezec = retezec + textAreaReseni.getText().charAt(i);
            }
        }

        String prikaz = "";
        List<Funkce> seznamFunkci = new ArrayList<Funkce>();	// z�sobn�k
        Funkce koren = null;
        Terminal terminal;

        // rozpozn� zadan� p��kaz
        for (int i = 0; i < retezec.length(); i++) {
            prikaz = prikaz + retezec.charAt(i);

            switch (prikaz) {
                case "IF-FOOD-AHEAD":
                    prikaz = "";

                    if (seznamFunkci.size() != 0) {

                        while (seznamFunkci.get(seznamFunkci.size() - 1).arita == seznamFunkci.get(seznamFunkci.size() - 1).geny.size()) {
                            seznamFunkci.remove(seznamFunkci.size() - 1);
                        }

                        Funkce funkce = new Funkce("IF-FOOD-AHEAD", 2, seznamFunkci.size(), maximalniHloubka, mnozinaTerminalu, mnozinaFunkci);
                        seznamFunkci.get(seznamFunkci.size() - 1).geny.add(funkce);
                        seznamFunkci.add(funkce);

                    } else {
                        koren = new Funkce("IF-FOOD-AHEAD", 2, 0, maximalniHloubka, mnozinaTerminalu, mnozinaFunkci);
                        seznamFunkci.add(koren);
                    }

                    break;

                case "PROGN2":
                    prikaz = "";

                    if (seznamFunkci.size() != 0) {

                        while (seznamFunkci.get(seznamFunkci.size() - 1).arita == seznamFunkci.get(seznamFunkci.size() - 1).geny.size()) {
                            seznamFunkci.remove(seznamFunkci.size() - 1);
                        }

                        Funkce funkce = new Funkce("PROGN2", 2, seznamFunkci.size(), maximalniHloubka, mnozinaTerminalu, mnozinaFunkci);
                        seznamFunkci.get(seznamFunkci.size() - 1).geny.add(funkce);
                        seznamFunkci.add(funkce);

                    } else {
                        seznamFunkci.add(new Funkce("PROGN2", 2, 0, maximalniHloubka, mnozinaTerminalu, mnozinaFunkci));
                        koren = seznamFunkci.get(0);
                    }

                    break;

                case "PROGN3":
                    prikaz = "";

                    if (seznamFunkci.size() != 0) {
                        while (seznamFunkci.get(seznamFunkci.size() - 1).arita == seznamFunkci.get(seznamFunkci.size() - 1).geny.size()) {
                            seznamFunkci.remove(seznamFunkci.size() - 1);
                        }

                        Funkce funkce = new Funkce("PROGN3", 3, seznamFunkci.size(), maximalniHloubka, mnozinaTerminalu, mnozinaFunkci);
                        seznamFunkci.get(seznamFunkci.size() - 1).geny.add(funkce);
                        seznamFunkci.add(funkce);

                    } else {
                        seznamFunkci.add(new Funkce("PROGN3", 3, 0, maximalniHloubka, mnozinaTerminalu, mnozinaFunkci));
                        koren = seznamFunkci.get(0);
                    }

                    break;

                case "RIGHT":
                    while (seznamFunkci.get(seznamFunkci.size() - 1).arita == seznamFunkci.get(seznamFunkci.size() - 1).geny.size()) {
                        seznamFunkci.remove(seznamFunkci.size() - 1);
                    }
                    prikaz = "";
                    terminal = new Terminal("RIGHT");

                    if (seznamFunkci.size() != 0) {
                        seznamFunkci.get(seznamFunkci.size() - 1).geny.add(terminal);
                    }

                    break;

                case "LEFT":
                    while (seznamFunkci.get(seznamFunkci.size() - 1).arita == seznamFunkci.get(seznamFunkci.size() - 1).geny.size()) {
                        seznamFunkci.remove(seznamFunkci.size() - 1);
                    }
                    prikaz = "";
                    terminal = new Terminal("LEFT");
                    if (seznamFunkci.size() != 0) {
                        seznamFunkci.get(seznamFunkci.size() - 1).geny.add(terminal);
                    }

                    break;

                case "MOVE":
                    while (seznamFunkci.get(seznamFunkci.size() - 1).arita == seznamFunkci.get(seznamFunkci.size() - 1).geny.size()) {
                        seznamFunkci.remove(seznamFunkci.size() - 1);
                    }
                    prikaz = "";
                    terminal = new Terminal("MOVE");
                    if (seznamFunkci.size() != 0) {
                        seznamFunkci.get(seznamFunkci.size() - 1).geny.add(terminal);
                    }

                    break;

                default:
                    break;
            }

        }

    }

    public int nahodneKladneCislo(int horniHranice) {		// vraci hodnoty 0 =< hodnota < horniHranice
        return (int) (Math.random() * horniHranice);
    }

    public double nahodneKladneCisloDouble(int horniHranice) {
        return Math.random() * horniHranice;
    }

    public Button getButtonZastavit() {
        return buttonZastavit;
    }

    public void setButtonZastavit(Button buttonZastavit) {
        this.buttonZastavit = buttonZastavit;
    }

    /**
     * quicksort - metoda pro se�azen� jedinc� podle zdatnosti. Vyu��v� ji
     * decimace.
     */
    public static void quicksort(List<Genotyp> populace, int left, int right) {
        if (left < right) {
            int boundary = left;
            for (int i = left + 1; i < right; i++) {
                if (populace.get(i).getFitness().getValue() > populace.get(left).getFitness().getValue()) {
                    swap(populace, i, ++boundary);
                }
            }
            swap(populace, left, boundary);
            quicksort(populace, left, boundary);
            quicksort(populace, boundary + 1, right);
        }
    }

    private static void swap(List<Genotyp> populace, int left, int right) {
        Genotyp tmp = new Genotyp(populace.get(right));
        populace.set(right, populace.get(left));
        populace.set(left, tmp);
    }

}
