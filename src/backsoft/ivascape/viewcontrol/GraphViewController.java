package backsoft.ivascape.viewcontrol;

import backsoft.ivascape.handler.Loader;
import backsoft.ivascape.model.Graph;
import backsoft.ivascape.model.GraphOnList;
import backsoft.ivascape.logic.Pair;
import backsoft.ivascape.model.Company;
import backsoft.ivascape.logic.CoorsMap;
import backsoft.ivascape.model.Link;
import javafx.beans.property.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

import java.util.*;

public class GraphViewController {

    private final Map<Node, VisualVertex> dragMap = new HashMap<>();
    private final Map<Company, VisualVertex> visVerMap = new HashMap<>();
    private final Map<Link, VisualEdge> visEdgeMap = new HashMap<>();
    private CoorsMap coorsMap = new CoorsMap();

    private boolean draggable = false;

    private Pair<Double,Double> dragContext;
    private Graph<Company, Link> graph;

    @FXML
    private Pane surface;

    private final DoubleProperty scaleProperty = new SimpleDoubleProperty(100.0);
    private final BooleanProperty priceShownProperty =  new SimpleBooleanProperty(false);
    private final BooleanProperty surfaceChangedProperty = new SimpleBooleanProperty(false);

    void bindToSurfaceChanged(BooleanProperty property){
        surfaceChangedProperty.bind(property);
    }

    void setScale(double scaleProperty){

        this.scaleProperty.setValue(scaleProperty);
    }


    void setPricesVisible(boolean answer){

        priceShownProperty.setValue(answer);
    }

    private void shiftGraph(double xShift, double yShift){

        for (Iterator<Company> iterator = graph.getVertexIterator(); iterator.hasNext();) {
            VisualVertex vv = visVerMap.get(iterator.next());
            vv.setAllCoors(vv.x() + xShift, vv.y() + yShift);
        }
    }

    void cropIt(){

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;

        for (Iterator<Company> iterator = graph.getVertexIterator(); iterator.hasNext();) {

            VisualVertex vv = visVerMap.get(iterator.next());
            minX = Math.min(minX, vv.x());
            minY = Math.min(minY, vv.y());

            if (minX == 0.0 && minY == 0.0)
                return;
        }

        shiftGraph(-minX,-minY);

        surfaceChangedProperty.setValue(true);
    }

    private CoorsMap.Coors getCoors(Company company){

        VisualVertex vv = visVerMap.get(company);
        return new CoorsMap.Coors(vv.x(), vv.y());
    }

    void setGraph(GraphOnList<Company, Link> graph, CoorsMap coorsMap,
                  Color vertexColor, Color edgeColor, boolean isDraggable) {

        this.graph = graph;
        VisualVertex.setColor(vertexColor);
        VisualEdge.setColor(edgeColor);
        this.coorsMap = coorsMap;
        draggable = isDraggable;
    }

    void addVertex(Company company){

        Pair<Parent, VisualVertex> fxml;

        fxml = Loader.loadFXML("VisualVertex");

        VisualVertex vertex = fxml.getTwo();
        vertex.setTitle(company.getTitle());
        makeNodeDraggable(vertex, company.hashCode());
        scaleProperty.addListener(vertex.getScaleListener());

        dragMap.put(vertex.getCircle(),vertex);
        visVerMap.put(company,vertex);
        vertex.setAllCoors(
                5.0 + (new Random()).nextInt(200),
                5.0 + (new Random()).nextInt(200)
        );

        coorsMap.put(company.hashCode(), getCoors(company));
    }

    void addEdge(Link link){

        VisualVertex one = visVerMap.get(link.one());
        VisualVertex two = visVerMap.get(link.two());
        VisualEdge edge =  new VisualEdge(
                one.xCenterProperty(), one.xCenterProperty(),
                two.xCenterProperty(), two.yCenterProperty(), link.getPrice());
        Label priceLabel = edge.getPriceLabel();
        priceLabel.setVisible(priceShownProperty.getValue());
        edge.getPriceLabel().setVisible(priceShownProperty.getValue());
        priceShownProperty.addListener((val, bo, bn) -> edge.getPriceLabel().setVisible(bn));
        scaleProperty.addListener((val, no, nn) -> { edge.getLine().setStrokeWidth(.05 * nn.doubleValue());
            edge.getPriceLabel().setFont(Font.font("System", 10 + .05 * nn.doubleValue())); });
        visEdgeMap.put(link, edge);
    }

