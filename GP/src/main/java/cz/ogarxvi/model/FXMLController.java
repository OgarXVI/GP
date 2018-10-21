package cz.ogarxvi.model;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.apache.commons.io.FilenameUtils;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.ToggleSwitch;
/**
 * Ovladací třída pro GUI hlavního okna.
 * Užívá knihovny třetí strany (ConstrolsFX) pro větší funkcionalitu GUI
 * @author OgarXVI
 */
public class FXMLController implements Initializable {
    /**
     * Data pro GP
     */
    private DataHandler dh;
    /**
     * Záznamník zpráv
     */
    private Messenger m;
    /**
     * Vybraná selekční metoda
     */
    private int selectionMethod = 0;
    /**
     * Tabulka pro ovládání a zobrazení
     */
    @FXML
    private TableView<String[]> TableView;
    /**
     * Textové pole pro zadání velikosti populace
     */
    @FXML
    private TextField PopulationSIzeTextField;
    /**
     * Textové pole pro zadání počtu generací
     */
    @FXML
    private TextField NumberOfGenerationsTextField;
    /**
     * Textové pole pro zadání šance na reprodukci
     */
    @FXML
    private TextField ReproductionProbabilityTextField;
    /**
     * Textové pole pro zadání šance na mutaci
     */
    @FXML
    private TextField MutationProbabilityTextField;
    /**
     * Textové pole pro zadání šance na křížení
     */
    @FXML
    private TextField CrossoverProbabilityTextField;
    /**
     * Textové pole pro zadání maximální hloubky stromu po operaci
     */
    @FXML
    private TextField TreeMaxDepthAfterOperationTextField;
    /**
     * Textové pole pro zadání maximální hloubky stromu při inicializaci
     */
    @FXML
    private TextField TreeMaxInicializationDepthTextField;
    /**
     * Toogle pro zapnutí/vypnutí elitismu
     */
    @FXML
    private ToggleSwitch ElitistToogleButton;
    /**
     * Selekční menu pro výběr selekční metody
     */
    @FXML
    private MenuButton SelectionMenu;
    /**
     * CheckBomboBox s funkcemi
     */
    @FXML
    private CheckComboBox<DataHandler.BoxDataItem> FunctionsComboBox;
    /**
     * CheckComboBox s terminály
     */
    @FXML
    private CheckComboBox<DataHandler.BoxDataItem> TerminalsComboBox;
    /**
     * Tlačítko pro spuštění výpočtu GA
     */
    @FXML
    private Button StartButton;
    /**
     * Tlačítko pro zastavení výpočtu GA
     */
    @FXML
    private Button StopButton;
    /**
     * TextArea pro textový výstup
     */
    @FXML
    private TextArea ConsoleOutput;
    /**
     * Toggle pro zapnutí/vypnutí decimace
     */
    @FXML
    private ToggleSwitch DecimationButton;
    /**
     * Tlačítko pro vyčištění textové oblasti
     */
    @FXML
    private Button ClearButton;

    //Listeners
    private ListChangeListener<DataHandler.BoxDataItem> functionCheckComboBoxListener;
    private ListChangeListener<DataHandler.BoxDataItem> terminalCheckComboBoxListener;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Init
        m = new Messenger(ConsoleOutput);
        dh = new DataHandler();
        //Vytvoření předmětů v CheckComboBoxexh
        FunctionsComboBox.getItems().addAll(DataHandler.BoxDataItem.generateFunctionBoxItems());
        TerminalsComboBox.getItems().addAll(DataHandler.BoxDataItem.generateTerminalsBoxItems());
        //Inicializace posluchačů
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
        //Incializace posluchačů
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
        
