/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.model;

import cz.ogarxvi.controller.FXMLController;
import cz.ogarxvi.model.genetic.Chromosome;
import cz.ogarxvi.model.genetic.Gen;
import cz.ogarxvi.model.genetic.GeneticAlgorithm;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author OgarXVI
 */
public class ConfigurationThread extends Thread {

    private List<Configuration> configurations;
    private List<Map<Output, Integer>> outputs;
    private List<Map<BigDecimal, Integer>> finalOutputs;

    private String fileName;
    
    public ConfigurationThread(String FileName) {
        configurations = new ArrayList<>();
        outputs = new ArrayList<>();
        finalOutputs = new ArrayList<>();
        fileName = FileName;
        
    }

    public boolean LoadData() throws URISyntaxException {
        //Check
        if (!DataHandler.getInstance().isLoaded()) {
            JOptionPane.showConfirmDialog(null, Localizator.getString("warning.error.load.file"));
            return false;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()));
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader xsr = null;
            try {
                xsr = factory.createXMLStreamReader(new FileReader(selectedFile));
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
                return true;
            } finally {
                try {
                    xsr.close();
                } catch (Exception e) {
                    System.err.println(Localizator.getString("warning.error.close") + e.getMessage());
                    return true;
                }
            }
            //Messenger.getInstance().AddMesseage(Localizator.getString("output.calculation.progress"));
            //Messenger.getInstance().GetMesseage();
        }
        return true;
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
            //Messenger.getInstance().AddMesseage("Configuration " + configurations.get(i).Id + " done");
            //Messenger.getInstance().GetMesseage();
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
            StreamResult result = new StreamResult(new File("Output_" + fileName));
            transformer.transform(source, result);

            //Messenger.getInstance().AddMesseage(Localizator.getString("ouptut.file.saved"));
            //Messenger.getInstance().GetMesseage();
            System.out.println(Localizator.getString("output.done"));
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    class Output {

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

    public class Configuration {

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
}
