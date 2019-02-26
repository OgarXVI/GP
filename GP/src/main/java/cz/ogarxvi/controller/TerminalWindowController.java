/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.controller;

import com.sun.javafx.property.adapter.PropertyDescriptor;
import cz.ogarxvi.model.DataHandler;
import cz.ogarxvi.model.Localizator;
import cz.ogarxvi.model.Messenger;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import org.controlsfx.control.CheckListView;
import org.controlsfx.control.RangeSlider;
import org.controlsfx.control.ToggleSwitch;
import org.controlsfx.control.textfield.CustomTextField;

/**
 * FXML Controller class
 *
 * @author OgarXVI
 */
public class TerminalWindowController implements Initializable {

//    /**
//     * CustomTextField
//     */
//    private CustomTextField terminalTextField;
//    /**
//     * Tooltip
//     */
//    private Tooltip tooltipTextFieldTerminal;
//    /**
//     * Button
//     */ 
//    private Button buttonAdd;
//    /**
//     * Tooltip
//     */
//    private Tooltip tooltipButtonAddTerminal;
    /**
     * Button
     */
    @FXML
    private Button buttonOk;
//    /**
//     * Tooltip
//     */
//    private Tooltip tooltipActiveTerminals;
//    /**
//     * Tooltip
//     */
//    private Tooltip tooltipButtonRemoveTerminal;
//    /**
//     * Button
//     */
//    private Button buttonRemoveTerminal;
//    /**
//     * Check List View
//     */
//    private CheckListView<String> checklistViewTerminals;
    @FXML
    private ToggleSwitch toogleSwitchSwap;
    @FXML
    private RangeSlider slider;
    @FXML
    private TextArea textAreaExample;
    @FXML
    private Label labelExample;
    @FXML
    private Tooltip tooltipActiveTerminals1;

    private String integersText = "Integers";
    private String realNumbersText = "Real numbers";
    private boolean stateNumnbers = false; //Default false like integers

    /**
     * Initializes the controller class.
     *
     * @param url url
     * @param rb rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Localizator
//        tooltipButtonRemoveTerminal.setText(Localizator.getString("tooltip.button.removeTerminal"));
//        buttonRemoveTerminal.setText(Localizator.getString("button.remove"));
//        tooltipActiveTerminals.setText(Localizator.getString("tooltip.activeTerminals"));
//        tooltipButtonAddTerminal.setText(Localizator.getString("tooltip.button.addTerminal"));
//        tooltipTextFieldTerminal.setText(Localizator.getString("tooltip.textfield.terminalInput"));
//        buttonAdd.setText(Localizator.getString("button.add"));
        //GET ALL TERMINALS
        initRange();
        initTextArea();
        setListenerOnToogle();
        setListenerOnSlider();
    }

    /**
     * Vrátí všechny vybrané terminály
     *
     * @return vybrané terminály
     */
    public ObservableList<String> getSelectedTerminals() {
        ArrayList<String> pom = new ArrayList<>();
        if (stateNumnbers) {
            for (double i = slider.lowValueProperty().intValue(); i < slider.highValueProperty().doubleValue(); i = i + 0.05) {
                pom.add(String.valueOf(i));
            }
        } else {
            for (int i = slider.lowValueProperty().intValue(); i < slider.highValueProperty().doubleValue(); i++) {
                pom.add(String.valueOf(i));
            }
        }
        return FXCollections.observableArrayList(pom);
    }

//    /**
//     * Kontroluje text
//     * @param inputText vstupní text
//     * @return true pokud text je konvertovatelný na číslo
//     */
//    private boolean validateText(String inputText) {
//        try {
//            Double.parseDouble(inputText); //CHECK TEST
//        } catch (Exception e) {
//            //
//            JOptionPane.showMessageDialog(null, Localizator.getString("warning.wrongInput"));
//            return false;
//        }
//        return true;
//    }
//
//    /**
//     *
//     * @param event
//     */
//    private void ButtonAddClick(ActionEvent event) {
//        if (validateText(terminalTextField.getText())) {
//            checklistViewTerminals.getItems().add(terminalTextField.getText());
//        }
//    }
    /**
     *
     * @param event
     */
    @FXML
    private void ButtonOKClose(ActionEvent event) {
        //SET TERMINALS
//        DataHandler.getInstance().setAllCheckedTerminals(checklistViewTerminals.getCheckModel().getCheckedItems());
//        DataHandler.getInstance().setAllTerminals(checklistViewTerminals.getItems());
        //UPDATE
        Messenger.getInstance().Update();
        //CLOSE
        Stage stage = (Stage) buttonOk.getScene().getWindow();
        event.consume();
        stage.close();
    }

//    /**
//     *
//     * @param event
//     */
//    private void buttonRemoveTerminalClick(ActionEvent event) {
//        checklistViewTerminals.getItems().remove(checklistViewTerminals.getSelectionModel().getSelectedIndex());
//    }
    private void initRange() {
        slider = new RangeSlider(-100, 100, 0, 5);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setBlockIncrement(10);
    }

    private void initTextArea() {
        textAreaExample.setEditable(false);
        updateTextArea();
    }

    private void updateTextArea() {
        for (String selectedTerminal : getSelectedTerminals()) {
            textAreaExample.appendText(selectedTerminal);
            textAreaExample.appendText(" ");
        }
    }

    private void setListenerOnToogle() {
        toogleSwitchSwap.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    toogleSwitchSwap.setText(realNumbersText);
                } else {
                    toogleSwitchSwap.setText(integersText);
                }
            }
        });
    }

    private void setListenerOnSlider() {
        slider.lowValueProperty().addListener(o -> {
            updateTextArea();
        }
        );
        slider.highValueProperty().addListener(o -> {
            updateTextArea();
        }
        );
    }

}
