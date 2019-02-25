/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.controller;

import cz.ogarxvi.model.DataHandler;
import cz.ogarxvi.model.Localizator;
import cz.ogarxvi.model.Messenger;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import org.controlsfx.control.CheckListView;
import org.controlsfx.control.textfield.CustomTextField;

/**
 * FXML Controller class
 *
 * @author OgarXVI
 */
public class TerminalWindowController implements Initializable {

    /**
     * CustomTextField
     */
    @FXML
    private CustomTextField terminalTextField;
    /**
     * Tooltip
     */
    @FXML
    private Tooltip tooltipTextFieldTerminal;
    /**
     * Button
     */ 
    @FXML
    private Button buttonAdd;
    /**
     * Tooltip
     */
    @FXML
    private Tooltip tooltipButtonAddTerminal;
    /**
     * Button
     */
    @FXML
    private Button buttonOk;
    /**
     * Tooltip
     */
    @FXML
    private Tooltip tooltipActiveTerminals;
    /**
     * Tooltip
     */
    @FXML
    private Tooltip tooltipButtonRemoveTerminal;
    /**
     * Button
     */
    @FXML
    private Button buttonRemoveTerminal;
    /**
     * Check List View
     */
    @FXML
    private CheckListView<String> checklistViewTerminals;

    /**
     * Initializes the controller class.
     * @param url url
     * @param rb rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Localizator
        tooltipButtonRemoveTerminal.setText(Localizator.getString("tooltip.button.removeTerminal"));
        buttonRemoveTerminal.setText(Localizator.getString("button.remove"));
        tooltipActiveTerminals.setText(Localizator.getString("tooltip.activeTerminals"));
        tooltipButtonAddTerminal.setText(Localizator.getString("tooltip.button.addTerminal"));
        tooltipTextFieldTerminal.setText(Localizator.getString("tooltip.textfield.terminalInput"));
        buttonAdd.setText(Localizator.getString("button.add"));
        //GET ALL TERMINALS
        initDefaultTerminals();
        //SET CHECK ALL
        setCheckedItems();
    }

    /**
     * Nastaví výchozí hodnoty check list view
     */
    private void initDefaultTerminals() {
        checklistViewTerminals.getItems().clear();
        checklistViewTerminals.getItems().addAll(DataHandler.getInstance().getAllTerminals());
        checklistViewTerminals.getItems().add("-5");
        checklistViewTerminals.getItems().add("-4");
        checklistViewTerminals.getItems().add("-3");
        checklistViewTerminals.getItems().add("-2");
        checklistViewTerminals.getItems().add("-1");
        checklistViewTerminals.getItems().add("0");
        checklistViewTerminals.getItems().add("1");
        checklistViewTerminals.getItems().add("2");
        checklistViewTerminals.getItems().add("3");
        checklistViewTerminals.getItems().add("4");
        checklistViewTerminals.getItems().add("5");
    }

    /**
     * Check all previsously checked items
     */
    private void setCheckedItems() {
        for (String item : DataHandler.getInstance().getAllCheckedTerminals()) {
            checklistViewTerminals.getCheckModel().check(item);
        }
    }

    /**
     * Vrátí všechny vybrané terminály
     * @return vybrané terminály
     */
    public ObservableList<String> getSelectedTerminals() {
        return checklistViewTerminals.getItems();
    }

    /**
     * Kontroluje text
     * @param inputText vstupní text
     * @return true pokud text je konvertovatelný na číslo
     */
    private boolean validateText(String inputText) {
        try {
            Double.parseDouble(inputText); //CHECK TEST
        } catch (Exception e) {
            //
            JOptionPane.showMessageDialog(null, Localizator.getString("warning.wrongInput"));
            return false;
        }
        return true;
    }

    /**
     *
     * @param event
     */
    @FXML
    private void ButtonAddClick(ActionEvent event) {
        if (validateText(terminalTextField.getText())) {
            checklistViewTerminals.getItems().add(terminalTextField.getText());
        }
    }

    /**
     *
     * @param event
     */
    @FXML
    private void ButtonOKClose(ActionEvent event) {
        //SET TERMINALS
        DataHandler.getInstance().setAllCheckedTerminals(checklistViewTerminals.getCheckModel().getCheckedItems());
        DataHandler.getInstance().setAllTerminals(checklistViewTerminals.getItems());
        //UPDATE
        Messenger.getInstance().Update();
        //CLOSE
        Stage stage = (Stage) buttonOk.getScene().getWindow();
        event.consume();
        stage.close();
    }

    /**
     *
     * @param event
     */
    @FXML
    private void buttonRemoveTerminalClick(ActionEvent event) {
        checklistViewTerminals.getItems().remove(checklistViewTerminals.getSelectionModel().getSelectedIndex());
    }

}
