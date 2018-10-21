/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.model;

import java.util.ArrayList;
import javafx.scene.control.TextArea;

/**
 * Třída pro ukládání a posílání zpráv
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
     * Vytvoří instanci pro ukládání a vypisování zpráv
     * @param textArea Vypisovací zóna
     */
    public Messenger(TextArea textArea) {
        this.messages = new ArrayList<>();
        this.textArea = textArea;
    }

    /**
     * Vyčistí list od všech zpráv v listu a smaže text vevypisované oblasti
     */
    public void ClearMessenger() {
        this.messages.clear();
        this.textArea.setText("");
    }

    /**
     * Vrátí posledně přidánou zprávu
     * @return Vrátí posledně přidáný string
     */
    public String GetMesseage() {
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
     * @param s NO-NULL string
     */
    public void AddMesseage(String s) {
        if (s == null) {
            return;
        }
        messages.add(s + System.lineSeparator());
    }

    /**
     * Přidá vybranou zprávu do textové oblasti
     * @param s Vybraná zpráva
     */
    private void AppendMesseage(String s) {
        if (textArea != null) {
            javafx.application.Platform.runLater(() -> textArea.appendText(s));
        }
    }
    /**
     * Zavolá metodi "GetMesseage" na všechny zaznamenané zprávy.
     */
    public void GetAllMesseages() {
        while (!messages.isEmpty()) {
            GetMesseage();
        }
    }

}
