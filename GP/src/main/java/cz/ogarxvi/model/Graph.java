/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.ogarxvi.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Scale;

public class Graph {

    private Model model;

    private Group canvas;

    private ZoomableScrollPane scrollPane;

    MouseGestures mouseGestures;

    /**
     * the pane wrapper is necessary or else the scrollpane would always align
     * the top-most and left-most child to the top and left eg when you drag the
     * top child down, the entire scrollpane would move down
     */
    CellLayer cellLayer;

    public Graph() {

        this.model = new Model();

        canvas = new Group();
        cellLayer = new CellLayer();

        canvas.getChildren().add(cellLayer);

        mouseGestures = new MouseGestures(this);

        scrollPane = new ZoomableScrollPane(canvas);

        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

    }

    public ScrollPane getScrollPane() {
        return this.scrollPane;
    }

    public Pane getCellLayer() {
        return this.cellLayer;
    }

    public Model getModel() {
        return model;
    }

    public void beginUpdate() {
    }

    public void endUpdate() {

        // add components to graph pane
        getCellLayer().getChildren().addAll(model.getAddedEdges());
        getCellLayer().getChildren().addAll(model.getAddedCells());

        // remove components from graph pane
        getCellLayer().getChildren().removeAll(model.getRemovedCells());
        getCellLayer().getChildren().removeAll(model.getRemovedEdges());

        // enable dragging of cells
        for (Cell cell : model.getAddedCells()) {
            mouseGestures.makeDraggable(cell);
        }

        // every cell must have a parent, if it doesn't, then the graphParent is
        // the parent
        getModel().attachOrphansToGraphParent(model.getAddedCells());

        // remove reference to graphParent
        getModel().disconnectFromGraphParent(model.getRemovedCells());

        // merge added & removed cells with all cells
        getModel().merge();

    }

    public double getScale() {
        return this.scrollPane.getScaleValue();
    }

    public void addGraphComponents() {

        Model model = this.getModel();

        this.beginUpdate();

        //Zde namontovat model dle nejlepšího chromosomu
        //To znamená, že se zde initne CellType
        //Jaký problém nastane v případě nastavení 
        model.addCell("+", CellType.TRIANGLE);
        model.addCell("1", CellType.TRIANGLE);
        model.addCell("-", CellType.TRIANGLE);
        model.addCell("2", CellType.TRIANGLE);
        model.addCell("3", CellType.TRIANGLE);

        model.addEdge("+", "1");
        model.addEdge("+", "-");
        model.addEdge("-", "2");
        model.addEdge("-", "3");

        this.endUpdate();

    }

    public class Model {

        Cell graphParent;

        List<Cell> allCells;
        List<Cell> addedCells;
        List<Cell> removedCells;

        List<Edge> allEdges;
        List<Edge> addedEdges;
        List<Edge> removedEdges;

        Map<String, Cell> cellMap; // <id,cell>

        public Model() {

            graphParent = new Cell("_ROOT_");

            // clear model, create lists
            clear();
        }

        public void clear() {

            allCells = new ArrayList<>();
            addedCells = new ArrayList<>();
            removedCells = new ArrayList<>();

            allEdges = new ArrayList<>();
            addedEdges = new ArrayList<>();
            removedEdges = new ArrayList<>();

            cellMap = new HashMap<>(); // <id,cell>

        }

        public void clearAddedLists() {
            addedCells.clear();
            addedEdges.clear();
        }

        public List<Cell> getAddedCells() {
            return addedCells;
        }

        public List<Cell> getRemovedCells() {
            return removedCells;
        }

        public List<Cell> getAllCells() {
            return allCells;
        }

        public List<Edge> getAddedEdges() {
            return addedEdges;
        }

        public List<Edge> getRemovedEdges() {
            return removedEdges;
        }

        public List<Edge> getAllEdges() {
            return allEdges;
        }

