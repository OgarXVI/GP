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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.commons.io.FilenameUtils;
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
    /**
     * Button
     */
    //private Button FunctionsButton;
    /**
     * Button
     */
    @FXML
    private Button TerminalsButton;
    /**
     * Menu File
     */
    @FXML
    private Menu menuFile;
    /**
     * Menu Item Load Data
     */
    @FXML
    private MenuItem menuItemLoadData;
    /**
     * Menu Item Run Cofiguration
     */
    @FXML
    private MenuItem menuItemRunConfi;
    /**
     * Menu Item Open Output
     */
    @FXML
    private MenuItem menuItemOpenOutput;
    /**
     * Menu Item Exit
     */
    @FXML
    private MenuItem menuItemExit;
    /**
     * Menu Show
     */
    @FXML
    private Menu menuShow;
    /**
     * Menu Item Show Graph
     */
    @FXML
    private MenuItem menuItemShowGraph;
    /**
     * Menu About
     */
    @FXML
    private Menu menuAbout;
    /**
     * Menu Item About
     */
    @FXML
    private MenuItem menuItemAbout;
    /**
     * Label Population Size
     */
    @FXML
    private Label labelPopulationSize;
    /**
     * Label Number of Generation
     */
    @FXML
    private Label labelNumberofGeneration;
    /**
     * Label Reproduction of Probability
     */
    @FXML
    private Label labelReproductionProbability;
    /**
     * Label Muation of Probability
     */
    @FXML
    private Label labelMutationProbabilty;
    /**
     * Label Crossing of Probability
     */
    @FXML
    private Label labelCrossingProbabilty;
    /**
     * Label Tree Max Operation Depth
     */
    @FXML
    private Label labelTreeMaxOperationDepth;
    /**
     * Label Tree Max Init Depth
     */
    @FXML
    private Label labelTreeMaxInitDepth;
    /**
     * Menu Item Tournament 3
     */
    @FXML
    private MenuItem menuItemTournament3;
    /**
     * Menu Item Tournamrnt 2
     */
    @FXML
    private MenuItem menuItemTournament2;
    /**
     * Menu Item tournament 5
     */
    @FXML
    private MenuItem menuItemTournament5;
    /**
     * Menu Item Roulete
     */
    @FXML
    private MenuItem menuItemRoulete;
    /**
     * Label Functions
     */
    //private Label labelFunctions;
    /**
     * Bale Terminals
     */
    @FXML
    private Label labelTerminals;
    /**
     * Menu item
     */
    @FXML
    private MenuItem menuitemSetting;
    @FXML
    private Label labelToleration;
    @FXML
    private Slider sliderTolerationFitness;

    /**
     * Nápověda pro TreeMaxDepthAfterOperation
     */
    //@FXML
    //private Tooltip tooltipTreeMaxDepthAfterOperationTextField;
    public void initialize(URL url, ResourceBundle rb) {
        Messenger.getInstance().setArea(this.ConsoleOutput);
        ElitistToogleButton.setText(Localizator.getString("parameter.elitism"));
        DecimationButton.setText(Localizator.getString("parameter.decimation"));
        labelTerminals.setText(Localizator.getString("parameter.terminals"));
        //labelFunctions.setText(Localizator.getString("parameter.functions"));
        menuItemRoulete.setText(Localizator.getString("parameter.selection.roulette"));
        menuItemTournament5.setText(Localizator.getString("parameter.selection.tournament5"));
        menuItemTournament3.setText(Localizator.getString("parameter.selection.tournament3"));
        menuItemTournament2.setText(Localizator.getString("parameter.selection.tournament2"));
        labelTreeMaxInitDepth.setText(Localizator.getString("parameter.treeMaxInitDepth"));
        labelTreeMaxOperationDepth.setText(Localizator.getString("parameter.treeMaxOperationDepth"));
        labelCrossingProbabilty.setText(Localizator.getString("parameter.crossingProbability"));
        labelMutationProbabilty.setText(Localizator.getString("parameter.mutationProbability"));
        labelReproductionProbability.setText(Localizator.getString("parameter.reproductionProbability"));
        labelNumberofGeneration.setText(Localizator.getString("parameter.numberOfGenerations"));
        labelPopulationSize.setText(Localizator.getString("parameter.populationsize"));
        menuItemAbout.setText(Localizator.getString("menu.about.about"));
        menuAbout.setText(Localizator.getString("menu.about"));
        menuItemShowGraph.setText(Localizator.getString("menu.show.graph"));
        menuShow.setText(Localizator.getString("menu.show"));
        menuItemExit.setText(Localizator.getString("menu.file.exit"));
        menuItemOpenOutput.setText(Localizator.getString("menu.file.openoOutput"));
        menuItemRunConfi.setText(Localizator.getString("menu.file.runConfiguration"));
        menuItemLoadData.setText(Localizator.getString("menu.file.loadData"));
        menuFile.setText(Localizator.getString("menu.file"));
        TerminalsButton.setText(Localizator.getString("parameter.terminals.select"));
        //SelectionMenu.setText(Localizator.getString("parameter.selection.method"));
        //FunctionsButton.setText(Localizator.getString("parameter.functions.select"));
        ClearButton.setText(Localizator.getString("button.clear"));
        StopButton.setText(Localizator.getString("button.stop"));
        StartButton.setText(Localizator.getString("button.start"));
        //tooltipTreeMaxDepthAfterOperationTextField.setText(Localizator.getString(""));
        TableView.setPlaceholder(new Label(Localizator.getString("tableview.empty")));

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

    /**
     * Zobrazení okno funkcí
     */
    private void showFunctionWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/fxml/FunctionWindow.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            Stage stage = new Stage();
            stage.setTitle(Localizator.getString("window.title.functions"));
            stage.getIcons().add(new Image("/images/icon.jpg"));
            stage.setScene(scene);

            stage.show();
        } catch (UnsupportedOperationException | IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    /**
     * Kliknutí START tlačítka
     *
     * @param event event
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
     * @param event event
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
     * @param event event
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
     * @param event event
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
            //resetovaní error logu
            FileHandler.getInstance().reset();
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
                stage.setTitle(FileHandler.getInstance().errorsInFile
                        ? Localizator.getString("warning.error.found")
                        : Localizator.getString("warning.error.notFound"));
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
            //double d = DataHandler.getInstance().getFitnessLimit();
            sliderTolerationFitness.valueProperty().addListener(o -> {
                double round = sliderTolerationFitness.getValue();
                DataHandler.getInstance().resolveFitnessLimit(round);
                if (String.valueOf(round).length() > 10) {
                    labelToleration.setText("Toleration:  " + String.valueOf(DataHandler.getInstance().getFitnessLimit()).substring(0, 10));
                } else {
                    labelToleration.setText("Toleration:  " + String.valueOf(DataHandler.getInstance().getFitnessLimit()));
                }
            });

            sliderTolerationFitness.setShowTickLabels(false);
            sliderTolerationFitness.setShowTickMarks(true);
            clear(null);

        }
    }

    /**
     * Zobrazení grafu nejlepšího jedince
     *
     * @param event event
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
     * @param event event
     */
    @FXML
    private void ShowAbout(ActionEvent event) {
        JOptionPane.showMessageDialog(null, Localizator.getString("app.autor") + Localizator.getString("app.version"));
    }

    /**
     * Slouží pro sdružení kódu volení selekčních mechanismů
     *
     * @param x číslo výběru
     */
    private void TournamentXSelected(String x, int setType) {
        Messenger.getInstance().AddMesseage(Localizator.getString("parameter.selection.selected") + " " + Localizator.getString("parameter.selection.tournament" + x));
        SelectionMenu.setText(Localizator.getString("parameter.selection.method")
                + Localizator.getString("parameter.selection.tournament" + x));
        DataHandler.getInstance().setSelectionMethod(setType);
        Messenger.getInstance().GetMesseage();

    }

    /**
     * Nastavení výběru turnajové selekce
     *
     * @param event event
     */
    @FXML
    private void Tournament5Selected(ActionEvent event) {
        TournamentXSelected("5", 2);
    }

    /**
     * Nastavení výběru turnajové selekce
     *
     * @param event event
     */
    @FXML
    private void Tournament3Selected(ActionEvent event) {
        TournamentXSelected("3", 0);
    }

    /**
     * Nastavení výběru turnajové selekce
     *
     * @param event event
     */
    @FXML
    private void Tournament2Selected(ActionEvent event) {
        TournamentXSelected("2", 1);
    }

    /**
     * Nastavení výběru ruletové selekce
     *
     * @param event event
     */
    @FXML
    private void RouleteSelected(ActionEvent event) {
        Messenger.getInstance().AddMesseage(Localizator.getString("parameter.selection.selected")
                + " " + Localizator.getString("parameter.selection.roulette"));
        SelectionMenu.setText(Localizator.getString("parameter.selection.method")
                + Localizator.getString("parameter.selection.roulette"));
        DataHandler.getInstance().setSelectionMethod(3);
        Messenger.getInstance().GetMesseage();
    }

    /**
     * Vyčistí záznamník, správně ošetří odstranění posluchačů, načtení dat a
     * opětovné nastavení posluchačů
     *
     * @param event event
     */
    @FXML
    private void clear(ActionEvent event) {
        Messenger.getInstance().ClearMessenger();
        DataHandler.getInstance().getLoadedTerminals().clear();
        //clear
//        DataHandler.getInstance().getLoadedFunctionsCategories().forEach((loadedFunctionsCategory) -> {
//            loadedFunctionsCategory.clear();
//        });
        DataHandler.getInstance().loadParamsAsTerminals();
        Messenger.getInstance().Update();
    }

    /**
     * Pustí konfiguraci
     *
     * @param event event
     * @throws URISyntaxException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    @FXML
    private void RunCofiguration(ActionEvent event) throws URISyntaxException, TransformerConfigurationException, TransformerException {
        if (FileHandler.getInstance().file == null) {
            JOptionPane.showMessageDialog(null, Localizator.getString("warning.loadData"));
            return;
        }
        ConfigurationThread configurationThread = new ConfigurationThread(FileHandler.getInstance().file.getName());
        configurationThread.LoadData();
        configurationThread.start();
    }

    /**
     * Zobrazí okno pro terminály
     *
     * @param event event
     */
    @FXML
    private void showTerminalWindow(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/fxml/TerminalWindow.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            Stage stage = new Stage();
            stage.setTitle("Terminals");
            stage.getIcons().add(new Image("/images/icon.jpg"));
            stage.setScene(scene);

            stage.show();
        } catch (UnsupportedOperationException | IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    @FXML
    private void menuitemSettingClick(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/fxml/SettingWindow.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            Stage stage = new Stage();
            stage.setTitle("Setting");
            stage.getIcons().add(new Image("/images/icon.jpg"));
            stage.setScene(scene);

            stage.show();
        } catch (UnsupportedOperationException | IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

}
