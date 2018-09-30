package cz.ogarxvi.model;

import cz.ogarxvi.genetic.Fitness;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class FXMLController implements Initializable {

    DataHandler dh;
    Messenger m;
    @FXML
    private TableView<String[]> TableView;
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
    private TextArea ConsoleOutput;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        m = new Messenger(ConsoleOutput);
        dh = new DataHandler();
    }

    @FXML
    private void CloseFromMenuItem(ActionEvent event) {
        //CLOSE APPLICATION, BUT NOT LIKE THIS!! TODO:
        System.exit(0);
    }

    @FXML
    private void LoadXLS(ActionEvent event) throws URISyntaxException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()));
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            //TODO: FILL TABLE FOR VIEW
            XLSXReader xlsR = new XLSXReader(m, TableView);
            xlsR.ReadXLSX(selectedFile);
            dh.parseData(xlsR.GetData());
        }
    }

    @FXML
    private void LoadCSV(ActionEvent event) throws URISyntaxException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()));
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            //TODO: FILL TABLE FOR VIEW
            CSVReader xlsR = new CSVReader(m, TableView);
            xlsR.ReadCSV(selectedFile);
            dh.parseData(xlsR.GetData());
        }
    }

    @FXML
    private void StartCalculation(ActionEvent event) {

            //TODO: for testing
            XLSXReader xlsR = new XLSXReader(m, TableView);
            xlsR.ReadXLSX(new File("C:/Users/OgarXVI/Documents/NetBeansProjects/GP/GP_TEST.xlsx"));
            dh.parseData(xlsR.GetData());
        
        if (!dh.isLoaded()) {
            JOptionPane.showMessageDialog(null, "Load any data, please.");
            event.consume();
            return;
        }

        int pocetGeneraci = 
                Integer.valueOf(PopulationSIzeTextField.getText().isEmpty()?
                        PopulationSIzeTextField.getPromptText():PopulationSIzeTextField.getText());
        int velikostPocatecniPopulace = 
                Integer.valueOf(NumberOfGenerationsTextField.getText().isEmpty()?
                        NumberOfGenerationsTextField.getPromptText():NumberOfGenerationsTextField.getText());
        int maximalniInicializacniHloubkaStromu =
                Integer.valueOf(TreeMaxInicializationDepthTextField.getText().isEmpty()?
                        TreeMaxInicializationDepthTextField.getPromptText():TreeMaxInicializationDepthTextField.getText());
        int maximalniHloubkaStromuPoKrizeni = 
                Integer.valueOf(TreeMaxDepthAfterOperationTextField.getText().isEmpty()?
                        TreeMaxDepthAfterOperationTextField.getPromptText():TreeMaxDepthAfterOperationTextField.getText());
        double krizeni = 
                Double.valueOf(CrossingProbabilityTextField.getText().isEmpty()?
                        CrossingProbabilityTextField.getPromptText():CrossingProbabilityTextField.getText());
        double reprodukce = 
                Double.valueOf(ReproductionProbabilityTextField.getText().isEmpty()?
                        ReproductionProbabilityTextField.getPromptText():ReproductionProbabilityTextField.getText());
        double mutace = 
                Double.valueOf(MutationProbabilityTextField.getText().isEmpty()?
                        MutationProbabilityTextField.getPromptText():MutationProbabilityTextField.getText());
        double krizeniVUzluFunkce = 0.0f;
        boolean zachovavatNejzdatnejsihoJedince = ElitistToogleButton.isSelected();
        boolean decimace = false;
        boolean editace = true;
        int pocetKroku = 1;
        int metodaInicializace = 0;

        GPController demo = new GPController(
                m,
                dh,
                PauseButton,
                StopButton,
                pocetGeneraci,
                velikostPocatecniPopulace,
                maximalniInicializacniHloubkaStromu,
                maximalniHloubkaStromuPoKrizeni,
                krizeni,
                reprodukce,
                mutace,
                krizeniVUzluFunkce,
                zachovavatNejzdatnejsihoJedince,
                decimace,
                editace,
                pocetKroku,
                metodaInicializace);
        demo.start();

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