        public void addCell(String id, CellType type) {

            switch (type) {

                /* case RECTANGLE:
            RectangleCell rectangleCell = new RectangleCell(id);
            addCell(rectangleCell);
            break;
                 */
                case TRIANGLE:
                    TriangleCell circleCell = new TriangleCell(id);
                    addCell(circleCell);
                    break;
                case CIRCLE:
                    CircleCell circleCell2 = new CircleCell(id);
                    addCell(circleCell2);
                    break;

                default:
                    throw new UnsupportedOperationException("Unsupported type: " + type);
            }
        }

        private void addCell(Cell cell) {

            addedCells.add(cell);

            cellMap.put(cell.getCellId(), cell);

        }

        public void addEdge(String sourceId, String targetId) {

            Cell sourceCell = cellMap.get(sourceId);
            Cell targetCell = cellMap.get(targetId);

            Edge edge = new Edge(sourceCell, targetCell);

            addedEdges.add(edge);

        }

        /**
         * Attach all cells which don't have a parent to graphParent
         *
         * @param cellList
         */
        public void attachOrphansToGraphParent(List<Cell> cellList) {

            for (Cell cell : cellList) {
                if (cell.getCellParents().size() == 0) {
                    graphParent.addCellChild(cell);
                }
            }

        }

        /**
         * Remove the graphParent reference if it is set
         *
         * @param cellList
         */
        public void disconnectFromGraphParent(List<Cell> cellList) {

            for (Cell cell : cellList) {
                graphParent.removeCellChild(cell);
            }
        }

        public void merge() {

            // cells
            allCells.addAll(addedCells);
            allCells.removeAll(removedCells);

            addedCells.clear();
            removedCells.clear();

            // edges
            allEdges.addAll(addedEdges);
            allEdges.removeAll(removedEdges);

            addedEdges.clear();
            removedEdges.clear();

        }
    }

    public class MouseGestures {

        final DragContext dragContext = new DragContext();

        Graph graph;

        public MouseGestures(Graph graph) {
            this.graph = graph;
        }

        public void makeDraggable(final Node node) {

            node.setOnMousePressed(onMousePressedEventHandler);
            node.setOnMouseDragged(onMouseDraggedEventHandler);
            node.setOnMouseReleased(onMouseReleasedEventHandler);

        }

        EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                Node node = (Node) event.getSource();

                double scale = graph.getScale();

