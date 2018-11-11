/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import javax.swing.JOptionPane;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Třída implemetující IReader, umožňuje načítat soubory typu .XLSX
 * Načte tabulku dat a nastaví potřebné hlavičky
 * @author OgarXVI
 */
public class XLSXReader implements IReader {
    /**
     * Tabulka dat
     */
    private String[][] xlsxData;
    /**
     * Tabulka na zobrazení a ovládání
     */
    private TableView<String[]> tv;
    /**
     * Načítač souboru a jeho zpracování na tabulku
     * @param tv Tabulka pro ovládání a zobrazení
     */
    public XLSXReader(TableView<String[]> tv) {
        this.tv = tv;
        this.xlsxData = new String[3][3];
    }

    @Override
    public void ReadFile(File file) {
        try {
            //Připravení potřebných proměnných pro zpracování souboru
            FileInputStream fis = new FileInputStream(file);
            XSSFWorkbook myWorkBook = new XSSFWorkbook(fis);
            XSSFSheet sheet = myWorkBook.getSheetAt(0);
            XSSFRow row;
            XSSFCell cell;

            int rows;
            rows = sheet.getPhysicalNumberOfRows();

            int cols = 0;
            int tmp = 0;
            // Zjištění počtu sloupců
            for (int i = 0; i < 10 || i < rows; i++) {
                row = sheet.getRow(i);
                if (row != null) {
                    tmp = sheet.getRow(i).getPhysicalNumberOfCells();
                    if (tmp > cols) {
                        cols = tmp;
                    }
                }
            }
            // vytvoření prostoru pro data
            xlsxData = new String[rows][cols];
            // naplnění tabulky datys
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
            // vyčištění tabulky pro případ předchozího naplnění
            tv.getColumns().clear();
            tv.getItems().clear();
            tv.getSelectionModel().clearSelection();
            // Naplnění tabulky
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
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(null, "Cannot load file");
        }
    }

    @Override
    public String[][] GetData() {
        return this.xlsxData;
    }

}
