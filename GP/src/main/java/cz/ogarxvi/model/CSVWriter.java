/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author OgarXVI
 */
public class CSVWriter implements IWriter {

    @Override
    public boolean update(List<String> replaces, List<Integer> colums, List<Integer> rows, File file) {
        boolean bool = false;
        try {
            //Načíst
            com.opencsv.CSVReader reader = new com.opencsv.CSVReader(new FileReader(file), ',', com.opencsv.CSVWriter.NO_QUOTE_CHARACTER);
            List<String[]> csvBody = reader.readAll();
            for (int i = 0; i < replaces.size(); i++) {
                csvBody.get(rows.get(i)+1)[colums.get(i)] = replaces.get(i);
                bool = true;
            }
            reader.close();
            //Uložit
            com.opencsv.CSVWriter writer = new com.opencsv.CSVWriter(new FileWriter(file), ',', com.opencsv.CSVWriter.NO_QUOTE_CHARACTER);
            writer.writeAll(csvBody);
            writer.flush();
            writer.close();
            
            return bool;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bool;
    }

    @Override
    public boolean copy(List<String> replaces, List<Integer> colums, List<Integer> rows, File file) {
        return update(replaces, colums, rows, file);
    }

}
