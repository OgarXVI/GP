/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.CustomTextField;

/**
 * FXML Controller class
 *
 * @author OgarXVI
 */
public class TerminalWindowController implements Initializable {

    @FXML
    private CustomTextField terminalTextField;
    @FXML
    private Tooltip tooltipTextFieldTerminal;
    @FXML
    private Button buttonAdd;
    @FXML
    private Tooltip tooltipButtonAddTerminal;
    @FXML
    private Button buttonOk;
    @FXML
    private Label labelActiveTerminals;
    @FXML
    private Tooltip tooltipActiveTerminals;
    @FXML
    private Tooltip tooltipButtonRemoveTerminal;
    @FXML
    private ListView<String> lisViewTerminals;
    @FXML
    private Button buttonRemoveTerminal;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Localizator
//        tooltipButtonRemoveTerminal 
//                tooltipActiveTerminals
//        tooltipButtonAddTerminal 
//                tooltipTextFieldTerminal 
// TODO
    }

    @FXML
    private void ButtonAddClick(ActionEvent event) {
        lisViewTerminals.getItems().add(terminalTextField.getText());
    }

    @FXML
    private void NuttonOKClose(ActionEvent event) {
        Stage stage = (Stage) buttonOk.getScene().getWindow();
        event.consume();
        stage.close();
    }

    @FXML
    private void buttonRemoveTerminalClick(ActionEvent event) {
        lisViewTerminals.getItems().remove(lisViewTerminals.getSelectionModel().getSelectedIndex());
    }

}
