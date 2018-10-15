package cz.ogarxvi.model;

import cz.ogarxvi.model.DataHandler.BoxDataItem;
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
import org.abego.treelayout.TreeLayout;
import org.abego.treelayout.netbeans.demo.AbegoTreeLayoutForNetbeansDemo;
import org.abego.treelayout.netbeans.demo.AbegoTreeLayoutForNetbeansDemo.TreeScene;
import org.abego.treelayout.util.DefaultTreeForTreeLayout;
import org.apache.commons.io.FilenameUtils;
import org.controlsfx.control.CheckComboBox;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.graph.layout.GraphLayout;
import org.netbeans.api.visual.graph.layout.GraphLayoutFactory;
import org.netbeans.api.visual.graph.layout.GraphLayoutSupport;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.layout.SceneLayout;

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

    //Listeners
    private ListChangeListener<DataHandler.BoxDataItem> functionCheckComboBoxListener;
    private ListChangeListener<DataHandler.BoxDataItem> terminalCheckComboBoxListener;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        m = new Messenger(ConsoleOutput);
        dh = new DataHandler();

        FunctionsComboBox.getItems().addAll(DataHandler.BoxDataItem.generateFunctionBoxItems());
        TerminalsComboBox.getItems().addAll(DataHandler.BoxDataItem.generateTerminalsBoxItems());

        functionCheckComboBoxListener = new ListChangeListener<DataHandler.BoxDataItem>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends DataHandler.BoxDataItem> c) {
                ObservableList<DataHandler.BoxDataItem> olChecked = FunctionsComboBox.getCheckModel().getCheckedItems();
                dh.getLoadedFunctions().clear();
                for (DataHandler.BoxDataItem boxDataItem : olChecked) {
                    dh.getLoadedFunctions().addAll(boxDataItem.getGens());
                }
                if (FunctionsComboBox.equals(TerminalsComboBox)) {
                    dh.loadParamsAsTerminals();
                }
                m.ClearMessenger();
                updateOutput();
            }
        };

        terminalCheckComboBoxListener = new ListChangeListener<DataHandler.BoxDataItem>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends DataHandler.BoxDataItem> c) {
                ObservableList<DataHandler.BoxDataItem> olChecked = TerminalsComboBox.getCheckModel().getCheckedItems();
                dh.getLoadedTerminals().clear();
                for (DataHandler.BoxDataItem boxDataItem : olChecked) {
                    dh.getLoadedTerminals().addAll(boxDataItem.getGens());
                }
                if (TerminalsComboBox.equals(TerminalsComboBox)) {
                    dh.loadParamsAsTerminals();
                }
                m.ClearMessenger();
                updateOutput();
            }
        };

        FunctionsComboBox.getCheckModel().getCheckedItems().addListener(functionCheckComboBoxListener);
        TerminalsComboBox.getCheckModel().getCheckedItems().addListener(terminalCheckComboBoxListener);

        // updateOutput();
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

            Clear(null);
        }
    }

    private void updateOutput() {
        m.ClearMessenger();
        m.AddMesseage("Terminals: " + dh.getLoadedTerminals());
        m.AddMesseage("Functions: " + dh.getLoadedFunctions());
        m.GetAllMesseages();
    }

    @FXML
    private void ShowGraph(ActionEvent event) {
/*
        if (dh.getBestChromosome() == null) {
            JOptionPane.showMessageDialog(null, "Missing chromosome for rendering");
            return;
        }
*/
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/Graph.fxml"));
            Stage stage = new Stage();
            stage.setTitle("TreeGraph");
            Scene scene = new Scene(root, 450, 450);

            TextInBox rootT = new TextInBox("root", 40, 20);
            TextInBox n1 = new TextInBox("n1", 30, 20);
            TextInBox n1_1 = new TextInBox("n1.1\n(first node)", 80, 36);
            TextInBox n1_2 = new TextInBox("n1.2", 40, 20);
            TextInBox n1_3 = new TextInBox("n1.3\n(last node)", 80, 36);
            TextInBox n2 = new TextInBox("n2", 30, 20);
            TextInBox n2_1 = new TextInBox("n2", 30, 20);
            DefaultTreeForTreeLayout<TextInBox> tree = new DefaultTreeForTreeLayout<TextInBox>(rootT);
            tree.addChild(rootT, n1);
            tree.addChild(n1, n1_1);
            tree.addChild(n1, n1_2);
            tree.addChild(n1, n1_3);
            tree.addChild(rootT, n2);
            tree.addChild(n2, n2_1);
            
            stage.setScene(scene);
            // Init Graf
            //Graph graph;
            //graph.setChromosome(dh.getBestChromosome());
            //graph.draw();

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ShowAbout(ActionEvent event) {
        JOptionPane.showMessageDialog(null, "Autor: Jaroslav Dibitanzl ");
    }

    @FXML
    private void TournamentSelected(ActionEvent event) {
        m.AddMesseage("Tournament selection selected!");
        SelectionMenu.setText("Selection: Tournament");
        selectionMethod = 0;
        m.GetMesseage();
    }

    @FXML
    private void RouleteSelected(ActionEvent event) {
        m.AddMesseage("Roulete selection selected!");
        SelectionMenu.setText("Selection: Roulette");
        selectionMethod = 1;
        m.GetMesseage();
    }

    @FXML
    private void Clear(ActionEvent event) {
        m.ClearMessenger();
        dh.getLoadedTerminals().clear();
        FunctionsComboBox.getCheckModel().getCheckedItems().removeListener(functionCheckComboBoxListener);
        dh.getLoadedFunctions().clear();
        TerminalsComboBox.getCheckModel().getCheckedItems().removeListener(terminalCheckComboBoxListener);
        dh.loadParamsAsTerminals();
        updateOutput();

        //
        FunctionsComboBox.getCheckModel().clearChecks();
        TerminalsComboBox.getCheckModel().clearChecks();

        FunctionsComboBox.getCheckModel().getCheckedItems().addListener(functionCheckComboBoxListener);
        TerminalsComboBox.getCheckModel().getCheckedItems().addListener(terminalCheckComboBoxListener);
    }

    class TextInBox{
        String text;
        int width;
        int height;

        public TextInBox(String text, int width, int height) {
            this.text = text;
            this.width = width;
            this.height = height;
        }
        
        
    }
    
    
}
