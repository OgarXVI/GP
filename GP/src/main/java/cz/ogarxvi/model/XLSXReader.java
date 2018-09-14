/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.model;

import cz.ogarxvi.model.Messenger;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author OgarXVI
 */
public class XLSXReader {

    private String[][] xlsxData;
    private Messenger m;
    private TableView<String[]> tv;

    public XLSXReader(Messenger m, TableView<String[]> tv) {
        this.m = m;
        this.tv = tv;
    }

    public void ReadXLSX(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            // Finds the workbook instance for XLSX file
            XSSFWorkbook myWorkBook = new XSSFWorkbook (fis);
            // Return first sheet from the XLSX workbook
            XSSFSheet sheet = myWorkBook.getSheetAt(0);
            XSSFRow row;
            XSSFCell cell;

            int rows;
            rows = sheet.getPhysicalNumberOfRows();

            int cols = 0;
            int tmp = 0;

            for (int i = 0; i < 10 || i < rows; i++) {
                row = sheet.getRow(i);
                if (row != null) {
                    tmp = sheet.getRow(i).getPhysicalNumberOfCells();
                    if (tmp > cols) {
                        cols = tmp;
                    }
                }
            }
            xlsxData = new String[rows][cols];

            for (int r = 0; r < rows; r++) {
                row = sheet.getRow(r);
                if (row != null) {
                    for (int c = 0; c < cols; c++) {
                        cell = row.getCell((short) c);
                        if (cell != null) {
                            xlsxData[r][c] = cell.toString();
                        }
                    }
                }
            }
            ObservableList<String[]> data = FXCollections.observableArrayList();
            data.addAll(Arrays.asList(xlsxData));
            data.remove(0);
            for (int i = 0; i < xlsxData[0].length; i++) {
                TableColumn tc = new TableColumn(xlsxData[0][i]);
                final int colNo = i;
                tc.setCellValueFactory(new Callback<CellDataFeatures<String[], String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(CellDataFeatures<String[], String> p) {
                        return new SimpleStringProperty((p.getValue()[colNo]));
                    }
                });
                tc.setPrefWidth(90);
                tv.getColumns().add(tc);
            }
            tv.setItems(data);
        } catch (Exception ioe) {
            ioe.printStackTrace();
            
        }
    }

    public String[][] GetData(){
        return this.xlsxData;
    }
    
}
