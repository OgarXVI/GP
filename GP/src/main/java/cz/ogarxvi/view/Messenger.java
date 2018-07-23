/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.view;

import java.util.ArrayList;

/**
 * Class for saving reports and printing them.
 * @author OgarXVI
 */
public class Messenger {
    /***
     * 
     */
    private ArrayList<String> messages;
    /***
     * 
     */
    public Messenger() {
        this.messages = new ArrayList<>();
    }
    /***
     * 
     */
    public void ClearMessenger(){
        this.messages.clear();
    }
    /***
     * 
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
        return tmpS;
    }
    /***
     * 
     * @param s 
     */
    public void AddMesseage(String s){
        if (s == null)
            return;
        messages.add(s);
    }
    
}
