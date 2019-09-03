package ivascape.view.main;

import ivascape.model.Company;
import ivascape.model.CoorsMap;
import ivascape.model.Link;
import ivascape.model.Project;
import ivascape.logic.*;
import ivascape.view.serve.VisualEdge;
import ivascape.view.serve.VisualVertex;
import javafx.beans.property.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.util.*;

import static ivascape.view.serve.MyAlerts.*;

public class GraphViewController {

    private GenericGraph<Company, Link> graph;

    private final Map<Node, VisualVertex> dragMap = new HashMap<>();
    private final Map<Company, VisualVertex> visVerMap = new HashMap<>();
    private final Map<Link,VisualEdge> visEdgeMap = new HashMap<>();

    private boolean draggable = false;

    private Pair<Double,Double> dragContext;

    private Project project = Project.get();

    private final DoubleProperty scaleProperty = new SimpleDoubleProperty(100.0);
    private final BooleanProperty priceShownProperty =  new SimpleBooleanProperty(false);
    private final BooleanProperty surfaceChangedProperty = new SimpleBooleanProperty(false);

    BooleanProperty getSurfaceChangedProperty() {
        return surfaceChangedProperty;
    }

    public void setScale(double scaleProperty){

        this.scaleProperty.setValue(scaleProperty);
    }

    @FXML
    private Pane surface;

    @FXML
    private void initialize(){

        surface.setFocusTraversable(false);
    }

    Pane getSurface() {
        return surface;
    }

    public GraphViewController(){}

    public void setPricesVisible(boolean answer){

        priceShownProperty.setValue(answer);
    }

    private void shiftGraph(double xShift, double yShift){

        for (Iterator<Company> iterator = graph.getVertexIterator(); iterator.hasNext();) {

            VisualVertex vv = visVerMap.get(iterator.next());
            vv.setAllCoors(vv.x() + xShift, vv.y() + yShift);
        }
    }

    public void cropIt(){

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;

        for (Iterator<Company> iterator = graph.getVertexIterator(); iterator.hasNext();) {

            VisualVertex vv = visVerMap.get(iterator.next());
            minX = minX > vv.x() ? vv.x() : minX;
            minY = minY > vv.y() ? vv.y() : minY;

            if (minX == 0.0 && minY == 0.0)
                return;
        }

        shiftGraph(-minX,-minY);

        surfaceChangedProperty.setValue(true);
    }

    CoorsMap getCoorsMap(){

        double tmpValue = scaleProperty.getValue();

        scaleProperty.setValue(100.0);

        CoorsMap verCoorsMap = new CoorsMap();

        for (int i = 0; i < graph.size(); i++){

            verCoorsMap.put(graph.getVertex(i).getTitle(), getCoors(graph.getVertex(i)));
        }
        scaleProperty.setValue(tmpValue);

        return verCoorsMap;
    }

    private Pair<Double,Double> getCoors(Company company){

        VisualVertex vv = visVerMap.get(company);
        return new Pair<>(vv.x(),vv.y());
    }

    public void setGraph(GenericGraph<Company, Link> graph, Map<String,Pair<Double,Double>> verCoorsMap, Color vertexColor, Color edgeColor, boolean isDraggable) {

        VisualVertex.setColor(vertexColor);
        VisualEdge.setColor(edgeColor);

        this.graph = graph;
        draggable = isDraggable;

        double tmpValue = scaleProperty.getValue();
        scaleProperty.setValue(100.0);
        loadSurface(verCoorsMap);
        scaleProperty.setValue(tmpValue);
    }

    private void loadSurface(Map<String,Pair<Double,Double>> verCoorsMap){

        for (int i = 0; i < graph.size(); i ++){

            FXMLLoader loader = new FXMLLoader(
                    VisualVertex.class.getResource("VisualVertex.fxml"));

            try {
                loader.load();

            } catch (IOException e){

                getAlert(MyAlertType.UNKNOWN, null);
                e.printStackTrace();
            }

            VisualVertex vertex = loader.getController();

            vertex.setTitle(graph.getVertex(i).getTitle());

            dragMap.put(vertex.getCircle(),vertex);
            visVerMap.put(graph.getVertex(i),vertex);
            scaleProperty.addListener(vertex.getScaleListener());

            vertex.setAllCoors(
                    surface.getLayoutX() + verCoorsMap.get(graph.getVertex(i).getTitle()).getKey() ,
                    surface.getLayoutY() + verCoorsMap.get(graph.getVertex(i).getTitle()).getValue());
        }

        Iterator<Link> iterator = graph.getEdgeIterator();

        while (iterator.hasNext()){

            Link link = iterator.next();

            VisualVertex one = visVerMap.get(link.one());
            VisualVertex another = visVerMap.get(link.another());

            VisualEdge vE = new VisualEdge(one, another, link.getPrice());
            priceShownProperty.addListener(vE.getPriceListener());
            scaleProperty.addListener(vE.getScaleListener());
            visEdgeMap.put(link,vE);
        }
    }

