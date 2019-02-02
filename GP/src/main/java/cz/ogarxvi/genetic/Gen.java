package cz.ogarxvi.genetic;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ch.obermuhlner.math.big.BigDecimalMath;
import java.math.BigInteger;
import java.math.MathContext;

/**
 * Třída představující geny. Každý uzel stromu je genem (fukncí nebo
 * terminálem). Každý gen umí vypsat svůj příkaz a příkazy svých podgenů a te ve
 * tvaru použitelným při výpočtu vzorce. Je předkem pro Function a Terminal
 */
public class Gen {

    /**
     * Maximální hloubka genu
     */
    public static int maxDepth = 0;
    /**
     * Příkaz, který prezentuje gen, například "+" je příkaz, stejně tak "4"
     */
    protected String command;
    /**
     * Zda je gen funkce
     */
    protected boolean isFunction;
    /**
     * Jakou má gen aritu, tedy kolik může mít pod sebou dalších genů
     */
    protected int arita;
    /**
     * Jaké má gen pod sebou geny, tvořící tak strom
     */
    protected List<Gen> gens;
    /**
     * Hloubka genu
     */
    protected int depth;
    /**
     * Zda se jedná gen se změněním způsobem zápisu
     */
    protected boolean editable;

    /**
     * *
     * Konstanta pro odmocnění
     */
    private static final BigDecimal TWO = BigDecimal.ONE.add(BigDecimal.ONE);

    /**
     * Založení genu s příkazem a aritou
     *
     * @param command příkaz
     * @param arita arita
     */
    public Gen(String command, int arita) {
        this.command = command;
        this.isFunction = false;
        this.arita = arita;
        if (arita != 0) {
            gens = new ArrayList<>(arita);
        }
    }

    /**
     * Založení genu s příkazem a aritou
     *
     * @param command příkaz
     * @param arita arita
     * @param editable Změna zápisu
     */
    public Gen(String command, int arita, boolean editable) {
        this.command = command;
        this.isFunction = false;
        this.arita = arita;
        if (arita != 0) {
            gens = new ArrayList<>(arita);
        }
        this.editable = editable;
    }

    public Gen() {
    }

    /**
     * Kopírovací konstruktor
     *
     * @param gen
     */
    public Gen(Gen gen) {
        this.command = gen.command;
        this.isFunction = gen.isFunction;
        this.arita = gen.arita;
        this.depth = gen.depth;
        if (this.isFunction) {
            this.gens = new ArrayList<>(this.arita);
        }

        for (int i = 0; i < this.arita; i++) {
            Gen podgen = new Gen(gen.gens.get(i));
            this.gens.add(podgen);
        }
        this.editable = gen.editable;
    }

    /**
     * Rekurzivní výpis genu
     *
     * @return Vrací popisný stav genu
     */
    public String print() {
        switch (arita) {
            case 0:
                return command;
            case 1:
                return command + "(" + gens.get(0).print() + ")";
            case 2:
                /*if (editable)
                    return command + "(" + gens.get(0).print() + ", " + gens.get(1).print() + ")";
                else*/
                return ((depth != 0) ? "(" : "") + gens.get(0).print() + command + gens.get(1).print() + ((depth != 0) ? ")" : "");
            case 3:
                return ((depth != 0) ? "(" : "") + gens.get(0).print() + command + gens.get(1).print() + command + gens.get(2).print() + ((depth != 0) ? ")" : "");
            case 4:
                return ((depth != 0) ? "(" : "") + gens.get(0).print() + command + gens.get(1).print() + command + gens.get(2).print() + command + gens.get(3).print() + ((depth != 0) ? ")" : "");

        }
        return "";
    }

    /**
     * Opravení hloubky genu po operaci, která mohla změnit hloubku genu
     */
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

