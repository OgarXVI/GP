/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import javafx.scene.control.TextArea;

/**
 * Class for saving reports and printing them.
 *
 * @author OgarXVI
 */
public class Messenger {
    
    
    private ArrayList<String> messages;
    /**
     * *
     *
     */
    private TextArea textArea;

    
    /**
     * *
     *
     * @param textArea
     */
    public Messenger(TextArea textArea) {
        this.messages = new ArrayList<>();
        this.textArea = textArea;
    }

    /**
     * *
     *
     */
    public void ClearMessenger() {
        this.messages.clear();
        this.textArea.setText("");
    }

    /**
     * *
     * Return first messeage
     *
     * @return
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
     * *
     * Add messeage
     *
     * @param s
     */
    public void AddMesseage(String s) {
        if (s == null) {
            return;
        }
        messages.add(s + System.lineSeparator());
    }

    /**
     * *
     * Append text to Text Area
     *
     * @param s
     */
    public void AppendMesseage(String s) {
        if (textArea != null) {
            javafx.application.Platform.runLater(() -> textArea.appendText(s));
        }
    }

    public void GetAllMesseages() {
        while (!messages.isEmpty()) {
            GetMesseage();
        }
    }

// function to check if character is operator or not
    public boolean isOperator(char x) {
        switch (x) {
            case '+':
            case '-':
            case '/':
            case '*':
                return true;
        }
        return false;
    }

// Convert prefix to Infix expression
    public String preToInfix(String pre_exp) {
        Stack<String> s = new Stack<>();

// length of expression
        int length = pre_exp.length();

// reading from right to left
        for (int i = length - 1; i >= 0; i--) {

            // check if symbol is operator
            if (isOperator(pre_exp.charAt(i))) {

                // pop two operands from stack
                String op1 = s.lastElement();
                s.pop();
                String op2 = s.lastElement();
                s.pop();

                // concat the operands and operator
                String temp = "(" + op1 + pre_exp.charAt(i) + op2 + ")";

                // Push string temp back to stack
                s.push(temp);
            } // if symbol is an operand
            else {

                // push the operand to the stack
                s.push(String.valueOf(pre_exp.charAt(i)));
            }
        }

// Stack now contains the Infix expression
        return s.lastElement();
    }

    

}
