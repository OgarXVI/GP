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
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
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
    private TextArea textAreaExample;
    @FXML
    private Label labelExample;
    @FXML
    private Tooltip tooltipActiveTerminals1;

    private String integersText = "Integers";
    private String realNumbersText = "Real numbers";
    private boolean stateNumbers = false; //Default false like integers
    private double increment = 0.1d;
    private List<String> generatedRawTerminals;

    @FXML
    private Slider sliderMin;
    @FXML
    private Slider sliderMax;

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
        generatedRawTerminals = new ArrayList<>();
        initTextArea();
        setListenerOnToogle();
        setListenerOnSlider();
    }

    /**
     * Vrátí všechny vybrané terminály
     *
     * @return vybrané terminály
     */
    private ObservableList<String> getSelectedTerminals() {
        ArrayList<String> pom = new ArrayList<>();
        if (stateNumbers) {
            for (double i = sliderMin.valueProperty().doubleValue(); i < sliderMax.valueProperty().doubleValue(); i = i + increment) {
                pom.add(String.valueOf(i));
            }
        } else {
            for (int i = sliderMin.valueProperty().intValue(); i < sliderMax.valueProperty().intValue(); i++) {
                pom.add(String.valueOf(i));
            }
        }
        return FXCollections.observableArrayList(pom);
    }
    /**
     *
     * @param event
     */
    @FXML
    private void ButtonOKClose(ActionEvent event) {
        //SET TERMINALS
        DataHandler.getInstance().setAllCheckedTerminals(generatedRawTerminals);
        //UPDATE
        Messenger.getInstance().Update();
        //CLOSE
        Stage stage = (Stage) buttonOk.getScene().getWindow();
        event.consume();
        stage.close();
    }
    /**
     * init
     */
    private void initTextArea() {
        textAreaExample.setEditable(false);
        updateTextArea();
    }
    /**
     * update
     */
    private void updateTextArea() {
        generatedRawTerminals.clear();
        textAreaExample.setText("");
        for (String selectedTerminal : getSelectedTerminals()) {
            generatedRawTerminals.add(selectedTerminal);
            textAreaExample.appendText(selectedTerminal);
            textAreaExample.appendText(" ");
        }
    }
    /**
     * set
     */
    private void setListenerOnToogle() {
        toogleSwitchSwap.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    toogleSwitchSwap.setText(realNumbersText);
                    stateNumbers = true;
                } else {
                    toogleSwitchSwap.setText(integersText);
                    stateNumbers = false;
                }
                updateTextArea();
            }
        });
    }
    /**
     * set listener
     */
    private void setListenerOnSlider() {
        sliderMax.valueProperty().addListener(o -> {
            updateTextArea();
        }
        );
        sliderMin.valueProperty().addListener(o -> {
            updateTextArea();
        }
        );
    }
}
