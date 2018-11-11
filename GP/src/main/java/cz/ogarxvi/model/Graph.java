/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.model;

import cz.ogarxvi.genetic.Chromosome;
import cz.ogarxvi.genetic.Gen;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.transform.Scale;

public class Graph {
    /**
     * Model pro držení všech buněk a linek
     */
    private Model model;
    /**
     * Vykreslovací prostor
     */
    private Group canvas;
    /**
     * Zoomable prostor 
     */
    private ZoomableScrollPane scrollPane;
    /**
     * Layer buňky
     */
    CellLayer cellLayer;
    /**
     * Vytvoří Graf
     */
    public Graph() {

        this.model = new Model();

        canvas = new Group();
        cellLayer = new CellLayer();

        canvas.getChildren().add(cellLayer);

        scrollPane = new ZoomableScrollPane(canvas);

        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

    }
    /**
     * Vrátí ScrollPane
     * @return ScrollPane
     */
    public ScrollPane getScrollPane() {
        return this.scrollPane;
    }
    /**
     * Vrátí CellLayer
     * @return CellLayer
     */
    public Pane getCellLayer() {
        return this.cellLayer;
    }
    /**
     * Vrátí Model  
     * @return Model
     */
    public Model getModel() {
        return model;
    }
    /**
     * Ukončí nahrávání kompoment a uloží je do modelu do prostoru
     */
    public void endUpdate() {

        // Přidat kompomenty
        getCellLayer().getChildren().addAll(model.getAddedEdges());
        getCellLayer().getChildren().addAll(model.getAddedCells());

        // Odstranit kompomenty 
        getCellLayer().getChildren().removeAll(model.getRemovedCells());
        getCellLayer().getChildren().removeAll(model.getRemovedEdges());

        // Sloučit model
        getModel().merge();

    }
    /**
     * Vrátit Scale Value
     * @return Scale
     */
    public double getScale() {
        return this.scrollPane.getScaleValue();
    }
    /**
     * Přidá chromosome do modelu
     * @param chromosome Chromosome
     */
    public void addGraphComponents(Chromosome chromosome) {
        //Získá model
        Model model = this.getModel();

        //Získej celý strom jako jeden list genů
        Gen root = chromosome.getRoot();
        List<Gen> tree = root.getAll();

        //Připrav listy jako pole hloubek
        List<List<Gen>> arraysByDepth = new ArrayList<>(20);
        for (int i = 0; i < 20; i++) {
            arraysByDepth.add(i, new ArrayList<>());
        }

        //Pro každý gen dle jeho hloubky ho dosat do jeho listu hloubky
        tree.forEach((gen) -> {
            arraysByDepth.get(gen.getDepth()).add(gen);
        });

        //dle hloubky je zařad do buněk
        arraysByDepth.forEach((list) -> {
            list.forEach((gen) -> {
                model.addCell(gen, gen.getCommand(), arraysByDepth.indexOf(list), 20);
            });
        });

        //Pak zařaď bunkám jejich čáry
        arraysByDepth.forEach((list) -> {
            for (Gen gen : list) {
                if (gen.getArita() == 0) {
                    continue;
                }
                if (gen.getArita() == 1) {
                    model.addEdge(gen, gen.getGens().get(0));
                }
                if (gen.getArita() == 2) {
                    model.addEdge(gen, gen.getGens().get(0));
                    model.addEdge(gen, gen.getGens().get(1));
                }
            }
        });
        this.endUpdate();

    }
    /**
     * Model obalující buňky a linky
     */
    public class Model {
        /**
         * Rodič všech buněk, který němají rodiče
         */
        Cell graphParent;
        /**
         * Všechny buňky
         */
        List<Cell> allCells;
        /**
         * Přidané buňky
         */
        List<Cell> addedCells;
        /**
         * Odstraněné buňky
         */
        List<Cell> removedCells;
        /**
         * Všechny linky
         */
        List<Edge> allEdges;
        /**
         *  Přidané linky
         */
        List<Edge> addedEdges;
        /**
         *  Odstraněné linky
         */
        List<Edge> removedEdges;
        /**
         * Mapa návazností buňěk
         */
        Map<Object, Cell> cellMap; 
        /**
         * Vytvoří model
         */
        public Model() {
            graphParent = new Cell("_ROOT_");
            clear();
        }
        /**
         * Vyčistí kolekce
         */
        public void clear() {
            allCells = new ArrayList<>();
            addedCells = new ArrayList<>();
            removedCells = new ArrayList<>();
            allEdges = new ArrayList<>();
            addedEdges = new ArrayList<>();
            removedEdges = new ArrayList<>();
            cellMap = new HashMap<>();
        }
        /**
         * Vrátí přidané buňky
         * @return AddedCells
         */
        public List<Cell> getAddedCells() {
            return addedCells;
        }
        /**
         * Vrátí odstraněné buňky
         * @return RemovedCells
         */
        public List<Cell> getRemovedCells() {
            return removedCells;
        }
        /**
         * Vrátí všechny buňky
         * @return AllCells
         */
        public List<Cell> getAllCells() {
            return allCells;
        }
        /**
         * Vrátí přidané linky
         * @return AddedEdges
         */
        public List<Edge> getAddedEdges() {
            return addedEdges;
        }
        /**
         * Vrátí odstraněné linky
         * @return RemovedEdges
         */
        public List<Edge> getRemovedEdges() {
            return removedEdges;
        }
        /**
         * Vrátí všechny linky
         * @return AllEdges
         */
        public List<Edge> getAllEdges() {
            return allEdges;
        }
        /**
         * Přidá buňku dle typu
         * @param id Reference objektu
         * @param command Příkaz objektu
         * @param depth hloubka objektu
         * @param width šířka písma objektu
         * @return Buňka
         */
        public Cell addCell(Object id, String command, int depth, int width) {
                    GenCell circleCell = new GenCell(id, command, depth, width);
                    addCell(circleCell);
                    return circleCell;
        }
        /**
         * Přidá buňku do modelu a přidá vztah
         * @param cell Buňka na přidání
         */
        private void addCell(Cell cell) {

            addedCells.add(cell);

            cellMap.put(cell.getCellId(), cell);

        }
        /**
         * Přidá link mezi buňky
         * @param sourceId Start
         * @param targetId Cíl
         */
        public void addEdge(Object sourceId, Object targetId) {

            Cell sourceCell = cellMap.get(sourceId);
            Cell targetCell = cellMap.get(targetId);

            Edge edge = new Edge(sourceCell, targetCell);

            addedEdges.add(edge);

        }
        /**
         * Sloucí prvky
         */
        public void merge() {
            allCells.addAll(addedCells);
            allCells.removeAll(removedCells);
            addedCells.clear();
            removedCells.clear();
            allEdges.addAll(addedEdges);
            allEdges.removeAll(removedEdges);
            addedEdges.clear();
            removedEdges.clear();

        }
    }
    /**
     * ABstrakce Layoutu pro pozicování buněk
     */
    public abstract class Layout {
        /**
         * Vykoná rozmístění buněk
         */
        public abstract void execute();
    }
    /**
     * Stromový Layout
     */
    public class TreeLayout extends Layout {
        /**
         * Odkaz na Graf
         */
        Graph graph;
        /**
         * Vytvoří stromový layout
         * @param graph Graf
         */
        public TreeLayout(Graph graph) {
            this.graph = graph;
        }

