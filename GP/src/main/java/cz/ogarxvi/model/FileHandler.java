/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.model;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JOptionPane;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

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
    //public List<String> calculatedFinalValues;
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
     * @return vrátí instanci objektu
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
        errorColummPosition = new ArrayList<>();
    }

    /**
     * Provede operaci operaci na všechny řádky celého souboru Samozřejmě kromě
     * hlavičky, kde jsou pouze názvy
     *
     * @param function funkce
     * @return List opravených hodnot buněk
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
     *
     * @param function funkce
     * @param rows řádky
     * @return vrátí list opravených buňek
     */
    public List<String> reparSpecificRows(String function, List<Integer> rows) {
        List<String> pomList = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            pomList.add(repairRow(function, rows.get(i) + 1));
        }
        return pomList;
    }

    /**
     * Načte celou funkci, získá dle ní terminály Terminály nahradí hodnotamy z
     * tabulky Provede výpočet dané funkce Slouží pouze k zjištění výsledku
     * funkce
     *
     * Funkce funguje, pouze pokud jsou terminály velkými znaky. V opačném
     * případě může dojít ke kolizi s funkcemi.
     *
     * @param function funkce
     * @param row řádky
     * @return vrátí opravenou buňku
     */
    public String repairRow(String function, int row) {
        //Zjištění terminálů
        List<Character> terminals = new ArrayList<>();
        List<String> terminalsValues = new ArrayList<>();
        //System.out.println("FUNKCE: " + function);

        //JE POTŘEBA ROUZLIŠOVAT DVA STAVY - ERROR IN F a v Terminal..
        //Musíme zjistit, zda se opravuje řádek, kde je chyba výsledek
        //Chyba je na posledním řádku
        if (readedData[row][readedData[0].length - 1] == null || readedData[row][readedData[0].length - 1].isEmpty()) {

            //Vyčte a vyfitrulje terminály z funkce
            for (int i = 0; i < function.length(); i++) {
                if (Character.isAlphabetic(function.charAt(i))) {
                    terminals.add(function.charAt(i));
                    terminals = new ArrayList(new HashSet(terminals)); //DISTIC
                    java.util.Collections.sort(terminals); //SORT
                    //System.out.println("Nalezeny terminály ve funkci a přidány!" + function.charAt(i));
                }
            }

            for (int i = 0; i < readedData[row].length - 1; i++) {
                if (readedData[row][i] != null) {
                    terminalsValues.add(readedData[row][i]);
//                    System.out.println("Terminál" + terminals.get(i) + "má hodnotu: " + terminalsValues.get(i));
                }
            }
            //Evaluate expression
            ScriptEngineManager factory = new ScriptEngineManager();
            ScriptEngine engine = factory.getEngineByName("JavaScript");
            String result = "";
            try {
                String command = "";
                for (int i = 0; i < terminals.size(); i++) {
                    try {
                        command += "var " + terminals.get(i) + " = " + terminalsValues.get(i) + ";";
                    } catch (Exception e) {
                        continue;
                    }
                }
                command += function;
//                System.out.println("Celý command zní: " + command);
                result = engine.eval(command).toString();
            } catch (ScriptException ex) {
                ex.printStackTrace();
            }

//            System.out.println("Výsledek výpočtu funkce: " + function + " je " + result);
            return result;
        } //Chyba je někde v těle dat
        else {
            //Takže musíme načíst všechny prvky, včetně F
            for (int i = 0; i < readedData[row].length; i++) {
                //Ve funkci je známý terminál z dat
                terminals.add(readedData[0][i].charAt(0));
                terminalsValues.add(readedData[row][i]);
//                System.out.println("Terminál " + terminals.get(i) + " má hodnotu: " + terminalsValues.get(i));
            }
            //Evaluate expression
            ScriptEngineManager factory = new ScriptEngineManager();
            ScriptEngine engine = factory.getEngineByName("JavaScript");
            String result = "";
            try {
                String command = "";
                //Taky nově po terminálech
                for (int i = 0; i < terminals.size(); i++) {
                    //ZDE MUSÍ DOJÍT K ROZLIŠENÍ LOGIKY NÁLEZU
                    try {
                        Double.parseDouble(terminalsValues.get(i));
                    } catch (NumberFormatException e) {
                        terminalsValues.set(i, "NaN");
                    }
                    if (!terminalsValues.get(i).isEmpty() || !terminalsValues.get(i).equals("NaN")) {
                        command += "var " + terminals.get(i) + " = " + terminalsValues.get(i) + ";";
                    } else {
                        command += "var " + terminals.get(i) + ";";
                    }
                }
                command += function;
//                System.out.println("Celý command zní: " + command);
                result = engine.eval(command).toString();
            } catch (ScriptException ex) {
                ex.printStackTrace();
            }

//            System.out.println("Výsledek výpočtu funkce: " + function + " je " + result);
            return result;
        }

    }

    /**
     * Vrátí všechny možné pozice posledních sloupců
     *
     * @return list
     */
    public List<Integer> getPositionAllLastColumms() {
        List<Integer> pom = new ArrayList<>();
        int lastIndex = readedData[0].length - 1;
        for (int i = 0; i < readedData.length; i++) {
            pom.add(lastIndex);
        }
        return pom;
    }

    /**
     * Vrátí jednotlivé indexy všech řádků v souboru
     *
     * @return list
     */
    public List<Integer> getPositionAllRows() {
        List<Integer> pom = new ArrayList<>();
        for (int i = 0; i < readedData.length; i++) {
            pom.add(i);
        }
        return pom;
    }

    /**
     * Vrátí instanci rohraní dle typu souboru
     *
     * @param name název souboru
     * @return Implementace IWritter
     */
    private IWriter resolveExtension(String name) {
        switch (FilenameUtils.getExtension(name)) {
            case "xlsx":
                return new XLSXWriter();
            case "csv":
                return new CSVWriter();
            default:
                return null;
        }
    }

    /**
     * Vrátí kopii původního zadaného souboru a přidá mu zadaný jméno
     *
     * @param originFile soubor
     * @param name jméno
     * @return Kopie
     */
    private File copyFile(File originFile, String name) {
        String newName = FilenameUtils.removeExtension(originFile.getName())
                + name + "." + FilenameUtils.getExtension(originFile.getName());
        File newFile = new File(newName);
        try {
            newFile.createNewFile();
            FileUtils.copyFile(file, newFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return newFile;

    }

    /**
     * Rozhodne o způsobu opravy souboru
     *
     * @param function funkce
     */
    public void resolveGPReconstruct(String function) {
        IWriter iw = resolveExtension(file.getName());

        if (repairFile && reconstructFile) {
            if (copyFile) {
                if (iw.copy(repairAllRow(function), getPositionAllLastColumms(), getPositionAllRows(), copyFile(file, "COPY"))) {
                    JOptionPane.showMessageDialog(null, Localizator.getString("filehandler.file.writed"));
                } else {
                    JOptionPane.showMessageDialog(null, Localizator.getString("filehandler.file.notwrited"));
                }
            } else {
                if (iw.update(repairAllRow(function), getPositionAllLastColumms(), getPositionAllRows(), file)) {
                    JOptionPane.showMessageDialog(null, Localizator.getString("filehandler.file.writed"));
                } else {
                    JOptionPane.showMessageDialog(null, Localizator.getString("filehandler.file.notwrited"));
                }
            }
        } else if (copyFile) {
            if (iw.copy(reparSpecificRows(function, errorRowPositions), errorColummPosition, errorRowPositions, copyFile(file, "COPY"))) {
                JOptionPane.showMessageDialog(null, Localizator.getString("filehandler.file.writed"));
            } else {
                JOptionPane.showMessageDialog(null, Localizator.getString("filehandler.file.notwrited"));
            }
        } else if (repairFile) {
            if (iw.update(reparSpecificRows(function, errorRowPositions), errorColummPosition, errorRowPositions, file)) {
                JOptionPane.showMessageDialog(null, Localizator.getString("filehandler.file.writed"));
            } else {
                JOptionPane.showMessageDialog(null, Localizator.getString("filehandler.file.notwrited"));
            }
        }

    }

    /**
     * Vyresetuje známé objevené chyby předešlého souboru
     */
    public void reset() {
        this.errorColummPosition.clear();
        this.errorRowPositions.clear();
    }

    /**
     * Vytvoří a zapíše report do souboru
     *
     * @param reports
     * @param outputPdf
     * @param openFileAfter
     * @param createPdfOutput
     * @param exp
     */
    public void createReport(List<String> reports, List<String> outputPdf, boolean openFileAfter, boolean createPdfOutput, String exp) {
        if (exp.equals("txt")) {
            File fileNew = new File("Report.txt");
            System.out.println("PRINT LOG");
            try {
                FileUtils.writeLines(fileNew, reports);
                System.out.println("LOG SAVED");
            } catch (IOException ex) {
                ex.printStackTrace();
                Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (openFileAfter) {
                ProcessBuilder pb = new ProcessBuilder("Notepad.exe", "Report.txt");
                try {
                    pb.start();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        if (createPdfOutput) {
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);
            System.out.println("PRINT PDF");
            int newLinePost = 745;
            try {
                PDPageContentStream contentStream = new PDPageContentStream(document, page);
                //contentStream.setFont(PDType1Font.COURIER, 12);

                PDFont pdfFont = PDType1Font.HELVETICA;
                float fontSize = 12;
                float leading = 1.5f * fontSize;

                PDRectangle mediabox = page.getMediaBox();
                float margin = 72;
                float width = mediabox.getWidth() - 2 * margin;
                float startX = mediabox.getLowerLeftX() + margin;
                float startY = mediabox.getUpperRightY() - margin;

                //contentStream.setFont(PDType1Font.TIMES_ROMAN, 16);
                List<String> lines = new ArrayList<String>();
                for (String text : outputPdf) {
                    int lastSpace = -1;
                    while (text.length() > 0) {
                        int spaceIndex = text.indexOf(' ', lastSpace + 1);
                        if (spaceIndex < 0) {
                            spaceIndex = text.length();
                        }
                        String subString = text.substring(0, spaceIndex);
                        float size = fontSize * pdfFont.getStringWidth(subString) / 1000;
                        //System.out.printf("'%s' - %f of %f\n", subString, size, width);
                        if (size > width) {
                            if (lastSpace < 0) {
                                lastSpace = spaceIndex;
                            }
                            subString = text.substring(0, lastSpace);
                            lines.add(subString);
                            text = text.substring(lastSpace).trim();
                            //System.out.printf("'%s' is line\n", subString);
                            lastSpace = -1;
                        } else if (spaceIndex == text.length()) {
                            lines.add(text);
                            //System.out.printf("'%s' is line\n", text);
                            text = "";
                        } else {
                            lastSpace = spaceIndex;
                        }
                    }
                }
                //Setting the position for the line 
                //contentStream.newLineAtOffset(25, newLinePost);
                contentStream.beginText();
                contentStream.setFont(pdfFont, fontSize);
                contentStream.newLineAtOffset(startX, startY);
                float currentY = startY;
                for (String line : lines) {
                    currentY -= leading;

                    if (currentY <= margin) {

                        contentStream.endText();
                        contentStream.close();
                        PDPage new_Page = new PDPage();
                        document.addPage(new_Page);
                        contentStream = new PDPageContentStream(document, new_Page);
                        contentStream.beginText();
                        contentStream.setFont(pdfFont, fontSize);
                        contentStream.newLineAtOffset(startX, startY);
                        currentY = startY;
                    }
                    contentStream.showText(line);
                    contentStream.newLineAtOffset(0, -leading);
                }
                contentStream.endText();
                contentStream.close();

                document.save("Output.pdf");
                System.out.println("PDF OUTPUT SAVED");
                document.close();

                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().open(new File("Output.pdf"));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

            } catch (IOException ex) {
                ex.printStackTrace();
                Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