    /**
     * Rekurzivní výpočet hodnoty genu
     *
     * @param values mapa klíče a hodnoty. knihovny třetí strany
     * (BigDecimalMath) na práci s BigDecimal
     * @return Vrátí vypočtený kus pro další vyhodnocení
     */
    public BigDecimal resolveCommand(Map<String, BigDecimal> values) {
        switch (command) {
            case "+":
                return gens.get(0).resolveCommand(values).add(gens.get(1).resolveCommand(values));
            case "-":
                return gens.get(0).resolveCommand(values).subtract(gens.get(1).resolveCommand(values));
            case "*":
                return gens.get(0).resolveCommand(values).multiply(gens.get(1).resolveCommand(values));
            case "^":
                BigDecimal bgPom = gens.get(1).resolveCommand(values);
                if (bgPom.intValue() > 0 && bgPom.intValue() < 6) {
                    return gens.get(0).resolveCommand(values).pow(bgPom.intValue());
                }
                return BigDecimal.valueOf(Double.MAX_VALUE); //+infinity //UNSUPPORTED
            case "/":
                BigDecimal bDD = gens.get(1).resolveCommand(values);
                if (bDD.compareTo(BigDecimal.ZERO) == 0) {
                    return BigDecimal.valueOf(Double.MAX_VALUE); //+infinity
                } else {
                    return gens.get(0).resolveCommand(values).divide(gens.get(1).resolveCommand(values), 6, RoundingMode.HALF_UP);
                }
            case "sin":
                try {
                    return BigDecimalMath.sin(gens.get(0).resolveCommand(values), new MathContext(6));
                } catch (ArithmeticException e) {
                    return gens.get(0).resolveCommand(values); //IGNORE
                }
            case "cos":
                try {
                    return BigDecimalMath.cos(gens.get(0).resolveCommand(values), new MathContext(6));
                } catch (ArithmeticException e) {
                    return gens.get(0).resolveCommand(values); //IGNORE
                }
            case "tan":
                try {
                    return BigDecimalMath.tan(gens.get(0).resolveCommand(values), new MathContext(6));
                } catch (ArithmeticException e) {
                    return gens.get(0).resolveCommand(values); //IGNORE
                }
            case "sqrt":
                BigDecimal bgSqrt = gens.get(0).resolveCommand(values);
                if (bgSqrt.doubleValue() == Double.POSITIVE_INFINITY
                        || bgSqrt.doubleValue() == Double.NEGATIVE_INFINITY) {
                    return BigDecimal.valueOf(Double.MAX_VALUE); // MOC VELKÝ!
                }
                if (bgSqrt.compareTo(BigDecimal.ZERO) < 0) {
                    return BigDecimal.valueOf(Double.MAX_VALUE); //+infinity //NOT DEFINET
                }
                if (bgSqrt.compareTo(BigDecimal.ZERO) == 0) {
                    return bgSqrt;
                }
                if (bgSqrt.compareTo(BigDecimal.ONE) == 0) {
                    return bgSqrt;
                }
                return mysqrt(bgSqrt, new MathContext(6));
            case "abs":
                return gens.get(0).resolveCommand(values).abs();
            case "exp":
                return BigDecimalMath.exp(gens.get(0).resolveCommand(values), new MathContext(6));
            case "log2":
                BigDecimal bgLog2 = gens.get(0).resolveCommand(values);
                if (bgLog2.compareTo(BigDecimal.ZERO) <= 0) {
                    return BigDecimal.valueOf(Double.MAX_VALUE); //+infinity //NOT DEFINET
                }
                return BigDecimalMath.log2(bgLog2, new MathContext(6));
            case "log10":
                BigDecimal bgLog10 = gens.get(0).resolveCommand(values);
                if (bgLog10.compareTo(BigDecimal.ZERO) <= 0) {
                    return BigDecimal.valueOf(Double.MAX_VALUE); //+infinity //NOT DEFINET
                }
                return BigDecimalMath.log10(bgLog10, new MathContext(6));
            case "ln":
                BigDecimal bgLog = gens.get(0).resolveCommand(values);
                if (bgLog.compareTo(BigDecimal.ZERO) <= 0) {
                    return BigDecimal.valueOf(Double.MAX_VALUE); //+infinity //NOT DEFINET
                }
                return BigDecimalMath.log(bgLog, new MathContext(6));
            case "!":
                return gens.get(0).resolveCommand(values).negate();
            case "cotg":
                try {
                    return BigDecimalMath.cot(gens.get(0).resolveCommand(values), new MathContext(6));
                } catch (ArithmeticException e) {
                    return gens.get(0).resolveCommand(values); //IGNORE
                }
            case "min":
                return gens.get(0).resolveCommand(values).min(gens.get(1).resolveCommand(values));
            case "max":
                return gens.get(0).resolveCommand(values).max(gens.get(1).resolveCommand(values));
            default:
                return values.get(command);
        }
    }

