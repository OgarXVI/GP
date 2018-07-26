/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.model;

import java.util.ArrayList;
import javafx.scene.control.TextArea;

/**
 * Class for saving reports and printing them.
 * @author OgarXVI
 */
public class Messenger {
    /***
     * TODO: REFACTOR TO STACK
     */
    private ArrayList<String> messages;
    /***
     *
     */
    private TextArea textArea;
    /***
     * 
     * @param textArea
     */
    public Messenger(TextArea textArea) {
        this.messages = new ArrayList<>();
        this.textArea = textArea;
    }
    /***
     * 
     */
    public void ClearMessenger(){
        this.messages.clear();
        this.textArea.setText("");
    }
    /***
     * Return first messeage
     * @return 
     */
    public String GetMesseage(){
        if (messages.isEmpty()) return null;
        int indexOfLast = messages.size();
        if (indexOfLast <= 0) return null;
        ArrayList<String> tmpAL = new ArrayList<>(messages);
        String tmpS = tmpAL.get(0);
        tmpAL.remove(0);
        messages = tmpAL;
        AppendMesseage(tmpS);
        return tmpS;
    }
    /***
     * Add messeage
     * @param s 
     */
    public void AddMesseage(String s){
        if (s == null)
            return;
        messages.add(s + System.lineSeparator());
    }
    /***
     * Append text to Text Area
     * @param s
     */
    public void AppendMesseage(String s){
        if (textArea!= null)
        textArea.appendText(s);
    }
    
    public void GetAllMesseages(){
        while (!messages.isEmpty())
            GetMesseage();
    }
    
}
