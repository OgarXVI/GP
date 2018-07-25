/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.controller;

import cz.ogarxvi.model.Messenger;
import java.io.File;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author OgarXVI
 */
public class XLSXReader {
    
    //TODO: AUTO?
    private int[][] xlsData;

    private Messenger m;
    
    public XLSXReader(Messenger m) {
        this.m = m;
        xlsData = new int[3][3];
    }
    
    public void ReadXLSX(File file){
    try {
    XSSFWorkbook wb = new XSSFWorkbook(file);;
    XSSFSheet sheet = wb.getSheetAt(0);
    XSSFRow row;
    XSSFCell cell;

    int rows; // No of rows
    rows = sheet.getPhysicalNumberOfRows();

    int cols = 0; // No of columns
    int tmp = 0;

    // This trick ensures that we get the data properly even if it doesn't start from first few rows
    for(int i = 0; i < 10 || i < rows; i++) {
        row = sheet.getRow(i);
        if(row != null) {
            tmp = sheet.getRow(i).getPhysicalNumberOfCells();
            if(tmp > cols) cols = tmp;
        }
    }

    
    for(int r = 0; r < rows; r++) {
        row = sheet.getRow(r);
        String tmpS = "";
        if(row != null) {
            for(int c = 0; c < cols; c++) {
                cell = row.getCell((short)c);
                if(cell != null) {
                    tmpS += cell.toString() + " ";
                }
            }
            m.AddMesseage(tmpS);
            m.GetMesseage();
        }
    }
} catch(Exception ioe) {
    ioe.printStackTrace();
}
    }
    
}
