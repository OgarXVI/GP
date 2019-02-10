package cz.ogarxvi.controller;

import cz.ogarxvi.model.CSVReader;
import cz.ogarxvi.model.ConfigurationThread;
import cz.ogarxvi.model.DataHandler;
import cz.ogarxvi.model.FileHandler;
import cz.ogarxvi.model.GPThread;
import cz.ogarxvi.model.Graph;
import cz.ogarxvi.model.Graph.Layout;
import cz.ogarxvi.model.Graph.TreeLayout;
import cz.ogarxvi.model.IReader;
import cz.ogarxvi.model.Localizator;
import cz.ogarxvi.model.Messenger;
import cz.ogarxvi.model.XLSXReader;
import cz.ogarxvi.view.OutputGraph;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.commons.io.FilenameUtils;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.ToggleSwitch;

/**
 * Ovladací třída pro GUI hlavního okna. Užívá knihovny třetí strany
 * (ConstrolsFX) pro větší funkcionalitu GUI
 *
 * @author OgarXVI
 */
public class FXMLController implements Initializable {

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
    //private ListChangeListener<DataHandler.BoxDataItem> functionCheckComboBoxListener;
    private ListChangeListener<DataHandler.BoxDataItem> terminalCheckComboBoxListener;
    @FXML
    private MenuItem RouleteMenuItem;
    @FXML
    private MenuItem tournament3MenuItem;
    @FXML
    private MenuItem tournament2MenuItem;
    @FXML
    private MenuItem tournament5MenuItem;
    @FXML
    private Button FunctionsButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Init
        Messenger.getInstance().setArea(this.ConsoleOutput);
        //Vytvoření předmětů v CheckComboBoxexh
        TerminalsComboBox.getItems().addAll(DataHandler.BoxDataItem.generateTerminalsBoxItems());
        //Incializace posluchačů
        
        //TODO: Zrušit a půjde zde podobně jako funkce okno
        