        @Override
        public void execute() {

            List<Cell> cells = graph.getModel().getAllCells();

            int fDepth = 80;
            int pX = 400;
            int pY = 50;

            double x = 0;
            double y = 0;

            //Připrav listy jako pole hloubek
            List<List<GenCell>> arraysByDepth = new ArrayList<>(20);
            for (int j = 0; j < 20; j++) {
                arraysByDepth.add(j, new ArrayList<>());
            }

            //Pro každý gen dle jeho hloubky ho dosat do jeho listu hloubky
            for (Cell cell : cells) {
                GenCell cellP = (GenCell) cell;
                arraysByDepth.get(cellP.depth).add(cellP);
            }

            //dle hloubky je zařad do buněk
            //J = délka
            for (int j = 0; j < arraysByDepth.size(); j++) {
                // K = šířka
                for (int k = 0; k < arraysByDepth.get(j).size(); k++) {
                    x = (k * fDepth) + pX - ((arraysByDepth.get(j).size() * fDepth) / 2);
                    y = (j * fDepth) + pY;
                    arraysByDepth.get(j).get(k).relocate(x, y);
                }
            }
        }
    }
    /**
     * Buňka popisující obecný Gen
     */
    public class GenCell extends Cell {
        /**
         * Hloubka genu
         */
        int depth;
        /**
         * Grafická prezentace genu
         */
        Button button;
        /**
         * Šířka buňky
         */
        int width;
        /**
         * Vytvoří GenCell
         * @param id reference objektu
         * @param command příkaz objektu
         * @param depth hloubka genu
         * @param width šířka genu
         */
        public GenCell(Object id, String command, int depth, int width) {
            super(id);
            this.depth = depth;
            this.width = width;
            button = new Button(command);
            setView(button);
        }
    }
    /**
     * Zoomovatelný prostor
     */
    public class ZoomableScrollPane extends ScrollPane {
        /**
         * Vykreslovací skupina
         */
        Group zoomGroup;
        /**
         * Škálování
         */
        Scale scaleTransform;
        /**
         * Obsah na vykreslení
         */
        Node content;
        /**
         * Výchozí hodnota
         */
        double scaleValue = 1.0;
        /**
         * Hodnota delta
         */
        double delta = 0.1;
        /**
         * Vytvoří zoom prostor
         * @param content Obsah
         */
        public ZoomableScrollPane(Node content) {
            this.content = content;
            Group contentGroup = new Group();
            zoomGroup = new Group();
            contentGroup.getChildren().add(zoomGroup);
            zoomGroup.getChildren().add(content);
            setContent(contentGroup);
            scaleTransform = new Scale(scaleValue, scaleValue, 0, 0);
            zoomGroup.getTransforms().add(scaleTransform);

            zoomGroup.setOnScroll(new ZoomHandler());
        }
        /**
         * Vrátí Scale
         * @return Scale
         */
        public double getScaleValue() {
            return scaleValue;
        }
        /**
         * Nastaví zoom na aktuální
         */
        public void zoomToActual() {
            zoomTo(1.0);
        }
        /**
         * Upraví škálu
         * @param scaleValue nová hodnota škály
         */
        public void zoomTo(double scaleValue) {

            this.scaleValue = scaleValue;

            scaleTransform.setX(scaleValue);
            scaleTransform.setY(scaleValue);

        }
        /**
         * Přiblíží aktuální
         */
        public void zoomActual() {

            scaleValue = 1;
            zoomTo(scaleValue);

        }
        /**
         * Oddálí
         */
        public void zoomOut() {
            scaleValue -= delta;

            if (Double.compare(scaleValue, 0.1) < 0) {
                scaleValue = 0.1;
            }

            zoomTo(scaleValue);
        }
        /**
         * Přiblíží
         */
        public void zoomIn() {

            scaleValue += delta;

            if (Double.compare(scaleValue, 10) > 0) {
                scaleValue = 10;
            }

            zoomTo(scaleValue);

        }

