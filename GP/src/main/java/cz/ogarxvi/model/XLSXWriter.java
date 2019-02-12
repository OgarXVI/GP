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
import java.util.List;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author OgarXVI
 */
public class XLSXWriter implements IWriter {

    /**
     * Aktulizuje soubor xlsx o nové zjištěné hodnoty, na konkrétně zadaných
     * paramerech. Pozor je potřeba dát na dodrření vstupních parametrů.
     *
     * @param replaces list hodnot pro nahrazení
     * @param colums X souřadnice
     * @param rows Y souřadnice
     * @param file soubor pro nahrazení/vytvoření
     * @return true pokud vše proěhlo v pořádku
     */
    @Override
    public boolean update(List<String> replaces, List<Integer> colums, List<Integer> rows, File file) {
        boolean bool = false;
        try {
            //org.apache.poi.xssf.usermodel.XSSFWorkbook wb = new XSSFWorkbook(file);
            //Access the worksheet, so that we can update / modify it.
            try (XSSFWorkbook wb = (XSSFWorkbook) WorkbookFactory.create(new FileInputStream(file))) {
                //org.apache.poi.xssf.usermodel.XSSFWorkbook wb = new XSSFWorkbook(file);
                //Access the worksheet, so that we can update / modify it.
                XSSFSheet sheet = (XSSFSheet) wb.getSheetAt(0);
                for (int i = 0; i < replaces.size(); i++) {
                    int rowStart = sheet.getFirstRowNum();
                    int rowEnd = sheet.getLastRowNum() + 1;
                    for (int rowNum = rowStart; rowNum < rowEnd; rowNum++) {
                        Row r = sheet.getRow(rowNum);
                        if (r == null) {
                            r = sheet.createRow(rowNum);
                        }
                        int firstColumn = r.getFirstCellNum();
                        int lastColumn = r.getLastCellNum();
                        for (int cn = firstColumn; cn < lastColumn; cn++) {
                            if (cn == colums.get(i)) {
                                if (rowNum == (rows.get(i) + 1)) {
                                    Cell c = r.getCell(cn);
                                    if (c == null) {
                                        //c = r.createCell(i);
                                        //continue;
                                    } else {
                                        c.setCellType(CellType.STRING);
                                        c.setCellValue(replaces.get(i));
                                        bool = true;
                                    }
                                }

                            }

                        }
                    }
                }
                //Open FileOutputStream to write updates
                FileOutputStream fileOut = new FileOutputStream(file);
                wb.write(fileOut);
                fileOut.close();
                // Closing the workbook
                return bool;
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InvalidFormatException ex) {
            ex.printStackTrace();
        }
        return bool;
    }

    @Override
    public boolean copy(List<String> replaces, List<Integer> colums, List<Integer> rows, File file) {
        return update(replaces, colums, rows, file);
    }

}