    private void addVertex(Company company){

        FXMLLoader loader = new FXMLLoader(VisualVertex.class.getResource("VisualVertex.fxml"));

        try { loader.load(); }

        catch (IOException e){

            getAlert(MyAlertType.UNKNOWN,null);
            e.printStackTrace();
        }

        VisualVertex vertex = loader.getController();
        vertex.setTitle(company.getTitle());
        makeNodeDraggable(vertex.getCircle());
        scaleProperty.addListener(vertex.getScaleListener());

        dragMap.put(vertex.getCircle(),vertex);
        visVerMap.put(company,vertex);

        vertex.setAllCoors(
                5.0 + (new Random()).nextInt(200),
                5.0 + (new Random()).nextInt(200)
        );

        project.setCoorsMap(getCoorsMap());
    }

    private void addEdge(Link link){

        VisualVertex one = visVerMap.get(link.one());
        VisualVertex another = visVerMap.get(link.another());
        VisualEdge vE =  new VisualEdge(one, another, link.getPrice());
        vE.getPrice().setVisible(priceShownProperty.getValue());
        priceShownProperty.addListener(vE.getPriceListener());
        scaleProperty.addListener(vE.getScaleListener());
        visEdgeMap.put(link, vE);
    }

    void removeVertex(Company company){

        scaleProperty.removeListener(visVerMap.get(company).getScaleListener());
        removeEdgesOf(company);
        surface.getChildren().remove(visVerMap.get(company).getItem());
        dragMap.remove(visVerMap.get(company).getCircle());
        visVerMap.remove(company);

        project.setCoorsMap(getCoorsMap());
    }

    void removeEdge(Company one, Company another) {

        VisualEdge edge = getEdge(graph.getEdge(one, another));
        if (edge == null)
            return;

        priceShownProperty.removeListener(edge.getPriceListener());
        scaleProperty.removeListener(edge.getScaleListener());
        surface.getChildren().remove(edge.getPrice());
        surface.getChildren().remove(edge.getLine());
        visEdgeMap.remove(graph.getEdge(one, another));
    }

    private void removeEdgesOf(Company company){

        for (int i = 0; i < graph.size(); i++)

            if (graph.getVertex(i) == company) {

                for (int j = 0; j < graph.size(); j++)

                    if (graph.getEdge(i, j) != null
                            && visEdgeMap.get(graph.getEdge(i, j)) != null)

                        removeEdge(graph.getVertex(i),graph.getVertex(j));
                return;
            }
    }

    private VisualEdge getEdge(Link link) {
        VisualEdge out = visEdgeMap.get(link);
        if (out == null)
            return visEdgeMap.get(link.getMating());
        else
            return out;
    }

    public void reloadView(){

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
                makeNodeDraggable(vertex.getCircle());
        }

        priceShownProperty.setValue(priceShownValue);
        scaleProperty.setValue(scaleValue);
    }

    private void makeNodeDraggable( final Node node) {

        node.setOnMousePressed(vertexPressedHandler);
        node.setOnMouseDragged(vertexDraggedHandler);
        node.setOnMouseReleased(vertexReleasedHandler);
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

        item.relocate(
                event.getScreenX() + dragContext.getKey()
                        + item.getBoundsInParent().getWidth()/2.0
                        - n.getRadius() > 0.0
                        ?
                        event.getScreenX() + dragContext.getKey() : item.getLayoutX(),

                event.getScreenY() + dragContext.getValue()
                        + item.getBoundsInParent().getHeight()/2.0
                        - n.getRadius() > 0.0
                        ?
                        event.getScreenY() + dragContext.getValue() : item.getLayoutY()
        );
    };

    private final EventHandler<MouseEvent> vertexReleasedHandler
            = event -> {
        surfaceChangedProperty.setValue(true);
        project.setCoorsMap(getCoorsMap());
    };
}