    void removeVertex(Company company){

        for (Iterator<Link> it = graph.getEdgeIteratorForVertex(company); it.hasNext();)
            removeEdge(it.next());
        scaleProperty.removeListener(visVerMap.get(company).getScaleListener());
        surface.getChildren().remove(visVerMap.get(company).getItem());
        dragMap.remove(visVerMap.get(company).getCircle());
        visVerMap.remove(company);
        coorsMap.remove(company.hashCode());
    }

    void removeEdge(Link link) {

        VisualEdge edge = getEdge(link);

        if (edge == null) return;
        surface.getChildren().remove(edge.getPriceLabel());
        surface.getChildren().remove(edge.getLine());
        visEdgeMap.remove(link);
    }

    private VisualEdge getEdge(Link link) {

        VisualEdge out = visEdgeMap.get(link);
        if (out == null)
            return visEdgeMap.get(link.getMating());
        else
            return out;
    }

    void updateView(){

        Double scaleValue = scaleProperty.getValue();
        scaleProperty.setValue(100.0);
        surface.getChildren().clear();

        for (Iterator<Link> iterator = graph.getEdgeIterator(); iterator.hasNext();){

            Link link = iterator.next();
            VisualEdge edge = getEdge(link);

            if (edge == null) {

                addEdge(link);
                edge = getEdge(link);
            }

            surface.getChildren().add(edge.getLine());
            surface.getChildren().add(edge.getPriceLabel());
        }

        for (Iterator<Company> iterator = graph.getVertexIterator(); iterator.hasNext();) {

            Company company = iterator.next();
            VisualVertex vertex = visVerMap.get(company);

            if (vertex == null){
                addVertex(company);
                vertex = visVerMap.get(company);
            }

            vertex.setTitle(company.getTitle());
            vertex.setAllCoors(
                    surface.getLayoutX() + coorsMap.get(company.hashCode()).x,
                    surface.getLayoutY() + coorsMap.get(company.hashCode()).y);

            surface.getChildren().add(vertex.getItem());
            if (draggable)
                makeNodeDraggable(vertex, company.hashCode());
        }

        scaleProperty.setValue(scaleValue);
    }

    private void makeNodeDraggable(VisualVertex vertex, Integer hash) {

        vertex.getCircle().setOnMousePressed(vertexPressedHandler);

        vertex.getCircle().setOnMouseDragged(vertexDraggedHandler);

        vertex.getCircle().setOnMouseReleased(event -> {
            coorsMap.put(hash, new CoorsMap.Coors(vertex.x(), vertex.y()));
            surfaceChangedProperty.setValue(true);});
    }

    private final EventHandler<MouseEvent> vertexPressedHandler = event -> {

        Circle n = (Circle) event.getSource();
        Bounds itemBounds = dragMap.get(n).getItem().getBoundsInParent();

        dragContext = new Pair<>(
                itemBounds.getMinX() - event.getScreenX(),
                itemBounds.getMinY() - event.getScreenY());
    };

    private final EventHandler<MouseEvent> vertexDraggedHandler = event -> {

        Circle n = (Circle) event.getSource();
        Node item = dragMap.get(n).getItem();
        double X = dragContext.getOne();
        double Y = dragContext.getTwo();
        item.relocate(
                event.getScreenX() + X
                        + item.getBoundsInParent().getWidth()/2.0
                        - n.getRadius() > 0.0
                        ?
                        event.getScreenX() + X : item.getLayoutX(),

                event.getScreenY() + Y
                        + item.getBoundsInParent().getHeight()/2.0
                        - n.getRadius() > 0.0
                        ?
                        event.getScreenY() + Y : item.getLayoutY()
        );
    };
}