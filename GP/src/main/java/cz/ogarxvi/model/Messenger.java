/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.model;

import java.util.ArrayList;
import javafx.scene.control.TextArea;
import org.apache.commons.io.FilenameUtils;

/**
 * Třída pro ukládání a posílání zpráv
 *
 * @author OgarXVI
 */
public class Messenger {

    /**
     * List zpráv
     */
    private ArrayList<String> messages;
    /**
     * TextArea, kde se zprávy zobrazují
     */
    private TextArea textArea;
    /**
     * Návrhový vzor Singleton
     */
    private static Messenger instance;

    /**
     * Vytvoří instanci pro ukládání a vypisování zpráv
     *
     * @param textArea Vypisovací zóna
     */
    private Messenger() {
        this.messages = new ArrayList<>();
    }

    public static synchronized Messenger getInstance() {
        if (instance == null) {
            instance = new Messenger();
        }
        return instance;
    }

    /**
     * Vyčistí list od všech zpráv v listu a smaže text vevypisované oblasti
     */
    public synchronized void ClearMessenger() {
        this.messages.clear();
        this.textArea.setText("");
    }

    public void setArea(TextArea textArea) {
        this.textArea = textArea;
    }

    /**
     * Vrátí posledně přidánou zprávu
     *
     * @return Vrátí posledně přidáný string
     */
    public synchronized String GetMesseage() {
        if (messages.isEmpty()) {
            return null;
        }
        int indexOfLast = messages.size();
        if (indexOfLast <= 0) {
            return null;
        }
        ArrayList<String> tmpAL = new ArrayList<>(messages);
        String tmpS = tmpAL.get(0);
        tmpAL.remove(0);
        messages = tmpAL;
        AppendMesseage(tmpS);
        return tmpS;
    }

    /**
     * Přidá do listu zprávu, pokud není NULL
     *
     * @param s NO-NULL string
     */
    public synchronized void AddMesseage(String s) {
        if (s == null) {
            return;
        }
        messages.add(s + System.lineSeparator());
    }

    /**
     * Přidá vybranou zprávu do textové oblasti
     *
     * @param s Vybraná zpráva
     */
    private synchronized void AppendMesseage(String s) {
        if (textArea != null) {
            javafx.application.Platform.runLater(() -> textArea.appendText(s));
        }
    }

    /**
     * Zavolá metodi "GetMesseage" na všechny zaznamenané zprávy.
     */
    public synchronized void GetAllMesseages() {
        while (!messages.isEmpty()) {
            GetMesseage();
        }
    }
    /**
     * Vypíše aktuální stav načtených funkcí, terminálů a souboru.
     */
    public synchronized void Update() {
        ClearMessenger();
        if (FileHandler.getInstance().file != null) {
            AddMesseage(Localizator.getString("output.expectedFunctions")
                    + FilenameUtils.getBaseName(FileHandler.getInstance().file.getName()));
            AddMesseage(Localizator.getString("output.rows")
                    + DataHandler.getInstance().getTableRows());
        }
        AddMesseage(Localizator.getString("output.terminals")
                + DataHandler.getInstance().getLoadedTerminals());
        AddMesseage(Localizator.getString("output.functions")
                + DataHandler.getInstance().getAllFunctionsAsString());
        GetAllMesseages();
    }

}