        terminalCheckComboBoxListener = new ListChangeListener<DataHandler.BoxDataItem>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends DataHandler.BoxDataItem> c) {
                ObservableList<DataHandler.BoxDataItem> olChecked = TerminalsComboBox.getCheckModel().getCheckedItems();
                DataHandler.getInstance().getLoadedTerminals().clear();
                for (DataHandler.BoxDataItem boxDataItem : olChecked) {
                    DataHandler.getInstance().getLoadedTerminals().addAll(boxDataItem.getGens());
                }
                if (TerminalsComboBox.equals(TerminalsComboBox)) {
                    DataHandler.getInstance().loadParamsAsTerminals();
                }
                parOfUpdate();
            }
        };
        //přižazení posluchačů
        TerminalsComboBox.getCheckModel().getCheckedItems().addListener(terminalCheckComboBoxListener);

    }

    /**
     * Zavření aplikace
     *
     * @param event Událost, která vedla k ukončení
     */
    @FXML
    private void CloseFromMenuItem(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void showFunctionWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/fxml/FunctionWindow.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            Stage stage = new Stage();
            stage.setTitle("Functions");
            stage.getIcons().add(new Image("/images/icon.jpg"));
            stage.setScene(scene);

            //FunctionWindowController fwc = fxmlLoader.getController();
            /* fwc.setMyStage(stage);
            fwc.setDataHandler(dh);
            fwc.setFXMLController(this);*/
            stage.show();
        } catch (UnsupportedOperationException | IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    /**
     * Kliknutí START tlačítka
     *
     * @param event
     */
    @FXML
    private void StartCalculation(ActionEvent event) {
        // ošetření nevyplněných parametrů
        if (!DataHandler.getInstance().isLoaded()) {
            JOptionPane.showMessageDialog(null, Localizator.getString("warning.loadData"));
            event.consume();
            return;
        }
        if (!DataHandler.getInstance().isAnyFunctionsLoaded()) {
            JOptionPane.showMessageDialog(null, Localizator.getString("warning.noSelectedFunction"));
            event.consume();
            return;
        }

        if (DataHandler.getInstance().getLoadedTerminals().isEmpty()) {
            JOptionPane.showMessageDialog(null, Localizator.getString("warning.withoutTerminals"));
        }
        // založení bezpečných parametrů
        int numberOfGenerations = Integer.valueOf(NumberOfGenerationsTextField.getPromptText());
        int sizeOfInitPopulation = Integer.valueOf(PopulationSIzeTextField.getPromptText());
        int treeMaxInitDepth = Integer.valueOf(TreeMaxInicializationDepthTextField.getPromptText());
        int treeMaxDepthAfterOperation = Integer.valueOf(TreeMaxDepthAfterOperationTextField.getPromptText());
        double crossover = Double.valueOf(CrossoverProbabilityTextField.getPromptText());
        double reproduction = Double.valueOf(ReproductionProbabilityTextField.getPromptText());
        double mutation = Double.valueOf(MutationProbabilityTextField.getPromptText());
        boolean elitism = ElitistToogleButton.isSelected();
        boolean decimation = DecimationButton.isSelected();
        // zkontrolování uživatelských vstupů
        try {
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
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(null, Localizator.getString("warning.wrongFormat"));
        }
        //puštění GP výpočtu
        GPThread gpC = new GPThread(
                numberOfGenerations,
                sizeOfInitPopulation,
                treeMaxInitDepth,
                treeMaxDepthAfterOperation,
                crossover,
                reproduction,
                mutation,
                elitism,
                decimation);
        gpC.start();

    }

    /**
     * Ukončení výpočtu
     *
     * @param event
     */
    @FXML
    private void StopCalculation(ActionEvent event) {
        if (!DataHandler.getInstance().isGpStop()) {
            DataHandler.getInstance().setGpStop(true);
            Messenger.getInstance().AddMesseage(Localizator.getString("output.stoped"));
            Messenger.getInstance().GetMesseage();
        }
    }

    /**
     * Načte soubor a zpracuje z něho tabulku, kterou zobrazí
     *
     * @param event
     */
    @FXML
    private void OpenOutput(ActionEvent event) throws URISyntaxException, XMLStreamException {
        //Výběr souboru
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()));
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            //Získat ze xml data
            OutputGraph outputGraph = new OutputGraph(selectedFile);
            outputGraph.createCharts();
            outputGraph.showGraphs(true);
        }
    }

    /**
     * Načtení souboru
     *
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
            //DataHandler.getInstance().setLoadedDataFile(fileChooser.getSelectedFile());
            FileHandler.getInstance().file = fileChooser.getSelectedFile();
            // užití knihovny třetí strany - pro zjištění typu souboru
            String extension = FilenameUtils.getExtension(FileHandler.getInstance().file.getName());
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
            ir.ReadFile(FileHandler.getInstance().file);
            FileHandler.getInstance().readedData = ir.GetData();
            DataHandler.getInstance().parseData(ir.GetData());
            //if (FileHandler.getInstance().errorsInFile) {
            //Zobraz okno
            //vytáhni z něho potřebné informace
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/fxml/ErrorWindow.fxml"));
                Scene scene = new Scene(fxmlLoader.load());

                Stage stage = new Stage();
                stage.setTitle(FileHandler.getInstance().errorsInFile ? "Found Errors in File:" : "No Errors Found:");
                stage.getIcons().add(new Image("/images/icon.jpg"));
                stage.setScene(scene);

                ErrorWindowController controller = fxmlLoader.getController();
                controller.fillData(FileHandler.getInstance().readedData, FileHandler.getInstance().errorRowPositions);

                stage.show();

            } catch (IOException ex) {
                Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }
            //}
            DataHandler.getInstance().setRows(TableView.getItems().size());
            Clear(null);

        }
    }

    /**
     * Zobrazení grafu nejlepšího jedince
     *
     * @param event
     */
    @FXML
    private void ShowGraph(ActionEvent event
    ) {

        if (DataHandler.getInstance().getBestChromosome() == null) {
            JOptionPane.showMessageDialog(null, Localizator.getString("warning.missingChromosome"));
            return;
        }

        BorderPane root;
        try {
            root = new BorderPane();

            Graph graph = new Graph();

            root.setCenter(graph.getScrollPane());

            graph.addGraphComponents(DataHandler.getInstance().getBestChromosome().getRoot());

            Scene scene = new Scene(root, 800, 800);
            Stage primaryStage = new Stage();
            primaryStage.setTitle(Localizator.getString("graph.title"));
            primaryStage.setScene(scene);
            primaryStage.show();

            TreeLayout rL = graph.new TreeLayout(graph);
            Layout layout = rL;
            layout.execute();
            //throw new UnsupportedOperationException();
        } catch (UnsupportedOperationException e) {
            JOptionPane.showMessageDialog(null, "Not implemented yet");
        }
    }

    /**
     * Zobraz o programu
     *
     * @param event
     */
    @FXML
    private void ShowAbout(ActionEvent event
    ) {
        JOptionPane.showMessageDialog(null, Localizator.getString("app.autor") + Localizator.getString("app.version"));
    }

    /**
     * Nastavení výběru turnajové selekce
     *
     * @param event
     */
    @FXML
    private void Tournament5Selected(ActionEvent event
    ) {
        Messenger.getInstance().AddMesseage(Localizator.getString("parameter.selection.tournament5") + Localizator.getString("parameter.selection.selected"));
        SelectionMenu.setText(Localizator.getString("parameter.selection.method")
                + Localizator.getString("parameter.selection.tournament5"));
        DataHandler.getInstance().setSelectionMethod(2);
        Messenger.getInstance().GetMesseage();
    }

    /**
     * Nastavení výběru turnajové selekce
     *
     * @param event
     */
    @FXML
    private void Tournament3Selected(ActionEvent event
    ) {
        Messenger.getInstance().AddMesseage(Localizator.getString("parameter.selection.tournament3") + Localizator.getString("parameter.selection.selected"));
        SelectionMenu.setText(Localizator.getString("parameter.selection.method")
                + Localizator.getString("parameter.selection.tournament3"));
        DataHandler.getInstance().setSelectionMethod(0);
        Messenger.getInstance().GetMesseage();
    }

    /**
     * Nastavení výběru turnajové selekce
     *
     * @param event
     */
    @FXML
    private void Tournament2Selected(ActionEvent event
    ) {
        Messenger.getInstance().AddMesseage(Localizator.getString("parameter.selection.tournament2") + Localizator.getString("parameter.selection.selected"));
        SelectionMenu.setText(Localizator.getString("parameter.selection.method")
                + Localizator.getString("parameter.selection.tournament2"));
        DataHandler.getInstance().setSelectionMethod(1);
        Messenger.getInstance().GetMesseage();
    }

    /**
     * Nastavení výběru ruletové selekce
     *
     * @param event
     */
    @FXML
    private void RouleteSelected(ActionEvent event
    ) {
        Messenger.getInstance().AddMesseage(Localizator.getString("parameter.selection.roulette") + Localizator.getString("parameter.selection.selected"));
        SelectionMenu.setText(Localizator.getString("parameter.selection.method")
                + Localizator.getString("parameter.selection.roulette"));
        DataHandler.getInstance().setSelectionMethod(3);
        Messenger.getInstance().GetMesseage();
    }

    /**
     * Vyčistí záznamník, správně ošetří odstranění posluchačů, načtení dat a
     * opětovné nastavení posluchačů
     *
     * @param event
     */
    @FXML
    private void Clear(ActionEvent event
    ) {
        Messenger.getInstance().ClearMessenger();
        DataHandler.getInstance().getLoadedTerminals().clear();
        TerminalsComboBox.getCheckModel().getCheckedItems().removeListener(terminalCheckComboBoxListener);
        DataHandler.getInstance().loadParamsAsTerminals();
        parOfUpdate();
        TerminalsComboBox.getCheckModel().clearChecks();
        TerminalsComboBox.getCheckModel().getCheckedItems().addListener(terminalCheckComboBoxListener);
    }

    private void parOfUpdate() {
        Messenger.getInstance().ClearMessenger();
        if (FileHandler.getInstance().file != null) {
            Messenger.getInstance().AddMesseage(Localizator.getString("output.expectedFunctions") + FilenameUtils.getBaseName(FileHandler.getInstance().file.getName()));
            Messenger.getInstance().AddMesseage(Localizator.getString("output.rows") + DataHandler.getInstance().getTableRows());
        }
        Messenger.getInstance().AddMesseage(Localizator.getString("output.terminals") + DataHandler.getInstance().getLoadedTerminals());
        Messenger.getInstance().AddMesseage(Localizator.getString("output.functions") + DataHandler.getInstance().getAllFunctionsAsString());
        Messenger.getInstance().GetAllMesseages();
    }

    @FXML
    private void RunCofiguration(ActionEvent event) throws URISyntaxException, TransformerConfigurationException, TransformerException {
        ConfigurationThread configurationThread = new ConfigurationThread(FileHandler.getInstance().file.getName());
        configurationThread.LoadData();
        configurationThread.start();
    }

}