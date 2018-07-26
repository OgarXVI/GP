package cz.ogarxvi.controller;

import cz.ogarxvi.model.Messenger;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

public class FXMLController implements Initializable {

    @FXML
    private TextArea ConsoleOutput;
    @FXML
    private TextField PopulationSIzeTextField;
    @FXML
    private TextField NumberOfGenerationsTextField;
    @FXML
    private TextField ReproductionProbabilityTextField;
    @FXML
    private TextField MutationProbabilityTextField;
    @FXML
    private TextField CrossingProbabilityTextField;
    @FXML
    private TextField TreeMaxDepthAfterOperationTextField;
    @FXML
    private TextField TreeMaxInicializationDepthTextField;
    @FXML
    private ToggleButton ElitistToogleButton;
    @FXML
    private MenuButton SelectionMenu;
    @FXML
    private ComboBox<?> FunctionsComboBox;
    @FXML
    private ComboBox<?> TerminalsComboBox;
    @FXML
    private Button StartButton;
    @FXML
    private Button PauseButton;
    @FXML
    private Button StopButton;
    @FXML
    private TableView<String[]> TableView;
    
    //Thread t = null;
    Messenger m;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        m = new Messenger(ConsoleOutput);
        //TODO: INIT THREAD
    }    

    @FXML
    private void CloseFromMenuItem(ActionEvent event) {
        //CLOSE APPLICATION, BUT NOT LIKE THIS!! TODO:
        System.exit(0);
    }
    
    @FXML
    private void LoadXLS(ActionEvent event) throws URISyntaxException{
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()));
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                
                //TODO: FILL TABLE FOR VIEW
                XLSXReader xlsR = new XLSXReader(m, TableView);
                xlsR.ReadXLSX(selectedFile);
            }
    }
    
    @FXML
    private void LoadCSV(ActionEvent event) throws URISyntaxException{
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()));
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                
                //TODO: FILL TABLE FOR VIEW
                CSVReader xlsR = new CSVReader(m, TableView);
                xlsR.ReadCSV(selectedFile);
            }
    }

    @FXML
    private void StartCalculation(ActionEvent event) {
        m.AddMesseage(PopulationSIzeTextField.getText()==null?PopulationSIzeTextField.getText():PopulationSIzeTextField.getPromptText());
        m.AddMesseage(NumberOfGenerationsTextField.getText()==null?NumberOfGenerationsTextField.getText():NumberOfGenerationsTextField.getPromptText());
        m.AddMesseage(ReproductionProbabilityTextField.getText()==null?ReproductionProbabilityTextField.getText():ReproductionProbabilityTextField.getPromptText());
        m.AddMesseage(MutationProbabilityTextField.getText()==null?MutationProbabilityTextField.getText():MutationProbabilityTextField.getPromptText());
        m.AddMesseage(CrossingProbabilityTextField.getText()==null?CrossingProbabilityTextField.getText():CrossingProbabilityTextField.getPromptText());
        m.AddMesseage(TreeMaxDepthAfterOperationTextField.getText()==null?TreeMaxDepthAfterOperationTextField.getText():TreeMaxDepthAfterOperationTextField.getPromptText());
        m.AddMesseage(TreeMaxInicializationDepthTextField.getText()==null?TreeMaxInicializationDepthTextField.getText():TreeMaxInicializationDepthTextField.getPromptText());
        //TODO:
        m.GetAllMesseages();
    }

    @FXML
    private void PauseCalculation(ActionEvent event) {
        //TODO: THREAD PAUSE
    }

    @FXML
    private void StopCalculation(ActionEvent event) {
        m.ClearMessenger();
        //TODO: STOP THREAD
    }
    
}