                dragContext.x = node.getBoundsInParent().getMinX() * scale - event.getScreenX();
                dragContext.y = node.getBoundsInParent().getMinY() * scale - event.getScreenY();

            }
        };

        EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                Node node = (Node) event.getSource();

                double offsetX = event.getScreenX() + dragContext.x;
                double offsetY = event.getScreenY() + dragContext.y;

                // adjust the offset in case we are zoomed
                double scale = graph.getScale();

                offsetX /= scale;
                offsetY /= scale;

                node.relocate(offsetX, offsetY);

            }
        };

        EventHandler<MouseEvent> onMouseReleasedEventHandler = new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

            }
        };

        class DragContext {

            double x;
            double y;

        }
    }

    public abstract class Layout {

        public abstract void execute();

    }

    public class RandomLayout extends Layout {

        Graph graph;

        Random rnd = new Random();

        public RandomLayout(Graph graph) {

            this.graph = graph;

        }

        @Override
        public void execute() {

            List<Cell> cells = graph.getModel().getAllCells();

            for (Cell cell : cells) {

                double x = rnd.nextDouble() * 500;
                double y = rnd.nextDouble() * 500;

                cell.relocate(x, y);

            }

        }

    }

    public class TriangleCell extends Cell {

        public TriangleCell(String id) {
            super(id);

            double width = 50;
            double height = 50;

            Polygon view = new Polygon(width / 2, 0, width, height, 0, height);

            view.setStroke(Color.RED);
            view.setFill(Color.RED);

            setView(view);

        }

    }

    public class ZoomableScrollPane extends ScrollPane {

        Group zoomGroup;
        Scale scaleTransform;
        Node content;
        double scaleValue = 1.0;
        double delta = 0.1;

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

        public double getScaleValue() {
            return scaleValue;
        }

        public void zoomToActual() {
            zoomTo(1.0);
        }

        public void zoomTo(double scaleValue) {

            this.scaleValue = scaleValue;

            scaleTransform.setX(scaleValue);
            scaleTransform.setY(scaleValue);

        }

        public void zoomActual() {

            scaleValue = 1;
            zoomTo(scaleValue);

        }

        public void zoomOut() {
            scaleValue -= delta;

            if (Double.compare(scaleValue, 0.1) < 0) {
                scaleValue = 0.1;
            }

            zoomTo(scaleValue);
        }

        public void zoomIn() {

            scaleValue += delta;

            if (Double.compare(scaleValue, 10) > 0) {
                scaleValue = 10;
            }

            zoomTo(scaleValue);

        }

        /**
         *
         * @param minimizeOnly If the content fits already into the viewport,
         * then we don't zoom if this parameter is true.
         */
        public void zoomToFit(boolean minimizeOnly) {

            double scaleX = getViewportBounds().getWidth() / getContent().getBoundsInLocal().getWidth();
            double scaleY = getViewportBounds().getHeight() / getContent().getBoundsInLocal().getHeight();

            // consider current scale (in content calculation)
            scaleX *= scaleValue;
            scaleY *= scaleValue;

            // distorted zoom: we don't want it => we search the minimum scale
            // factor and apply it
            double scale = Math.min(scaleX, scaleY);

            // check precondition
            if (minimizeOnly) {

                // check if zoom factor would be an enlargement and if so, just set
                // it to 1
                if (Double.compare(scale, 1) > 0) {
                    scale = 1;
                }
            }

            // apply zoom
            zoomTo(scale);

        }

        private class ZoomHandler implements EventHandler<ScrollEvent> {

            @Override
            public void handle(ScrollEvent scrollEvent) {
                // if (scrollEvent.isControlDown())
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

    public class Edge extends Group {

        protected Cell source;
        protected Cell target;

        Line line;

        public Edge(Cell source, Cell target) {

            this.source = source;
            this.target = target;

            source.addCellChild(target);
            target.addCellParent(source);

            line = new Line();

            line.startXProperty().bind(source.layoutXProperty().add(source.getBoundsInParent().getWidth() / 2.0));
            line.startYProperty().bind(source.layoutYProperty().add(source.getBoundsInParent().getHeight() / 2.0));

            line.endXProperty().bind(target.layoutXProperty().add(target.getBoundsInParent().getWidth() / 2.0));
            line.endYProperty().bind(target.layoutYProperty().add(target.getBoundsInParent().getHeight() / 2.0));

            getChildren().add(line);

        }

        public Cell getSource() {
            return source;
        }

        public Cell getTarget() {
            return target;
        }

    }

    public class CellLayer extends Pane {

    }

    public class Cell extends Pane {

        String cellId;

        List<Cell> children = new ArrayList<>();
        List<Cell> parents = new ArrayList<>();

        Node view;

        public Cell(String cellId) {
            this.cellId = cellId;
        }

        public void addCellChild(Cell cell) {
            children.add(cell);
        }

        public List<Cell> getCellChildren() {
            return children;
        }

        public void addCellParent(Cell cell) {
            parents.add(cell);
        }

        public List<Cell> getCellParents() {
            return parents;
        }

        public void removeCellChild(Cell cell) {
            children.remove(cell);
        }

        public void setView(Node view) {

            this.view = view;
            getChildren().add(view);

        }

        public Node getView() {
            return this.view;
        }

        public String getCellId() {
            return cellId;
        }
    }

    public enum CellType {

        RECTANGLE,
        TRIANGLE,
        CIRCLE;

    }

    public class CircleCell extends Cell {

        public CircleCell(String id) {
            super(id);

            Circle view = new Circle(50);

            view.setStroke(Color.DODGERBLUE);
            view.setFill(Color.DODGERBLUE);

            setView(view);

        }

    }
}
