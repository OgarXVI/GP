/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.model;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

/**
 * FXML Controller class
 *
 * @author OgarXVI
 */
public class FunctionWindowController implements Initializable {

    @FXML
    private CheckBox checkBoxPlus;
    @FXML
    private CheckBox checkBoxMinus;
    @FXML
    private CheckBox checkBoxTimes;
    @FXML
    private CheckBox checkBoxDivision;
    @FXML
    private CheckBox checkBoxExp;
    @FXML
    private CheckBox checkBoxSqrt;
    @FXML
    private CheckBox checkBoxSin;
    @FXML
    private CheckBox checkBoxCos;
    @FXML
    private CheckBox checkBoxCotg;
    @FXML
    private CheckBox checkBoxTg;
    @FXML
    private CheckBox checkBoxAbs;
    @FXML
    private CheckBox checkBoxLog2;
    @FXML
    private CheckBox checkBoxLog10;
    @FXML
    private CheckBox checkBoxLn;
    @FXML
    private CheckBox checkBoxNeg;

    @FXML
    private ComboBox<Category> comboBoxPlus;
    @FXML
    private ComboBox<Category> comboBoxMinus;
    @FXML
    private ComboBox<Category> comboBoxTimes;
    @FXML
    private ComboBox<Category> comboBoxDivision;
    @FXML
    private ComboBox<Category> comboBoxExp;
    @FXML
    private ComboBox<Category> comboBoxSqrt;
    @FXML
    private ComboBox<Category> comboBoxSin;
    @FXML
    private ComboBox<Category> comboBoxCos;
    @FXML
    private ComboBox<Category> comboBoxCotg;
    @FXML
    private ComboBox<Category> comboBoxTg;
    @FXML
    private ComboBox<Category> comboBoxAbs;
    @FXML
    private ComboBox<Category> comboBoxLog2;
    @FXML
    private ComboBox<Category> comboBoxLog10;
    @FXML
    private ComboBox<Category> comboBoxLn;
    @FXML
    private ComboBox<Category> comboBoxNeg;

    //
    private List<ComboBox<Category>> listComboBoxs;
    private List<CheckBox> listCheckBox;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //ADD INTO LISTS
        listComboBoxs = populateListComboBox();
        listCheckBox = populateListCheckBox();
        //POPULATE COMBOBOXS
        populateComboBoxs();
        //DISABLE ALL
        //disableAllComboBoxs();
        //SET LISTENERS ON CHECKBOXS
        setListenerOnEveryCheckBox();
        // CHECK ALL CHECKBOXS
        checkAllCheckBoxs();
        

    }

    private void populateComboBoxs() {
        listComboBoxs.forEach((listComboBox) -> {
            listComboBox.getItems().setAll(Category.values());
        });
    }

    private void disableAllComboBoxs() {
        listComboBoxs.forEach((listComboBox) -> {
            listComboBox.setDisable(true);
        });
    }

    private void checkAllCheckBoxs() {
        listCheckBox.forEach((checkBox) -> {
            checkBox.selectedProperty().set(true);
        });
    }

    private List<ComboBox<Category>> populateListComboBox() {
        ArrayList<ComboBox<Category>> pomArray = new ArrayList();
        pomArray.add(comboBoxPlus);
        pomArray.add(comboBoxMinus);
        pomArray.add(comboBoxTimes);
        pomArray.add(comboBoxDivision);
        pomArray.add(comboBoxSin);
        pomArray.add(comboBoxCos);
        pomArray.add(comboBoxCotg);
        pomArray.add(comboBoxTg);
        pomArray.add(comboBoxExp);
        pomArray.add(comboBoxSqrt);
        pomArray.add(comboBoxAbs);
        pomArray.add(comboBoxLog2);
        pomArray.add(comboBoxLog10);
        pomArray.add(comboBoxLn);
        pomArray.add(comboBoxNeg);
        return pomArray;
    }

    private List<CheckBox> populateListCheckBox() {
        ArrayList<CheckBox> pomArray = new ArrayList();
        pomArray.add(checkBoxPlus);
        pomArray.add(checkBoxMinus);
        pomArray.add(checkBoxTimes);
        pomArray.add(checkBoxDivision);
        pomArray.add(checkBoxSin);
        pomArray.add(checkBoxCos);
        pomArray.add(checkBoxCotg);
        pomArray.add(checkBoxTg);
        pomArray.add(checkBoxExp);
        pomArray.add(checkBoxSqrt);
        pomArray.add(checkBoxAbs);
        pomArray.add(checkBoxLog2);
        pomArray.add(checkBoxLog10);
        pomArray.add(checkBoxLn);
        pomArray.add(checkBoxNeg);
        return pomArray;
    }

    private void setListenerOnEveryCheckBox() {
        checkBoxPlus.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (comboBoxPlus.getItems().size() > 0) {
                comboBoxPlus.disableProperty().setValue(oldValue);
                if (!comboBoxPlus.disableProperty().get()){
                    comboBoxPlus.getSelectionModel().selectFirst();
                }
            }
        });
        checkBoxMinus.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (comboBoxMinus.getItems().size() > 0) {
                comboBoxMinus.disableProperty().setValue(oldValue);
                if (!comboBoxMinus.disableProperty().get()){
                    comboBoxMinus.getSelectionModel().selectFirst();
                }
            }
        });
        checkBoxTimes.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (comboBoxTimes.getItems().size() > 0) {
                comboBoxTimes.disableProperty().setValue(oldValue);
                if (!comboBoxTimes.disableProperty().get()){
                    comboBoxTimes.getSelectionModel().selectFirst();
                }
            }
        });
        checkBoxDivision.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (comboBoxDivision.getItems().size() > 0) {
                comboBoxDivision.disableProperty().setValue(oldValue);
                if (!comboBoxDivision.disableProperty().get()){
                    comboBoxDivision.getSelectionModel().selectFirst();
                }
            }
        });
    }

    public enum Category {
        ONE("1"),
        TWO("2"),
        THREE("3"),
        FOUR("4"),
        FIVE("5");

        private String value;

        private Category(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

        @Override
        public String toString() {
            return this.value;
        }

    }

}
