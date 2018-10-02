package cz.ogarxvi.model;

import cz.ogarxvi.genetic.Fitness;
import cz.ogarxvi.genetic.Gen;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.apache.commons.io.FilenameUtils;

public class FXMLController implements Initializable {

    private DataHandler dh;
    private Messenger m;
    private int selectionMethod = 0;
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
    private TextField CrossoverProbabilityTextField;
    @FXML
    private TextField TreeMaxDepthAfterOperationTextField;
    @FXML
    private TextField TreeMaxInicializationDepthTextField;
    @FXML
    private ToggleButton ElitistToogleButton;
    @FXML
    private MenuButton SelectionMenu;
    @FXML
    private ComboBox<DataHandler.BoxDataItem> FunctionsComboBox;
    @FXML
    private ComboBox<DataHandler.BoxDataItem> TerminalsComboBox;
    @FXML
    private Button StartButton;
    @FXML
    private Button PauseButton;
    @FXML
    private Button StopButton;
    @FXML
    private TextArea ConsoleOutput;
    @FXML
    private MenuItem tournamentMenuItem;
    @FXML
    private MenuItem RouleteMenuItem;
    @FXML
    private ToggleButton ElitistToogleButton1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        m = new Messenger(ConsoleOutput);
        dh = new DataHandler();

        FunctionsComboBox.setItems(DataHandler.BoxDataItem.generateFunctionBoxItems());
        TerminalsComboBox.setItems(DataHandler.BoxDataItem.generateTerminalsBoxItems());

        setListenersOnComboBox(FunctionsComboBox, dh.getLoadedFunctions());
        setListenersOnComboBox(TerminalsComboBox, dh.getLoadedTerminals());
        
    }

    @FXML
    private void CloseFromMenuItem(ActionEvent event) {
        //CLOSE APPLICATION, BUT NOT LIKE THIS!! TODO:
        System.exit(0);
    }

    @FXML
    private void StartCalculation(ActionEvent event) {

        if (!dh.isLoaded()) {
            JOptionPane.showMessageDialog(null, "Load any data, please.");
            event.consume();
            return;
        }
        if (dh.getLoadedFunctions().isEmpty()){
            JOptionPane.showMessageDialog(null, "Select any function, please.");
            event.consume();
            return;
        }
        
        if (dh.getLoadedTerminals().isEmpty()){
            JOptionPane.showMessageDialog(null, "Running without any terminals.");
        }

        int numberOfGenerations
                = Integer.valueOf(NumberOfGenerationsTextField.getText().isEmpty()
                        ? NumberOfGenerationsTextField.getPromptText() : NumberOfGenerationsTextField.getText());
        int sizeOfInitPopulation
                = Integer.valueOf(PopulationSIzeTextField.getText().isEmpty()
                        ? PopulationSIzeTextField.getPromptText() : PopulationSIzeTextField.getText());
        int treeMaxInitDepth
                = Integer.valueOf(TreeMaxInicializationDepthTextField.getText().isEmpty()
                        ? TreeMaxInicializationDepthTextField.getPromptText() : TreeMaxInicializationDepthTextField.getText());
        int treeMaxDepthAfterOperation
                = Integer.valueOf(TreeMaxDepthAfterOperationTextField.getText().isEmpty()
                        ? TreeMaxDepthAfterOperationTextField.getPromptText() : TreeMaxDepthAfterOperationTextField.getText());
        double crossover
                = Double.valueOf(CrossoverProbabilityTextField.getText().isEmpty()
                        ? CrossoverProbabilityTextField.getPromptText() : CrossoverProbabilityTextField.getText());
        double reproduction
                = Double.valueOf(ReproductionProbabilityTextField.getText().isEmpty()
                        ? ReproductionProbabilityTextField.getPromptText() : ReproductionProbabilityTextField.getText());
        double mutation
                = Double.valueOf(MutationProbabilityTextField.getText().isEmpty()
                        ? MutationProbabilityTextField.getPromptText() : MutationProbabilityTextField.getText());
        double crossoverInFunctionNode = 0.0f;
        boolean elitism = ElitistToogleButton.isSelected();
        boolean decimation = false;
        boolean editation = true;
        int numberOfSteps = 1;
        
        GPController demo = new GPController(
                m,
                dh,
                PauseButton,
                StopButton,
                numberOfGenerations,
                sizeOfInitPopulation,
                treeMaxInitDepth,
                treeMaxDepthAfterOperation,
                crossover,
                reproduction,
                mutation,
                crossoverInFunctionNode,
                elitism,
                decimation,
                editation,
                numberOfSteps,
                selectionMethod);
        demo.start();

    }

    @FXML
    private void PauseCalculation(ActionEvent event) {
        //TODO: THREAD PAUSE
        //m.AddMesseage(s);
    }

    @FXML
    private void StopCalculation(ActionEvent event) {
        m.ClearMessenger();
        //TODO: STOP THREAD
    }

    @FXML
    private void LoadFile(ActionEvent event) throws URISyntaxException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()));
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String extension = FilenameUtils.getExtension(selectedFile.getName());
            IReader ir;
            switch (extension) {
                case "xlsx":
                    ir = new XLSXReader(m, TableView);
                    break;
                case "csv":
                    ir = new CSVReader(m, TableView);
                    break;
                default:
                    return;
            }
            ir.ReadFile(selectedFile);
            dh.parseData(ir.GetData());
        }
    }
    
    
    private void updateOutput(){
        m.ClearMessenger();
        m.AddMesseage("Terminals: " + dh.getLoadedTerminals());
        m.AddMesseage("Functions: " + dh.getLoadedFunctions());
        m.GetAllMesseages();
    }

    private void setListenersOnComboBox(ComboBox<DataHandler.BoxDataItem> FunctionsComboBox, List<Gen> loadedFunctions) {
        FunctionsComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<DataHandler.BoxDataItem>() {
            @Override
            public void changed(ObservableValue<? extends DataHandler.BoxDataItem> observable, DataHandler.BoxDataItem oldValue, DataHandler.BoxDataItem newValue) {
                DataHandler.BoxDataItem bdi = FunctionsComboBox.getSelectionModel().getSelectedItem();
                if (bdi.isSelected()){
                    loadedFunctions.removeAll(bdi.getGens());
                    bdi.setSelected(false);
                }else{
                    loadedFunctions.addAll(bdi.getGens());
                    bdi.setSelected(true);
                }
                updateOutput();
            }
        });
    }
    
    @FXML
    private void ShowGraph(ActionEvent event) {
    }

    @FXML
    private void ShowAbout(ActionEvent event) {
    }

    @FXML
    private void TournamentSelected(ActionEvent event) {
        m.AddMesseage("Tournament selection selected!");
        selectionMethod = 0;
        m.GetMesseage();
    }

    @FXML
    private void RouleteSelected(ActionEvent event) {
        m.AddMesseage("Roulete selection selected!");
        selectionMethod = 1;
        m.GetMesseage();
    }

}