        //přižazení posluchačů
        FunctionsComboBox.getCheckModel().getCheckedItems().addListener(functionCheckComboBoxListener);
        TerminalsComboBox.getCheckModel().getCheckedItems().addListener(terminalCheckComboBoxListener);

    }

    /**
     * Zavření aplikace
     * @param event Událost, která vedla k ukončení
     */
    @FXML
    private void CloseFromMenuItem(ActionEvent event) {
        Platform.exit();
    }
    /**
     * Kliknutí START tlačítka
     * @param event 
     */
    @FXML
    private void StartCalculation(ActionEvent event) {
        // ošetření nevyplněných parametrů
        if (!dh.isLoaded()) {
            JOptionPane.showMessageDialog(null, "Load data.");
            event.consume();
            return;
        }
        if (dh.getLoadedFunctions().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Select function.");
            event.consume();
            return;
        }

        if (dh.getLoadedTerminals().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Running without terminals.");
        }
        // založení bezpečných parametrů
        int numberOfGenerations = Integer.valueOf(NumberOfGenerationsTextField.getPromptText());
        int sizeOfInitPopulation = Integer.valueOf(PopulationSIzeTextField.getPromptText());
        int treeMaxInitDepth = Integer.valueOf(TreeMaxInicializationDepthTextField.getPromptText());
        int treeMaxDepthAfterOperation  = Integer.valueOf(TreeMaxDepthAfterOperationTextField.getPromptText());
        double crossover = Double.valueOf(CrossoverProbabilityTextField.getPromptText());
        double reproduction = Double.valueOf(ReproductionProbabilityTextField.getPromptText());
        double mutation = Double.valueOf(MutationProbabilityTextField.getPromptText());
        boolean elitism = ElitistToogleButton.isSelected();
        boolean decimation = DecimationButton.isSelected();
        boolean editation = false;
        // zkontrolování uživatelských vstupů
        try{
        numberOfGenerations
                = Integer.valueOf(NumberOfGenerationsTextField.getText().isEmpty()
                        ? NumberOfGenerationsTextField.getPromptText() : NumberOfGenerationsTextField.getText());
        sizeOfInitPopulation
                = Integer.valueOf(PopulationSIzeTextField.getText().isEmpty()
                        ? PopulationSIzeTextField.getPromptText() : PopulationSIzeTextField.getText());
        treeMaxInitDepth
                = Integer.valueOf(TreeMaxInicializationDepthTextField.getText().isEmpty()
                        ? TreeMaxInicializationDepthTextField.getPromptText() : TreeMaxInicializationDepthTextField.getText());
        treeMaxDepthAfterOperation
                = Integer.valueOf(TreeMaxDepthAfterOperationTextField.getText().isEmpty()
                        ? TreeMaxDepthAfterOperationTextField.getPromptText() : TreeMaxDepthAfterOperationTextField.getText());
        crossover
                = Double.valueOf(CrossoverProbabilityTextField.getText().isEmpty()
                        ? CrossoverProbabilityTextField.getPromptText() : CrossoverProbabilityTextField.getText());
        reproduction
                = Double.valueOf(ReproductionProbabilityTextField.getText().isEmpty()
                        ? ReproductionProbabilityTextField.getPromptText() : ReproductionProbabilityTextField.getText());
        mutation
                = Double.valueOf(MutationProbabilityTextField.getText().isEmpty()
                        ? MutationProbabilityTextField.getPromptText() : MutationProbabilityTextField.getText());
        elitism = ElitistToogleButton.isSelected();
        decimation = DecimationButton.isSelected();
        }catch(NumberFormatException nfe){
            JOptionPane.showMessageDialog(null, "Wrong format, setting default values...");
        }
        //puštění GP výpočtu
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
                elitism,
                decimation,
                editation,
                selectionMethod);
        demo.start();

    }
    /**
     * Ukončení výpočtu
     * @param event 
     */
    @FXML
    private void StopCalculation(ActionEvent event) {
        dh.setGpStop(true);
    }
    /**
     * Načtení souboru
     * @param event
     * @throws URISyntaxException 
     */
    @FXML
    private void LoadFile(ActionEvent event) throws URISyntaxException {
        //Výběr souboru
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()));
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            // užití knihovny třetí strany - pro zjištění typu souboru
            String extension = FilenameUtils.getExtension(selectedFile.getName());
            IReader ir;
            switch (extension) {
                case "xlsx":
                    ir = new XLSXReader(TableView);
                    break;
                case "csv":
                    ir = new CSVReader(TableView);
                    break;
                default:
                    return;
            }
            //načtení a zpracování dat
            ir.ReadFile(selectedFile);
            dh.parseData(ir.GetData());
            // vyčištění prostoru
            Clear(null);
        }
    }
    /**
     * Aktulizování textového výstupu
     */
    private void updateOutput() {
        m.ClearMessenger();
        m.AddMesseage("Terminals: " + dh.getLoadedTerminals());
        m.AddMesseage("Functions: " + dh.getLoadedFunctions());
        m.GetAllMesseages();
    }
    /**
     * Zobrazení grafu nejlepšího jedince
     * @param event 
     */
    @FXML
    private void ShowGraph(ActionEvent event) {

        if (dh.getBestChromosome() == null) {
            JOptionPane.showMessageDialog(null, "Missing chromosome for rendering");
            return;
        }

        Parent root;
        try {
            
            /*
            root = FXMLLoader.load(getClass().getResource("/fxml/Graph.fxml"));
            Stage stage = new Stage();
            stage.setTitle("TreeGraph");
            Scene scene = new Scene(root, 450, 450);
            stage.setScene(scene);
            stage.show();
            */
            throw new UnsupportedOperationException();
        } catch (UnsupportedOperationException e) {
            JOptionPane.showMessageDialog(null, "Not implemented yet");
        }
    }
    /**
     * Zobraz o programu
     * @param event 
     */
    @FXML
    private void ShowAbout(ActionEvent event) {
        JOptionPane.showMessageDialog(null, "Autor: Jaroslav Dibitanzl ");
    }
    /**
     * Nastavení výběru turnajové selekce
     * @param event 
     */
    @FXML
    private void TournamentSelected(ActionEvent event) {
        m.AddMesseage("Tournament selection selected!");
        SelectionMenu.setText("Selection: Tournament");
        selectionMethod = 0;
        m.GetMesseage();
    }
    /**
     * Nastavení výběru ruletové selekce
     * @param event 
     */
    @FXML
    private void RouleteSelected(ActionEvent event) {
        m.AddMesseage("Roulete selection selected!");
        SelectionMenu.setText("Selection: Roulette");
        selectionMethod = 1;
        m.GetMesseage();
    }
    /**
     * Vyčistí záznamník, správně ošetří odstranění posluchačů, načtení dat a opětovné nastavení posluchačů
     * @param event 
     */
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

}
