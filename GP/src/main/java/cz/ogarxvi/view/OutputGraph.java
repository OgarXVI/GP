/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.view;

import cz.ogarxvi.model.Messenger;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

/**
 *
 * @author OgarXVI
 */
public class OutputGraph {

    private StandardChartTheme theme;
    private HistogramDataset dataset1;
    private DefaultPieDataset dataset2;
    private JFrame framePieChart;
    private JFrame frameHistogram;
    private Map<BigDecimal, Integer> results;

    public OutputGraph(File file) {
        try {
            ReadFile(file);
            ParseResultsIntoDatasets();
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    public void createCharts() {
        JFreeChart chart2 = ChartFactory.createPieChart("Output Graph", dataset2, true, true, false);
        JFreeChart chart = ChartFactory.createHistogram("Histogram", "Value", "Frequency", dataset1, PlotOrientation.VERTICAL, true, true, true);
        frameHistogram = createHistogram(chart);
        InitStyle(chart);
        framePieChart = createPieChart(chart2);
        InitStyle(chart2);
    }

    private JFrame createPieChart(JFreeChart chart2) throws HeadlessException {
        JFrame af2 = new JFrame("Output Graph - PieChart");
        af2.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/icon.jpg")));
        af2.setContentPane(new ChartPanel(chart2));
        af2.setSize(600, 600);
        RefineryUtilities.centerFrameOnScreen(af2);
        //af2.setVisible(true);
        return af2;
    }

    private JFrame createHistogram(JFreeChart chart) throws HeadlessException {
        JFrame af = new JFrame("Output Graph - Histogram");
        af.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/icon.jpg")));
        af.setContentPane(new ChartPanel(chart));
        af.setSize(600, 600);
        RefineryUtilities.centerFrameOnScreen(af);
        //af.setVisible(true);
        return af;
    }

    public void showGraphs(boolean value) {
        frameHistogram.setVisible(value);
        framePieChart.setVisible(value);
    }

    private void ParseResultsIntoDatasets() {
        dataset1 = new HistogramDataset();
        dataset1.setType(HistogramType.FREQUENCY);

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

        dataset1.addSeries("Histogram", pomD, 100, min - 0.1f, max + 0.1f);

        // Dataset
        dataset2 = new DefaultPieDataset();
        results.entrySet().forEach((entry) -> {
            BigDecimal key = entry.getKey();
            Integer value = entry.getValue();
            dataset2.setValue(key, value);
        });
    }

    private void ReadFile(File file) throws FactoryConfigurationError {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader xsr = null;
        results = new HashMap<>();

        try {
            xsr = factory.createXMLStreamReader(new FileReader(file));
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
        } catch (NumberFormatException | XMLStreamException e) {
            Messenger.getInstance().AddMesseage("Error: " + e.getMessage());
            Messenger.getInstance().GetMesseage();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(OutputGraph.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void InitStyle(JFreeChart chart) throws NumberFormatException {
        //STYLE
        theme = (StandardChartTheme) org.jfree.chart.StandardChartTheme.createJFreeTheme();

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
    }

}
