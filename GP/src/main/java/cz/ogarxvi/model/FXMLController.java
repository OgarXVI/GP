package cz.ogarxvi.model;

import cz.ogarxvi.genetic.Chromosome;
import cz.ogarxvi.genetic.Gen;
import cz.ogarxvi.genetic.GeneticAlgorithm;
import cz.ogarxvi.model.Graph.Layout;
import cz.ogarxvi.model.Graph.TreeLayout;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.io.FilenameUtils;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.ToggleSwitch;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

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

            FunctionWindowController fwc = fxmlLoader.getController();
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
        GPController gpC = new GPController(
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
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader xsr = null;
            Map<BigDecimal, Integer> results = new HashMap<>();

            try {
                xsr = factory.createXMLStreamReader(new FileReader(selectedFile));
                String element = "";
                BigDecimal fitness = null;
                int quantity = 0;

                while (xsr.hasNext()) {
                    // načítáme element
                    if (xsr.getEventType() == XMLStreamConstants.START_ELEMENT) {
                        element = xsr.getName().getLocalPart();
                        if (element.equals("Result")) {
                            //
                        }
                    } // načítáme hodnotu elementu
                    else if (xsr.getEventType() == XMLStreamConstants.CHARACTERS) {
                        switch (element) {
                            case "Fitness":
                                fitness = BigDecimal.valueOf(Double.valueOf(xsr.getText()));
                                break;
                            case "Quantity":
                                quantity = Integer.valueOf(xsr.getText());
                                break;
                        }
                        element = "";
                    } // načítáme konec elementu
                    else if ((xsr.getEventType() == XMLStreamConstants.END_ELEMENT)) {
                        if ((xsr.getName().getLocalPart().equals("Result"))) {
                            if (results.containsKey(fitness)) {
                                results.put(fitness, results.get(fitness) + quantity);
                            } else {
                                results.put(fitness, quantity);
                            }
                        }
                    }
                    xsr.next();
                }
            } catch (FileNotFoundException | NumberFormatException | XMLStreamException e) {
                Messenger.getInstance().AddMesseage("Error: " + e.getMessage());
                Messenger.getInstance().GetMesseage();
            }

            try {
                HistogramDataset dataset = new HistogramDataset();
                dataset.setType(HistogramType.FREQUENCY);

                List<Double> listDouble = new ArrayList();
                for (Map.Entry<BigDecimal, Integer> entry : results.entrySet()) {
                    BigDecimal key = entry.getKey();
                    Integer value = entry.getValue();
                    for (int i = 0; i < value.intValue(); i++) {
                        listDouble.add(key.doubleValue());
                    }
                }
                double[] pomD = new double[listDouble.size()];
                for (int i = 0; i < pomD.length; i++) {
                    pomD[i] = listDouble.get(i);
                }
                //CALC MIN
                double min = 0f;
                for (int i = 0; i < pomD.length; i++) {
                    if (min > pomD[i]) {
                        min = pomD[i];
                    }
                }

                //CALC MAX
                double max = 0f;
                for (int i = 0; i < pomD.length; i++) {
                    if (max < pomD[i]) {
                        max = pomD[i];
                    }
                }

                dataset.addSeries("Histogram", pomD, 100, min - 0.1f, max + 0.1f);

                // Dataset
                DefaultPieDataset dataset2 = new DefaultPieDataset();
                results.entrySet().forEach((entry) -> {
                    BigDecimal key = entry.getKey();
                    Integer value = entry.getValue();
                    dataset2.setValue(key, value);
                });
                //Chart
                JFreeChart chart2 = ChartFactory.createPieChart("Output Graph", dataset2, true, true, false);
                //Chart
                JFreeChart chart = ChartFactory.createHistogram("Histogram", "Value", "Frequency", dataset, PlotOrientation.VERTICAL, true, true, true);

                //STYLE
                StandardChartTheme theme = (StandardChartTheme) org.jfree.chart.StandardChartTheme.createJFreeTheme();

                theme.setTitlePaint(java.awt.Color.decode("#4572a7"));
                theme.setExtraLargeFont(new Font("Lucida Sans", Font.PLAIN, 16)); //title
                theme.setLargeFont(new Font("Lucida Sans", Font.BOLD, 15)); //axis-title
                theme.setRegularFont(new Font("Lucida Sans", Font.PLAIN, 11));
                theme.setRangeGridlinePaint(java.awt.Color.decode("#C0C0C0"));
                theme.setPlotBackgroundPaint(java.awt.Color.white);
                theme.setChartBackgroundPaint(java.awt.Color.white);
                theme.setGridBandPaint(java.awt.Color.red);
                theme.setAxisOffset(new RectangleInsets(0, 0, 0, 0));
                theme.setBarPainter(new StandardBarPainter());
                theme.setAxisLabelPaint(java.awt.Color.decode("#666666"));
                theme.apply(chart);

                chart.getPlot().setOutlineStroke(new BasicStroke(3f));
                //JPanel
                JFrame af = new JFrame("Output Graph - Histogram");
                af.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/icon.jpg")));
                af.setContentPane(new ChartPanel(chart));
                af.setSize(600, 600);
                RefineryUtilities.centerFrameOnScreen(af);
                af.setVisible(true);
                JFrame af2 = new JFrame("Output Graph - PieChart");
                af2.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/icon.jpg")));
                af2.setContentPane(new ChartPanel(chart2));
                af2.setSize(600, 600);
                RefineryUtilities.centerFrameOnScreen(af2);
                af2.setVisible(true);
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }
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
            DataHandler.getInstance().setLoadedDataFile(fileChooser.getSelectedFile());
            // užití knihovny třetí strany - pro zjištění typu souboru
            String extension = FilenameUtils.getExtension(DataHandler.getInstance().getLoadedDataFile().getName());
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
            ir.ReadFile(DataHandler.getInstance().getLoadedDataFile());
            DataHandler.getInstance().parseData(ir.GetData());
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
    private void ShowGraph(ActionEvent event) {

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
    private void ShowAbout(ActionEvent event) {
        JOptionPane.showMessageDialog(null, Localizator.getString("app.autor") + Localizator.getString("app.version"));
    }

    /**
     * Nastavení výběru turnajové selekce
     *
     * @param event
     */
    @FXML
    private void Tournament5Selected(ActionEvent event) {
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
    private void Tournament3Selected(ActionEvent event) {
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
    private void Tournament2Selected(ActionEvent event) {
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
    private void RouleteSelected(ActionEvent event) {
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
    private void Clear(ActionEvent event) {
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
        if (DataHandler.getInstance().getLoadedDataFile() != null) {
            Messenger.getInstance().AddMesseage(Localizator.getString("output.expectedFunctions") + FilenameUtils.getBaseName(DataHandler.getInstance().getLoadedDataFile().getName()));
            Messenger.getInstance().AddMesseage(Localizator.getString("output.rows") + DataHandler.getInstance().getTableRows());
        }
        Messenger.getInstance().AddMesseage(Localizator.getString("output.terminals") + DataHandler.getInstance().getLoadedTerminals());
        Messenger.getInstance().AddMesseage(Localizator.getString("output.functions") + DataHandler.getInstance().getAllFunctionsAsString());
        Messenger.getInstance().GetAllMesseages();
    }

    @FXML
    private void RunCofiguration(ActionEvent event) throws URISyntaxException, TransformerConfigurationException, TransformerException {
        //LOAD XML CONFIGURATION
        List<Configuration> configurations = new ArrayList<>();
        List<Map<Output, Integer>> outputs = new ArrayList<>();
        List<Map<BigDecimal, Integer>> finalOutputs = new ArrayList<>();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()));
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader xsr = null;
            try {
                xsr = factory.createXMLStreamReader(new FileReader(selectedFile));

                //System.out.println("JSEM ZDE!!");
                int Id = 0;
                int NumberOfStarts = 0;
                int PopulationSize = 0;
                int NumberOfGenerations = 0;
                int MaxDepthTreeInit = 0;
                int MaxDepthTreeAfterCrossover = 0;
                double ReproductionProbability = 0;
                double MutationProbability = 0;
                double CrossingProbability = 0;
                int Selection = 0;
                boolean Elitism = false;
                boolean Decimation = false;
                List<Gen> Functions = new ArrayList<>();
                List<Gen> Terminals = new ArrayList<>();

                int Arita = 0;

                String element = "";

                while (xsr.hasNext()) {
                    // načítáme element
                    if (xsr.getEventType() == XMLStreamConstants.START_ELEMENT) {
                        element = xsr.getName().getLocalPart();
                        if (element.equals("Configuration")) {
                            // ID configurace
                            Id = Integer.parseInt(xsr.getAttributeValue(0));
                        }
                    } // načítáme hodnotu elementu
                    else if (xsr.getEventType() == XMLStreamConstants.CHARACTERS) {
                        switch (element) {
                            case "NumberOfStarts":
                                NumberOfStarts = Integer.valueOf(xsr.getText());
                                break;
                            case "PopulationSize":
                                PopulationSize = Integer.valueOf(xsr.getText());
                                break;
                            case "NumberOfGenerations":
                                NumberOfGenerations = Integer.valueOf(xsr.getText());
                                break;
                            case "MaxDepthTreeInit":
                                MaxDepthTreeInit = Integer.valueOf(xsr.getText());
                                break;
                            case "MaxDepthTreeAfterCrossover":
                                MaxDepthTreeAfterCrossover = Integer.valueOf(xsr.getText());
                                break;
                            case "ReproductionProbability":
                                ReproductionProbability = Double.valueOf(xsr.getText());
                                break;
                            case "MutationProbability":
                                MutationProbability = Double.valueOf(xsr.getText());
                                break;
                            case "CrossingProbability":
                                CrossingProbability = Double.valueOf(xsr.getText());
                                break;
                            case "Selection":
                                Selection = Integer.valueOf(xsr.getText());
                                break;
                            case "Elitism":
                                Elitism = Boolean.valueOf(xsr.getText());
                                break;
                            case "Decimation":
                                Decimation = Boolean.valueOf(xsr.getText());
                                break;
                            case "Arita":
                                Arita = Integer.valueOf(xsr.getText());
                                break;
                            case "FunctionValue":
                                Functions.addAll(DataHandler.getInstance().generateFunctions(xsr.getText().trim(), Arita));
                                break;
                            case "Terminal":
                                Terminals.addAll(DataHandler.getInstance().generateTerminals(xsr.getText().trim()));
                                break;
                        }
                        element = "";
                    } // načítáme konec elementu
                    else if ((xsr.getEventType() == XMLStreamConstants.END_ELEMENT)) {
                        if ((xsr.getName().getLocalPart().equals("Configuration"))) {
                            configurations.add(new Configuration(Id, NumberOfStarts, PopulationSize, NumberOfGenerations, MaxDepthTreeInit, MaxDepthTreeAfterCrossover, ReproductionProbability, MutationProbability, CrossingProbability, Selection, Elitism, Decimation, Functions, Terminals));
                        }
                    }
                    xsr.next();
                }
            } catch (Exception e) {
                System.err.println(Localizator.getString("warning.error.read") + e.getMessage());
            } finally {
                try {
                    xsr.close();
                } catch (Exception e) {
                    System.err.println(Localizator.getString("warning.error.close") + e.getMessage());
                }
            }

            Messenger.getInstance().AddMesseage(Localizator.getString("output.calculation.progress"));
            Messenger.getInstance().GetMesseage();

            // ČAS to vše pustit
            AutomatController ac = new AutomatController(configurations, outputs, finalOutputs);
            ac.start();
        }
    }

    public static class Configuration {

        int Id;
        int NumberOfStarts;
        int PopulationSize;
        int NumberOfGenerations;
        int MaxDepthTreeInit;
        int MaxDepthTreeAfterCrossover;
        double ReproductionProbability;
        double MutationProbability;
        double CrossingProbability;
        int Selection;
        boolean Elitism;
        boolean Decimation;
        List<Gen> Functions;
        List<Gen> Terminals;

        public Configuration(int Id, int NumberOfStarts, int PopulationSize,
                int NumberOfGenerations, int MaxDepthTreeInit, int MaxDepthTreeAfterCrossover,
                double ReproductionProbability, double MutationProbability, double CrossingProbability,
                int Selection, boolean Elitism, boolean Decimation, List<Gen> Functions, List<Gen> Terminals) {
            this.Id = Id;
            this.NumberOfStarts = NumberOfStarts;
            this.PopulationSize = PopulationSize;
            this.NumberOfGenerations = NumberOfGenerations;
            this.MaxDepthTreeInit = MaxDepthTreeInit;
            this.MaxDepthTreeAfterCrossover = MaxDepthTreeAfterCrossover;
            this.ReproductionProbability = ReproductionProbability;
            this.MutationProbability = MutationProbability;
            this.CrossingProbability = CrossingProbability;
            this.Selection = Selection;
            this.Elitism = Elitism;
            this.Decimation = Decimation;
            this.Functions = Functions;
            this.Terminals = Terminals;
        }

    }

    public static class Output {

        BigDecimal Fitness;
        String formula;

        public Output(BigDecimal Fitness, String formula) {
            this.Fitness = Fitness;
            this.formula = formula;
        }

        @Override
        public String toString() {
            return formula + ": " + Fitness.toString();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Output)) {
                return false;
            }
            Output otherObj = (Output) obj;

            return formula.equals(otherObj.formula);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 19 * hash + Objects.hashCode(this.formula);
            return hash;
        }

    }

    public class AutomatController extends Thread {

        List<Configuration> configurations;
        List<Map<Output, Integer>> outputs;
        List<Map<BigDecimal, Integer>> finalOutputs;

        public AutomatController(List<Configuration> configurations, List<Map<Output, Integer>> outputs, List<Map<BigDecimal, Integer>> finalOutputs) {
            this.configurations = configurations;
            this.outputs = outputs;
            this.finalOutputs = finalOutputs;
        }

        @Override
        public void run() {
            Document doc = null;
            int numberOfConfi = configurations.size();
            long startTime = System.currentTimeMillis();
            //TOHLE POŘEŠÍ ČASOVOU NÁROČNOST STUPAJÍCÍ V ČASE
            GeneticAlgorithm ga = new GeneticAlgorithm(true);
            //UPRAVA -> PŘESUN DO LOOPU - loadedConfi
            for (int i = 0; i < numberOfConfi; i++) {
                Configuration loadedConfi = configurations.get(i);
                System.out.println("Start Confi" + i + " in: " + startTime);
                outputs.add(i, new HashMap<>());
                finalOutputs.add(i, new HashMap<>());
                int numberOfStart = loadedConfi.NumberOfStarts;
                for (int j = 0; j < numberOfStart; j++) {
                    Chromosome bestChromosome = ga.runGP(
                            loadedConfi.NumberOfGenerations,
                            loadedConfi.PopulationSize,
                            loadedConfi.MaxDepthTreeInit,
                            loadedConfi.MaxDepthTreeAfterCrossover,
                            loadedConfi.ReproductionProbability,
                            loadedConfi.CrossingProbability,
                            loadedConfi.MutationProbability,
                            loadedConfi.Elitism,
                            loadedConfi.Decimation,
                            loadedConfi.Functions,
                            loadedConfi.Terminals
                    );
                    // Zde je čas zapsat si výsledek
                    BigDecimal item = bestChromosome.getFitness().getValue();
                    //Pokud nějak vznikla absordita, měli bychom jí smazat z výsledků
                    // System.out.println("ITEM: " +  item.doubleValue());
                    if (item.doubleValue() >= 9999.9f /*|| item.doubleValue() <= 0.0000001*/) {
                        System.out.println("Cal. N.: " + j + ": " + (startTime - System.currentTimeMillis()));
                        continue;
                    }

                    Output output = new Output(item, bestChromosome.getRoot().print());
                    System.out.println("Cal. N.: " + j + ": " + (startTime - System.currentTimeMillis()));
                    if (outputs.get(i).containsKey(output)) {
                        outputs.get(i).put(output, outputs.get(i).get(output) + 1);

                    } else {
                        outputs.get(i).put(output, 1);
                    }
                    if (finalOutputs.get(i).containsKey(item)) {
                        finalOutputs.get(i).put(item, finalOutputs.get(i).get(item) + 1);
                    } else {
                        finalOutputs.get(i).put(item, 1);
                    }
                }
                //Zpráva o výstupu
                Messenger.getInstance().AddMesseage("Configuration " + configurations.get(i).Id + " done");
                Messenger.getInstance().GetMesseage();
            }
            // Zde je čas zapsat výsledek do xml
            //Budeme chtít všechny nalezené formule a jejich fitness
            //A budeme chtít celkový počet nalezených řešení dle fitness bez ohledu na formu řešení
            DocumentBuilderFactory docFactory;
            DocumentBuilder docBuilder;
            try {
                docFactory = DocumentBuilderFactory.newInstance();
                docBuilder = docFactory.newDocumentBuilder();

                // root element
                doc = docBuilder.newDocument();
                Element rootElement = doc.createElement("Application");
                doc.appendChild(rootElement);

                for (int i = 0; i < numberOfConfi; i++) {
                    // confi elements
                    Element confi = doc.createElement("Configuration");
                    rootElement.appendChild(confi);
                    confi.setAttribute("id", String.valueOf(i));

                    Element wrapOutputsElement = doc.createElement("Outputs");
                    confi.appendChild(wrapOutputsElement);

                    for (Map.Entry<Output, Integer> entry : outputs.get(i).entrySet()) {
                        Output key = entry.getKey();
                        Integer value = entry.getValue();

                        Element outputElement = doc.createElement("Output");
                        wrapOutputsElement.appendChild(outputElement);

                        Element outputFitnessElement = doc.createElement("Fitness");
                        outputFitnessElement.appendChild(doc.createTextNode(key.Fitness.toString()));
                        outputElement.appendChild(outputFitnessElement);

                        Element outputFormulaElement = doc.createElement("Formula");
                        outputFormulaElement.appendChild(doc.createTextNode(key.formula));
                        outputElement.appendChild(outputFormulaElement);
                    }

                    Element wrapResultsElement = doc.createElement("Results");
                    confi.appendChild(wrapResultsElement);

                    for (Map.Entry<BigDecimal, Integer> entry : finalOutputs.get(i).entrySet()) {
                        BigDecimal key = entry.getKey();
                        Integer value = entry.getValue();

                        Element resultElement = doc.createElement("Result");
                        wrapResultsElement.appendChild(resultElement);

                        Element resultFitnessElement = doc.createElement("Fitness");
                        resultFitnessElement.appendChild(doc.createTextNode(key.toString()));
                        resultElement.appendChild(resultFitnessElement);

                        Element resultQuantityElement = doc.createElement("Quantity");
                        resultQuantityElement.appendChild(doc.createTextNode(String.valueOf(value)));
                        resultElement.appendChild(resultQuantityElement);
                    }
                }

                // write the content into xml file
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new File("Output_" + DataHandler.getInstance().getLoadedDataFile().getName()));
                transformer.transform(source, result);

                Messenger.getInstance().AddMesseage(Localizator.getString("ouptut.file.saved"));
                Messenger.getInstance().GetMesseage();

                System.out.println(Localizator.getString("output.done"));
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (TransformerConfigurationException ex) {
                Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (TransformerException ex) {
                Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
