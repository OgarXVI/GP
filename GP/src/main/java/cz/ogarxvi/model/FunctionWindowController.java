/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.model;

import cz.ogarxvi.model.DataHandler.FunctionCategory;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;

/**
 * FXML Controller class
 *
 * @author OgarXVI
 */
public class FunctionWindowController implements Initializable {

    @FXML
    private Button buttonStorno;
    @FXML
    private Button buttonOK;
    @FXML
    private ComboBox<Integer> comboBoxBinaryIterations;
    @FXML
    private ComboBox<Integer> comboBoxUnaryIterations;
    @FXML
    private ComboBox<Integer> comboBoxTrigonometricIterations;
    /**
     * Initializes the controller class.
     * @param url URL
     * @param rb ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        populateComboBoxs();
        setDefaultSelectedItems();
    }
    /**
     * Nastaví první hodnoty jako vybrané hodnoty
     */
    private void setDefaultSelectedItems(){
        comboBoxBinaryIterations.getSelectionModel().select(
                DataHandler.getInstance().getNumberIterationsCategory()[FunctionCategory.BINARY.category]);
        comboBoxUnaryIterations.getSelectionModel().select(
                DataHandler.getInstance().getNumberIterationsCategory()[FunctionCategory.UNARY.category]);
        comboBoxTrigonometricIterations.getSelectionModel().select(
                DataHandler.getInstance().getNumberIterationsCategory()[FunctionCategory.TRIGONOMETRIK.category]);
    }
    
    /**
     * Naplní comboboxy daty pro selekci
     */
    private void populateComboBoxs(){
        for (int i = 0; i <= 10; i++) {
            comboBoxBinaryIterations.getItems().add(i);
            comboBoxUnaryIterations.getItems().add(i);
            comboBoxTrigonometricIterations.getItems().add(i);
        }
    }
    
    @FXML
    private void ClickButtonStorno(ActionEvent event) {
        Stage stage = (Stage) buttonStorno.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void ClickButtonOK(ActionEvent event) {
        //SEND INFO INTO DATAHANDLER
        DataHandler.getInstance().AddFunctions(
                comboBoxBinaryIterations.getSelectionModel().getSelectedItem(),
                FunctionCategory.BINARY.category);
        DataHandler.getInstance().AddFunctions(
                comboBoxUnaryIterations.getSelectionModel().getSelectedItem(),
                FunctionCategory.UNARY.category);
        DataHandler.getInstance().AddFunctions(
                comboBoxTrigonometricIterations.getSelectionModel().getSelectedItem(),
                FunctionCategory.TRIGONOMETRIK.category);
        
        Messenger.getInstance().ClearMessenger();
        if (DataHandler.getInstance().getLoadedDataFile() != null) {
            Messenger.getInstance().AddMesseage(Localizator.getString("output.expectedFunctions") + 
                    FilenameUtils.getBaseName(DataHandler.getInstance().getLoadedDataFile().getName()));
            Messenger.getInstance().AddMesseage(Localizator.getString("output.rows") +
                    DataHandler.getInstance().getTableRows());
        }
        Messenger.getInstance().AddMesseage(Localizator.getString("output.terminals") +
                DataHandler.getInstance().getLoadedTerminals());
        Messenger.getInstance().AddMesseage(Localizator.getString("output.functions") +
                DataHandler.getInstance().getAllFunctionsAsString());
        Messenger.getInstance().GetAllMesseages();
        
        //Close
        Stage stage = (Stage) buttonStorno.getScene().getWindow();
        stage.close();
    }
    
}
