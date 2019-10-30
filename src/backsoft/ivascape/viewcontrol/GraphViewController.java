package backsoft.ivascape.viewcontrol;

import backsoft.ivascape.handler.Loader;
import backsoft.ivascape.logic.Graph;
import backsoft.ivascape.logic.GraphOnList;
import backsoft.ivascape.logic.Pair;
import backsoft.ivascape.model.Company;
import backsoft.ivascape.model.CoorsMap;
import backsoft.ivascape.model.Link;
import javafx.beans.property.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.util.*;

import static backsoft.ivascape.viewcontrol.MyAlerts.*;

public class GraphViewController {

    private final Map<Node, VisualVertex> dragMap = new HashMap<>();
    private final Map<Company, VisualVertex> visVerMap = new HashMap<>();
    private final Map<Link, VisualEdge> visEdgeMap = new HashMap<>();
    private CoorsMap coorsMap = new CoorsMap();

    private boolean draggable = false;

    private Pair<Double,Double> dragContext;
    private Graph<Company, Link> graph;

    private final DoubleProperty scaleProperty = new SimpleDoubleProperty(100.0);
    private final BooleanProperty priceShownProperty =  new SimpleBooleanProperty(false);
    private final BooleanProperty surfaceChangedProperty = new SimpleBooleanProperty(false);

    BooleanProperty getSurfaceChangedProperty() {
        return surfaceChangedProperty;
    }

    void setScale(double scaleProperty){

        this.scaleProperty.setValue(scaleProperty);
    }

    @FXML
    private Pane surface;

    Pane getSurface() {
        return surface;
    }

    public GraphViewController(){}

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
        double tmpValue = scaleProperty.getValue();
        scaleProperty.setValue(100.0);
        loadSurface(coorsMap);
        scaleProperty.setValue(tmpValue);
    }

    private void loadSurface(CoorsMap coorsMap){

        for (int i = 0; i < graph.size(); i ++){

            Pair<Parent, VisualVertex> fxml;

            try {

                fxml = Loader.loadFXML("VisualVertex");

            } catch (IOException e){

                getAlert(AlertType.UNKNOWN, e.getMessage());
                return;
            }
            VisualVertex vertex = fxml.getTwo();

            vertex.setTitle(graph.getVertex(i).getTitle());

            dragMap.put(vertex.getCircle(),vertex);
            visVerMap.put(graph.getVertex(i),vertex);
            scaleProperty.addListener(vertex.getScaleListener());

            vertex.setAllCoors(
                    surface.getLayoutX() + coorsMap.get(graph.getVertex(i).hashCode()).x,
                    surface.getLayoutY() + coorsMap.get(graph.getVertex(i).hashCode()).y);
        }

        for (Iterator<Link> it = graph.getEdgeIterator(); it.hasNext();){

            Link link = it.next();

            VisualVertex one = visVerMap.get(link.one());
            VisualVertex another = visVerMap.get(link.two());

            VisualEdge vE = new VisualEdge(one, another, link.getPrice());
            priceShownProperty.addListener(vE.getPriceListener());
            scaleProperty.addListener(vE.getScaleListener());
            visEdgeMap.put(link,vE);
        }
    }

    private void addVertex(Company company){

        Pair<Parent, VisualVertex> fxml;

        try {
            fxml = Loader.loadFXML("VisualVertex");
        }
        catch (IOException e){

            getAlert(AlertType.UNKNOWN, e.getMessage());
            return;
        }

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

    private void addEdge(Link link){

        VisualVertex one = visVerMap.get(link.one());
        VisualVertex another = visVerMap.get(link.two());
        VisualEdge vE =  new VisualEdge(one, another, link.getPrice());
        vE.getPrice().setVisible(priceShownProperty.getValue());
        priceShownProperty.addListener(vE.getPriceListener());
        scaleProperty.addListener(vE.getScaleListener());
        visEdgeMap.put(link, vE);
    }

    void removeVertex(Company company){

        removeEdgesOf(company);
        scaleProperty.removeListener(visVerMap.get(company).getScaleListener());
        surface.getChildren().remove(visVerMap.get(company).getItem());
        dragMap.remove(visVerMap.get(company).getCircle());
        visVerMap.remove(company);
        coorsMap.remove(company.hashCode());
    }


    private void removeEdge(Link link) {

        VisualEdge edge = getEdge(link);

        if (edge == null) return;

        priceShownProperty.removeListener(edge.getPriceListener());
        scaleProperty.removeListener(edge.getScaleListener());
        surface.getChildren().remove(edge.getPrice());
        surface.getChildren().remove(edge.getLine());
        visEdgeMap.remove(link);
    }

    private void removeEdgesOf(Company company){

        for (Iterator<Link> it = graph.getEdgeIteratorForVertex(company); it.hasNext();){

            removeEdge(it.next());
        }
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

        boolean priceShownValue = priceShownProperty.getValue();
        surface.getChildren().clear();

        for (Iterator<Link> iterator = graph.getEdgeIterator(); iterator.hasNext();){

            Link link = iterator.next();
            VisualEdge edge = getEdge(link);

            if (edge == null) {

                addEdge(link);
                edge = getEdge(link);
            }

            edge.setPrice(link.getPrice());
            surface.getChildren().add(edge.getLine());
            surface.getChildren().add(edge.getPrice());
        }

        for (Iterator<Company> iterator = graph.getVertexIterator(); iterator.hasNext();) {

            Company com = iterator.next();
            VisualVertex vertex = visVerMap.get(com);

            if (vertex == null){

                addVertex(com);
                vertex = visVerMap.get(com);
            }

            vertex.setTitle(com.getTitle());
            surface.getChildren().add(vertex.getItem());
            if (draggable)
                makeNodeDraggable(vertex, com.hashCode());
        }

        priceShownProperty.setValue(priceShownValue);
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