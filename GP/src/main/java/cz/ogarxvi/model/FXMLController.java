package cz.ogarxvi.model;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.apache.commons.io.FilenameUtils;
import org.controlsfx.control.CheckComboBox;

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
    private CheckComboBox<DataHandler.BoxDataItem> FunctionsComboBox;
    @FXML
    private CheckComboBox<DataHandler.BoxDataItem> TerminalsComboBox;
    @FXML
    private Button StartButton;
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
    @FXML
    private Button ClearButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        m = new Messenger(ConsoleOutput);
        dh = new DataHandler();

        FunctionsComboBox.getItems().addAll(DataHandler.BoxDataItem.generateFunctionBoxItems());
        TerminalsComboBox.getItems().addAll(DataHandler.BoxDataItem.generateTerminalsBoxItems());

        // FunctionsComboBox.setItems();
        // TerminalsComboBox.setItems(DataHandler.BoxDataItem.generateTerminalsBoxItems());
        setListenersOnComboBox(FunctionsComboBox, dh.getLoadedFunctions());
        setListenersOnComboBox(TerminalsComboBox, dh.getLoadedTerminals());

        updateOutput();
    }

    @FXML
    private void CloseFromMenuItem(ActionEvent event) {
        //CLOSE APPLICATION, BUT NOT LIKE THIS!! TODO:
        Platform.exit();

    }

    @FXML
    private void StartCalculation(ActionEvent event) {

        if (!dh.isLoaded()) {
            JOptionPane.showMessageDialog(null, "Load any data, please.");
            event.consume();
            return;
        }
        if (dh.getLoadedFunctions().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Select any function, please.");
            event.consume();
            return;
        }

        if (dh.getLoadedTerminals().isEmpty()) {
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
        boolean decimation = true;
        boolean editation = true;
        int numberOfSteps = 1;

        GPController demo = new GPController(
                m,
                dh,
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
    private void StopCalculation(ActionEvent event) {
        dh.setGpStop(true);
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
                    
        Clear(event);
        
            updateOutput();
        }
    }

    private void updateOutput() {
        m.ClearMessenger();
        m.AddMesseage("Terminals: " + dh.getLoadedTerminals());
        m.AddMesseage("Functions: " + dh.getLoadedFunctions());
        m.GetAllMesseages();
    }

    private void setListenersOnComboBox(CheckComboBox<DataHandler.BoxDataItem> checkComboBox, List loadedFunctions) {
        checkComboBox.getCheckModel().getCheckedItems().addListener(new ListChangeListener<DataHandler.BoxDataItem>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends DataHandler.BoxDataItem> c) {
                ObservableList<DataHandler.BoxDataItem> olChecked = checkComboBox.getCheckModel().getCheckedItems();
                loadedFunctions.clear();
                for (DataHandler.BoxDataItem boxDataItem : olChecked) {
                    loadedFunctions.addAll(boxDataItem.getGens());
                }
                if (checkComboBox.equals(TerminalsComboBox)){
                    dh.loadParamsAsTerminals();
                }
                updateOutput();
            }
        });
    }

    @FXML
    private void ShowGraph(ActionEvent event) {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getClassLoader().getResource("/fxml/Graph.fxml"));
            Stage stage = new Stage();
            stage.setTitle("TreeGraph");
            stage.setScene(new Scene(root, 450, 450));
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
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

    @FXML
    private void Clear(ActionEvent event) {
        m.ClearMessenger();
        dh.getLoadedTerminals().clear();
        dh.getLoadedFunctions().clear();
        dh.loadParamsAsTerminals();
    }

}