        /**
         * Přiblíží, pokud je to povoleno
         * @param minimizeOnly povolení přibližovat
         */
        public void zoomToFit(boolean minimizeOnly) {

            double scaleX = getViewportBounds().getWidth() / getContent().getBoundsInLocal().getWidth();
            double scaleY = getViewportBounds().getHeight() / getContent().getBoundsInLocal().getHeight();
            scaleX *= scaleValue;
            scaleY *= scaleValue;
            double scale = Math.min(scaleX, scaleY);
            if (minimizeOnly) {
                if (Double.compare(scale, 1) > 0) {
                    scale = 1;
                }
            }
            zoomTo(scale);
        }
        /**
         * Ovládá zoom 
         */
        private class ZoomHandler implements EventHandler<ScrollEvent> {

            @Override
            public void handle(ScrollEvent scrollEvent) {
                {
                    if (scrollEvent.getDeltaY() < 0) {
                        scaleValue -= delta;
                    } else {
                        scaleValue += delta;
                    }
                    zoomTo(scaleValue);
                    scrollEvent.consume();
                }
            }
        }
    }
    /**
     * Představuje linku
     */
    public class Edge extends Group {
        /**
         * Zdrojová buňka
         */
        protected Cell source;
        /**
         * Cílová buňka
         */
        protected Cell target;
        /**
         * Samotná linka
         */
        Line line;
        /**
         * Vytvoří linka
         * @param source zdroj
         * @param target cíl
         */
        public Edge(Cell source, Cell target) {

            this.source = source;
            this.target = target;
            
            source.addCellChild(target);
            target.addCellParent(source);

            line = new Line();

            line.startXProperty().bind((source.layoutXProperty().add(source.getBoundsInParent().getWidth() / 2.0)).add(((GenCell)source).width/2));
            line.startYProperty().bind((source.layoutYProperty().add(source.getBoundsInParent().getHeight() / 2.0)));

            line.endXProperty().bind((target.layoutXProperty().add(target.getBoundsInParent().getWidth() / 2.0)).add(((GenCell)source).width/2));
            line.endYProperty().bind((target.layoutYProperty().add(target.getBoundsInParent().getHeight() / 2.0)));

            getChildren().add(line);

        }
        /**
         * Vrátí zdroj
         * @return Bunka
         */
        public Cell getSource() {
            return source;
        }
        /**
         * Vrátí cíl
         * @return Bunka
         */
        public Cell getTarget() {
            return target;
        }

    }
    /**
     * Layer pro buňku
     */
    public class CellLayer extends Pane {

    }
    /**
     * Základní třída buňky
     */
    public class Cell extends Pane {
        /**
         * Odkaz na objekt
         */
        Object cellId;
        /**
         * Seznam potomků
         */
        List<Cell> children = new ArrayList<>();
        /**
         * Seznam rodičů
         */
        List<Cell> parents = new ArrayList<>();
        /**
         * Vzhled buňky
         */
        Node view;
        /**
         * Vytvoří buňku
         * @param cellId Reference objektu
         */
        public Cell(Object cellId) {
            this.cellId = cellId;
        }
        /**
         * Přidá potomka
         * @param cell potomek
         */
        public void addCellChild(Cell cell) {
            children.add(cell);
        }
        /**
         * Přidá rodiče
         * @param cell rodič
         */
        public void addCellParent(Cell cell) {
            parents.add(cell);
        }
        /**
         * Nastaví vzhled
         * @param view Vzhled
         */
        public void setView(Node view) {
            this.view = view;
            getChildren().add(view);
        }
        /**
         * Vrátí odkaz na objekt
         * @return Odkat objektu
         */
        public Object getCellId() {
            return cellId;
        }
    }

}
