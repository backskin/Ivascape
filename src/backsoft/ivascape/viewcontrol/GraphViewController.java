package backsoft.ivascape.viewcontrol;

import backsoft.ivascape.handler.Loader;
import backsoft.ivascape.model.Graph;
import backsoft.ivascape.model.GraphOnList;
import backsoft.ivascape.logic.Pair;
import backsoft.ivascape.model.Company;
import backsoft.ivascape.logic.CoorsMap;
import backsoft.ivascape.model.Link;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

import java.util.*;

public class GraphViewController implements ViewController {

    private final Map<Node, VisualVertex> dragMap = new HashMap<>();
    private final Map<Integer, VisualVertex> visVerHashMap = new HashMap<>();
    private final Map<Link, VisualEdge> visEdgeMap = new HashMap<>();
    private CoorsMap coorsMap = new CoorsMap();

    private boolean draggable = false;

    private Graph<Company, Link> graph;

    @FXML
    private Pane surface;

    private final DoubleProperty scaleProperty = new SimpleDoubleProperty(100.0);
    private final BooleanProperty priceShownProperty =  new SimpleBooleanProperty(false);
    private final BooleanProperty surfaceSavedProperty = new SimpleBooleanProperty(true);

    void bindToSurfaceChanged(BooleanProperty property){
        property.bindBidirectional(surfaceSavedProperty);
    }

    void setScale(double scaleProperty){ this.scaleProperty.setValue(scaleProperty); }

    void setPricesVisible(boolean answer){ priceShownProperty.setValue(answer); }

    private void shiftGraph(double xShift, double yShift){

        for (Iterator<Company> iterator = graph.getVertexIterator(); iterator.hasNext();) {
            VisualVertex vv = visVerHashMap.get(iterator.next().hashCode());
            vv.setAllCoors(vv.x() + xShift, vv.y() + yShift);
        }
    }

    void setView(GraphOnList<Company, Link> graph, CoorsMap coorsMap,
                 Color vertexColor, Color edgeColor, boolean isDraggable) {

        this.graph = graph;
        VisualVertex.setColor(vertexColor);
        VisualEdge.setColor(edgeColor);
        this.coorsMap = coorsMap;
        draggable = isDraggable;
    }

    void cropIt(){

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;

        for (Iterator<Company> iterator = graph.getVertexIterator(); iterator.hasNext();) {

            VisualVertex vv = visVerHashMap.get(iterator.next().hashCode());
            minX = Math.min(minX, vv.x());
            minY = Math.min(minY, vv.y());

            if (minX == 0.0 && minY == 0.0)
                return;
        }

        shiftGraph(-minX,-minY);

        surfaceSavedProperty.setValue(false);
    }

    private VisualVertex loadVertex(StringProperty title, int hashcode){

        VisualVertex vertex = (VisualVertex) Loader.loadFXML("VisualVertex").getTwo();
        vertex.setTitle(title);
        scaleProperty.addListener(vertex.getScaleListener());
        visVerHashMap.put(hashcode, vertex);
        if (draggable) {
            makeNodeDraggable(vertex, hashcode);
            dragMap.put(vertex.getCircle(), vertex);
        }
        surface.getChildren().add(vertex.getItem());
        return vertex;
    }

    public void add(Company company){

        VisualVertex vertex = loadVertex(company.titleProperty(), company.hashCode());

        vertex.setAllCoors(
                5.0 + (new Random()).nextInt(200),
                5.0 + (new Random()).nextInt(200)
        );

        coorsMap.put(company.hashCode(), new CoorsMap.Coors(vertex.x(), vertex.y()));
    }

    public void add(Link link){

        VisualVertex one = visVerHashMap.get(link.one().hashCode());
        VisualVertex two = visVerHashMap.get(link.two().hashCode());

        VisualEdge edge =  new VisualEdge(
                one.xCenterProperty(), one.yCenterProperty(),
                two.xCenterProperty(), two.yCenterProperty(), link.priceProperty());

        Label priceLabel = edge.getPriceLabel();

        priceLabel.setVisible(priceShownProperty.getValue());
        edge.getPriceLabel().setVisible(priceShownProperty.getValue());

        priceShownProperty.addListener((val, bo, bn) -> edge.getPriceLabel().setVisible(bn));

        scaleProperty.addListener((val, no, nn) -> {
            edge.getLine().setStrokeWidth(.05 * nn.doubleValue());
            edge.getPriceLabel().setFont(Font.font(
                    "System", 10 + .05 * nn.doubleValue())); });

        visEdgeMap.put(link, edge);

        surface.getChildren().add(0, edge.getLine());
        surface.getChildren().add(edge.getPriceLabel());
    }

    public void remove(Company company){

        for (Iterator<Link> it = graph.getEdgeIteratorForVertex(company); it.hasNext();)
            remove(it.next());

        scaleProperty.removeListener(visVerHashMap.get(company.hashCode()).getScaleListener());
        surface.getChildren().remove(visVerHashMap.get(company.hashCode()).getItem());

        dragMap.remove(visVerHashMap.get(company.hashCode()).getCircle());
        visVerHashMap.remove(company.hashCode());
        coorsMap.remove(company.hashCode());
    }

    public void remove(Link link) {

        VisualEdge edge = getEdge(link);
        if (edge == null) return;

        surface.getChildren().remove(edge.getPriceLabel());
        surface.getChildren().remove(edge.getLine());

        visEdgeMap.remove(link);
    }

    private VisualEdge getEdge(Link link) {

        VisualEdge out = visEdgeMap.get(link);
        if (out == null)
            out = visEdgeMap.get(link.getMating());
        return out;
    }

    public void updateView(){

        Double scaleValue = scaleProperty.getValue();
        scaleProperty.setValue(100.0);
        surface.getChildren().clear();
        for (Iterator<Company> iterator = graph.getVertexIterator(); iterator.hasNext();) {

            Company company = iterator.next();
            VisualVertex vertex = loadVertex(company.titleProperty(), company.hashCode());

            vertex.setAllCoors(
                    surface.getLayoutX() + coorsMap.get(company.hashCode()).x,
                    surface.getLayoutY() + coorsMap.get(company.hashCode()).y);
        }

        for (Iterator<Link> it = graph.getEdgeIterator(); it.hasNext();) add(it.next());
        scaleProperty.setValue(scaleValue);
    }

    private void makeNodeDraggable(VisualVertex vertex, Integer hash) {

        vertex.getCircle().setOnMousePressed(event ->
                vertex.setDragContext(new Pair<>(event.getScreenX(), event.getScreenY())));

        vertex.getCircle().setOnMouseDragged(event -> {
            Circle source = (Circle) event.getSource();
            Node item = dragMap.get(source).getItem();
                item.relocate(
                    Math.max(0, item.getLayoutX() + event.getScreenX() - vertex.getDragContext().getOne()),
                    Math.max(0, item.getLayoutY() + event.getScreenY() - vertex.getDragContext().getTwo()));

            vertex.setDragContext(new Pair<>(event.getScreenX(), event.getScreenY()));
        });

        vertex.getCircle().setOnMouseReleased(event -> {
            coorsMap.put(hash, new CoorsMap.Coors(vertex.x(), vertex.y()));
            surfaceSavedProperty.setValue(false);
        });
    }
}