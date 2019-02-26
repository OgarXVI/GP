/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.controller;

import com.sun.istack.internal.localization.Localizable;
import cz.ogarxvi.model.DataHandler;
import cz.ogarxvi.model.Localizator;
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
public class SettingWindowController implements Initializable {

    @FXML
    private CheckBox checkBoxPrintGeneration;
    @FXML
    private CheckBox checkBoxPrintPopulation;
    @FXML
    private CheckBox checkBoxCreateFile;
    @FXML
    private CheckBox checkBoxOpenFileAfter;
    @FXML
    private Button buttonOK;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
//        checkBoxPrintGeneration.setText(Localizator.getString(""));
//        checkBoxPrintPopulation.setText(Localizator.getString(""));
//        checkBoxCreateFile.setText(Localizator.getString(""));
//        checkBoxOpenFileAfter.setText(Localizator.getString(""));
        checkBoxOpenFileAfter.disableProperty().bind(checkBoxCreateFile.selectedProperty().not());
    }    
    
    @FXML
    private void ButtonOKClose(ActionEvent event) {
        //DATAHANDLER
        DataHandler.getInstance().setPrintGeneration(checkBoxPrintGeneration.selectedProperty().get());
        DataHandler.getInstance().setPrintPopulation(checkBoxPrintPopulation.selectedProperty().get());
        DataHandler.getInstance().setCreateFile(checkBoxCreateFile.selectedProperty().get());
        DataHandler.getInstance().setOpenFileAfter(checkBoxOpenFileAfter.selectedProperty().get());
        //Close
        Stage stage = (Stage) buttonOK.getScene().getWindow();
        stage.close();
    }
}
