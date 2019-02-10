/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Singleton na držení informací o načteném souboru. Spravuje úkony na souboru.
 *
 * @author OgarXVI
 */
public class FileHandler {

    /**
     * Samotný načtený soubor
     */
    public File file;
    /**
     * Načtená data ze souboru
     */
    public String[][] readedData;
    /**
     * Oznamuje, zda byly chyby v souboru
     */
    public boolean errorsInFile;
    /**
     * Inforumje o pozicích (indexů) řádků chyb v přijatém souboru
     */
    public List<Integer> errorRowPositions;
    /**
     * Informuje o pozicích (indexech) sloupců chyb v přijatém souboru
     */
    public List<Integer> errorColummPosition;
    /**
     * Udržuje informace o výsledcích posledních sloupců
     */
    public List<String> calculatedFinalValues;
    /**
     * Uživatel si zadal opravu původního souboru
     */
    public boolean repairFile;
    /**
     * Uživatel si zadal opravu a kopírování obsahu do nového souboru
     */
    public boolean copyFile;
    /**
     * Uživatel si zadal opravení celého obsahu souboru dle objevené funkce
     */
    public boolean reconstructFile;
    /**
     * 
     */
    private volatile static FileHandler instance;
    /**
     * 
     * @return 
     */
    public synchronized static FileHandler getInstance() {
        if (instance == null) {
            instance = new FileHandler();
        }
        return instance;
    }
    /**
     * 
     */
    private FileHandler() {
        errorRowPositions = new ArrayList<>();
    }

    /**
     * Provede operaci operaci na všechny řádky celého souboru Samozřejmě kromě
     * hlavičky, kde jsou pouze názvy
     *
     * @param function
     * @return
     */
    public List<String> repairAllRow(String function) {
        List<String> pomList = new ArrayList<>();
        for (int i = 1; i < readedData.length; i++) {
            pomList.add(repairRow(function, i));
        }
        return pomList;
    }
    /**
     * Provede operaci na konkrétní řádky souboru kromě hlavičky.
     * @param function
     * @param rows
     * @return 
     */
    public List<String> reparSpecificRows(String function, List<Integer> rows) {
        List<String> pomList = new ArrayList<>();
        for (int i = 1; i < readedData.length; i++) {
            pomList.add(repairRow(function, rows.get(i)));
        }
        return pomList;
    }
    
    public String resolveExpansion(String fileName){
        //TODO:
        return null;
    }
    
    /**
     * Načte celou funkci, získá dle ní terminály Terminály nahradí hodnotamy z
     * tabulky Provede výpočet dané funkce Slouží pouze k zjištění výsledku
     * funkce
     *
     * Funkce funguje, pouze pokud jsou terminály velkými znaky. V opačném
     * případě může dojít ke kolizi s funkcemi.
     *
     * @param function
     * @param row
     * @return
     */
    public String repairRow(String function, int row) {
        //Zjištění terminálů
        List<Character> terminals = new ArrayList<>();
        List<String> terminalsValues = new ArrayList<>();
        for (int i = 0; i < function.length(); i++) {
            if (Character.isUpperCase(function.charAt(i))) {
                terminals.add(function.charAt(i));
            }
        }
        //Zjištění columm a hodnoty všech terminálů.
        //Snad je tohle délka všech row
        for (int i = 1; i < readedData.length; i++) {
            if (terminals.get(i) == readedData[row][i].charAt(0)) {
                terminalsValues.add(readedData[row][i]);
            }
        }
        //Evaluate expression
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("JavaScript");
        String result = "";
        try {
            String command = "";
            for (int i = 0; i < terminals.size(); i++) {
                command += "var " + terminals.get(i) + " = " + terminalsValues.get(i) + ";";
            }
            command += function;
            result = engine.eval(command).toString();
        } catch (ScriptException ex) {
            Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    /**
     * 
     * @param replaces
     * @param colums
     * @param rows
     * @return 
     */
    public boolean copyFile(List<String> replaces, List<Integer> colums, List<Integer> rows) {
        try {
            FileUtils.copyFile(file, new File("COPY_" + file.getName()));
            updateXLS(replaces, colums, rows);
        } catch (IOException ex) {
            return false;
        }
        return true;
    }

    /**
     * Vrátí všechny možné pozice posledních sloupců
     *
     * @return
     */
    public List<Integer> getPositionAllLastColumms() {
        List<Integer> pom = new ArrayList<>();
        for (int i = 1; i < readedData.length; i++) {
            pom.add(readedData[0].length); //TODO -1?
        }
        return pom;
    }

    /**
     * Vrátí jednotlivé indexy všech řádků v souboru
     *
     * @return
     */
    public List<Integer> getPositionAllRows() {
        List<Integer> pom = new ArrayList<>();
        for (int i = 1; i < readedData.length; i++) {
            pom.add(i);
        }
        return pom;
    }

    /**
     * Aktulizuje soubor xlsx o nové zjištěné hodnoty, na konkrétně zadaných
     * paramerech. Pozor je potřeba dát na dodrření vstupních parametrů.
     *
     * @param replaces
     * @param colums
     * @param rows
     */
    public void updateXLS(List<String> replaces, List<Integer> colums, List<Integer> rows) {
        try {
            //org.apache.poi.xssf.usermodel.XSSFWorkbook wb = new XSSFWorkbook(file);
            //Access the worksheet, so that we can update / modify it.
            try (XSSFWorkbook wb = (XSSFWorkbook) WorkbookFactory.create(new FileInputStream(file))) {
                //org.apache.poi.xssf.usermodel.XSSFWorkbook wb = new XSSFWorkbook(file);
                //Access the worksheet, so that we can update / modify it.
                XSSFSheet sheet = (XSSFSheet) wb.getSheetAt(0);
                // declare a Cell object
                // Get Row at index 1
                //Modify
                for (int i = 0; i < rows.size(); i++) {
                    Row row = sheet.getRow(rows.get(i));
                    // Get the Cell at index 2 from the above row
                    Cell cell = row.getCell(colums.get(i));
                    if (cell == null) {
                        cell = row.createCell(i);
                    }
                    // Access the second cell in second row to update the value
                    cell.setCellType(CellType.STRING);
                    cell.setCellValue(replaces.get(i));
                }
                //Open FileOutputStream to write updates
                FileOutputStream fileOut = new FileOutputStream(file);
                wb.write(fileOut);
                fileOut.close();
                // Closing the workbook
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InvalidFormatException ex) {
            ex.printStackTrace();
        }
    }
    /**
     * Rozhodne o způsobu opravy souboru
     * @param function 
     */
    public void resolveGPReconstruct(String function) {
        if (repairFile && reconstructFile) {
            if (copyFile) {
                copyFile(repairAllRow(function), getPositionAllLastColumms(), getPositionAllRows());
            } else {
                updateXLS(repairAllRow(function), getPositionAllLastColumms(), getPositionAllRows());
            }
        } else if (copyFile) {
            copyFile(reparSpecificRows(function, errorRowPositions), getPositionAllLastColumms(), getPositionAllRows());
        } else if (repairFile) {
            updateXLS(reparSpecificRows(function, errorRowPositions), getPositionAllLastColumms(), getPositionAllRows());
        }
    }

}