    /**
     * Vrátí aritu genu
     *
     * @return arita
     */
    public int getArita() {
        return arita;
    }

    /**
     * Vrátí, zda je gen funkcí
     *
     * @return Je funkce
     */
    public boolean isFunction() {
        return isFunction;
    }

    /**
     * Nastavaví, zda je gen funkcí
     *
     * @param jeFunkce je Funkce
     */
    public void setIsFunction(boolean jeFunkce) {
        this.isFunction = jeFunkce;
    }

    /**
     * Vrátí příkaz genu
     *
     * @return Příkaz
     */
    public String getCommand() {
        return command;
    }

    /**
     * Nastaví maximální hloubku genů
     *
     * @param gen Gen
     */
    public void setMaxDepth(Gen gen) {
        Gen.maxDepth = 0;
        getMaxDepth(gen);
    }

    /**
     * Vrátí max hloubku genů
     *
     * @param gen Gen
     */
    private void getMaxDepth(Gen gen) {
        if (gen.depth > maxDepth) {
            maxDepth = gen.depth;
        }
        for (int i = 0; i < gen.arita; i++) {
            gen.gens.get(i).getMaxDepth(gen.gens.get(i));
        }
    }

    /**
     * Vrátí hloubku genu
     *
     * @return hloubka genu
     */
    public int getDepth() {
        return depth;
    }

    /**
     * Nastaví hloubku genu
     *
     * @param depth Hloubka genu
     */
    public void setDepth(int depth) {
        this.depth = depth;
    }

    public List<Gen> getGens() {
        return gens;
    }

    @Override
    public String toString() {
        return command;
    }

    public List<Gen> getAll() {
        List<Gen> list = new ArrayList<>();
        if (this != null) {
            list.add(this);
        }
        if (this.gens != null) {
            if (this.gens.get(0) != null) {
                list.addAll(gens.get(0).getAll());
            }
            if (gens.size() > 1) {
                if (this.gens.get(1) != null) {
                    list.addAll(gens.get(1).getAll());
                }
            }
        }
        return list;
    }

    /**
     * Optimalizována funkce druhé odmocniny
     *
     * @param n BigDecimal
     * @param mc Přesnost
     * @return Druhá odmocnina
     */
    private static BigDecimal mysqrt(BigDecimal n, MathContext mc) {
        ArrayList<BigDecimal> results = new ArrayList<>();
        BigDecimal xnp1 = new BigDecimal(Math.sqrt(n.doubleValue()), mc);
        results.add(xnp1);
        xnp1 = xnp1.add(n.divide(xnp1, mc)).divide(TWO, mc);
        while (true) {
            if (xnp1.subtract(results.get(results.size() - 1), mc).abs(mc).doubleValue() <= 0.000001) {
                break;
            }
            results.add(xnp1);
            xnp1 = xnp1.add(n.divide(xnp1, mc)).divide(TWO, mc);
        }
        results.add(xnp1);
        return results.get(results.size() - 1);

    }
}
