package cz.ogarxvi.model;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Třída nabízející statickou metodu pro získání řetězce na základě klíče
 * @author OgarXVI
 */
public class Localizator {
    /**
     * Vrátí řětezec na základě uvedeného klíče
     * @param key Klíč pro nalezení řetězce
     * @return Řetezec ze resources
     */
    public static String getString(String key) {
        
        FileInputStream fis = null;
        ResourceBundle words = null;
        try {
             words = ResourceBundle.getBundle("localization/localization");
             return words.getString(key);
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return "";
    }
}
