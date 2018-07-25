/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.controller;

import cz.ogarxvi.model.Messenger;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author OgarXVI
 */
public class CSVReader {
    
     //TODO: AUTO?
    private int[][] xlsData;

    private Messenger m;
    
    public CSVReader(Messenger m) {
        this.m = m;
        xlsData = new int[3][3];
    }
    
    public void ReadCSV(File file){
        String line = null;
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            while ((line = br.readLine()) != null) {
                String[] splitedData = line.split(",");
                //TODO:
                m.AddMesseage(Arrays.toString(splitedData));
                m.GetMesseage();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
