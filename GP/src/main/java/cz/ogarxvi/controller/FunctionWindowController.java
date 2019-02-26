/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.controller;

import cz.ogarxvi.model.DataHandler;
import cz.ogarxvi.model.DataHandler.FunctionCategory;
import cz.ogarxvi.model.Localizator;
import cz.ogarxvi.model.Messenger;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;

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
    private CheckBox checkBoxBinary;
    @FXML
    private CheckBox checkBoxUnary;
    @FXML
    private CheckBox checkBoxTrigonometric;

    /**
     * Initializes the controller class.
     *
     * @param url URL
     * @param rb ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //buttonStorno.setText(Localizator.getString(""));
        //buttonOK.setText(Localizator.getString(""));
        //buttonStorno.setText(Localizator.getString(""));
        //buttonStorno.setText(Localizator.getString(""));
        setDefaultSelectedItems();
    }

    /**
     * Nastaví první hodnoty jako vybrané hodnoty
     */
    private void setDefaultSelectedItems() {
        checkBoxBinary.setSelected(true);
        checkBoxUnary.setSelected(true);
        checkBoxTrigonometric.setSelected(true);
    }

    @FXML
    private void ClickButtonStorno(ActionEvent event) {
        Stage stage = (Stage) buttonStorno.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void ClickButtonOK(ActionEvent event) {
        //SEND INFO INTO DATAHANDLER
        if (checkBoxBinary.isSelected()) {
            DataHandler.getInstance().AddFunctions(
                    1,
                    FunctionCategory.BINARY.category);
        }
        if (checkBoxUnary.isSelected()) {
            DataHandler.getInstance().AddFunctions(
                    1,
                    FunctionCategory.UNARY.category);
        }
        if (checkBoxTrigonometric.isSelected()) {
            DataHandler.getInstance().AddFunctions(
                    1,
                    FunctionCategory.TRIGONOMETRIK.category);
        }

        Messenger.getInstance().Update();

        //Close
        Stage stage = (Stage) buttonStorno.getScene().getWindow();
        stage.close();
    }

}
