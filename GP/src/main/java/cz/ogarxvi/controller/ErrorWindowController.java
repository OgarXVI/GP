/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.controller;

import cz.ogarxvi.model.FileHandler;
import cz.ogarxvi.model.Localizator;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.ToggleSwitch;

/**
 * FXML Controller class
 *
 * @author OgarXVI
 */
public class ErrorWindowController implements Initializable {
    /**
     * Table View Error
     */
    @FXML
    private TableView<String[]> errorsTableView;
    /**
     * Toggle Switch
     */
    @FXML
    private ToggleSwitch toggleSwitchFixErrors;
    /**
     * Tooltip
     */
    @FXML
    private Tooltip tooltipFixErrors;
    /**
     * Toggle Switch
     */
    @FXML
    private ToggleSwitch toggleSwitchNewFIle;
    /**
     * Tooltip
     */
    @FXML
    private Tooltip tooltipNewFile;
    /**
     * Toggle Switch
     */
    @FXML
    private ToggleSwitch toggleSwitchRepairAll;
    /**
     * Tooltip
     */
    @FXML
    private Tooltip tooltipRepairAll;
    /**
     * Button
     */
    @FXML
    private Button buttonOK;

    /**
     * Initializes the controller class.
     * @param url url
     * @param rb rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //tooltips localization
        tooltipFixErrors.setText(Localizator.getString("tooltip.toggleSwitchFixErrors"));
        tooltipNewFile.setText(Localizator.getString("tooltip.toggleSwitchNewFIle"));
        tooltipRepairAll.setText(Localizator.getString("tooltip.toggleSwitchRepairAll"));
        toggleSwitchRepairAll.setText(Localizator.getString("toogleSwitch.text.repairAllRow"));
        //bind
        //toggleSwitchNewFIle.disableProperty().set(true);
        toggleSwitchNewFIle.disableProperty().bind(toggleSwitchFixErrors.selectedProperty());
        toggleSwitchNewFIle.disableProperty().bind(toggleSwitchFixErrors.selectedProperty().not());
        toggleSwitchRepairAll.disableProperty().bind(toggleSwitchFixErrors.selectedProperty());
        toggleSwitchRepairAll.disableProperty().bind(toggleSwitchFixErrors.selectedProperty().not());
        //Next bind level:
    }
    /**
     * Naastaví parametry FileHandler instance a ukončí okno
     * @param event 
     */
    @FXML
    private void buttonOkContinue(ActionEvent event) {
        //setValues
        boolean fixOriginalFile = (toggleSwitchFixErrors.isSelected() && !toggleSwitchNewFIle.isSelected());
        boolean fixCopyFile = toggleSwitchNewFIle.isSelected();
        boolean repairFile = toggleSwitchRepairAll.isSelected();

        FileHandler.getInstance().copyFile = fixCopyFile;
        FileHandler.getInstance().repairFile = fixOriginalFile;
        FileHandler.getInstance().reconstructFile = repairFile;

        //close
        Stage stage = (Stage) buttonOK.getScene().getWindow();
        stage.close();
    }
    /**
     * Parsuje data
     * @param readData Data na čtení
     * @param errorRows seznam řádků s chybou
     */
    public void fillData(String[][] readData, List<Integer> errorRows) {
        String[][] newErrorData = new String[errorRows.size()+2][readData[0].length];
        newErrorData[0] = readData[0];
        for (int i = 1; i < errorRows.size()+1; i++) {
            newErrorData[i] = readData[errorRows.get(i-1)+1];
        }
        errorsTableView.getColumns().clear();
        errorsTableView.getItems().clear();
        errorsTableView.getSelectionModel().clearSelection();
        ObservableList<String[]> data = FXCollections.observableArrayList();
        data.addAll(Arrays.asList(newErrorData));
        data.remove(0);
        for (int i = 0; i < newErrorData[0].length; i++) {
            TableColumn tc = new TableColumn(newErrorData[0][i]);
            final int colNo = i;
            tc.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<String[], String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<String[], String> p) {
                    return new SimpleStringProperty((p.getValue()[colNo]));
                }
            });
            tc.setPrefWidth(90);
            errorsTableView.getColumns().add(tc);
        }
        errorsTableView.setItems(data);

    }


}
