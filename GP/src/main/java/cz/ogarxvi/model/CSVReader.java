/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import javax.swing.JOptionPane;

/**
 * Třída pro načtení .CSV souboru a jeho zpracování do tabulky
 * Implementuje IReader
 * @author OgarXVI
 */
public class CSVReader implements IReader {
    /**
     * Tabulka dat
     */
    private String[][] csvData;
    /**
     * Tabulka na zobrazení a ovládání
     */
    private TableView<String[]> tv;
    /**
     * Načítač souboru a jeho zpracování na tabulku
     * @param tv Tabulka nazobrazení
     */
    public CSVReader(TableView<String[]> tv) {
        this.tv = tv;
        csvData = new String[3][3];

    }

    @Override
    public void ReadFile(File file) {
        String line = null;
        // Načítá počet řádků
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            int coll = ((line = br.readLine()) != null) ? line.split(",").length : 0;
            int row = 1;
            while ((line = br.readLine()) != null) {
                row++;
            }
            // založení prostoru pro data
            csvData = new String[row][coll];
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, Localizator.getString("warning.error.read") + e.getMessage());
        }
        // vložení dat do připravené tabulky
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            int row = 0;
            while ((line = br.readLine()) != null) {
                String[] cells = line.split(",");
                System.arraycopy(cells, 0, csvData[row], 0, cells.length);
                row++;
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, Localizator.getString("warning.error.read") + e.getMessage());
        }

        tv.getColumns().clear();
        tv.getItems().clear();
        tv.getSelectionModel().clearSelection();
        // načtení dat do tabulky pro zobrazení a ovládání
        ObservableList<String[]> data = FXCollections.observableArrayList();
        data.addAll(Arrays.asList(csvData));
        data.remove(0);
        for (int i = 0; i < csvData[0].length; i++) {
            TableColumn tc = new TableColumn(csvData[0][i]);
            final int colNo = i;
            tc.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<String[], String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<String[], String> p) {
                    return new SimpleStringProperty((p.getValue()[colNo]));
                }
            });
            tc.setPrefWidth(90);
            tv.getColumns().add(tc);
        }
        tv.setItems(data);
    }

    @Override
    public String[][] GetData() {
        return this.csvData;
    }

}
