package cz.ogarxvi.model;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Třída nabízející statickou metodu pro získání řetězce na základě klíče
 *
 * @author OgarXVI
 */
public class Localizator {

    private static ResourceBundle words;

    /**
     * Vrátí řětezec na základě uvedeného klíče
     *
     * @param key Klíč pro nalezení řetězce
     * @return Řetezec ze resources
     */
    public static String getString(String key) {
        try {
            return words.getString(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Nastaví lokalizaci a balíček jazyka
     *
     * @param local lokal
     * @return
     */
    public static boolean setLocale(Locale local) {
        try {
            words = ResourceBundle.getBundle("localization/localization", local);
        } catch (MissingResourceException e) {
            return false;
        }
        return true;
    }
}
