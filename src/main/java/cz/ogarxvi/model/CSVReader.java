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

/**
 *
 * @author OgarXVI
 */
public class CSVReader implements IReader {

    //TODO: AUTO?
    private String[][] csvData;

    private Messenger m;
    private TableView<String[]> tv;

    public CSVReader(Messenger m, TableView<String[]> tv) {
        this.m = m;
        this.tv = tv;
        csvData = new String[3][3];

        tv.getItems().clear();
        tv.getSelectionModel().clearSelection();
    }

    
    public void ReadFile(File file) {
        String line = null;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            int coll = ((line = br.readLine()) != null) ? line.split(",").length : 0;
            int row = 1;
            while ((line = br.readLine()) != null) {
                row++;
            }
            csvData = new String[row][coll];
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            int row = 0;
            while ((line = br.readLine()) != null) {
                String[] cells = line.split(",");
                System.arraycopy(cells, 0, csvData[row], 0, cells.length);
                row++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        tv.getColumns().clear();
        tv.getItems().clear();
        tv.getSelectionModel().clearSelection();

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
