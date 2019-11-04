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
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

import java.util.*;

public class GraphViewController {

    private final Map<Company, VisualVertex> visualVertexMap = new HashMap<>();
    private final Map<Link, VisualEdge> visualEdgeMap = new HashMap<>();
    private CoorsMap coorsMap = new CoorsMap();
    private boolean draggable = false;
    private Graph<Company, Link> graph;

    @FXML
    private Pane surface;
    private DoubleProperty scale;
    private final BooleanProperty priceShown =  new SimpleBooleanProperty(false);
    private BooleanProperty saved;

    void setPricesVisible(boolean visibility){ priceShown.setValue(visibility); }

    BooleanProperty priceShownProperty() { return priceShown; }
    void setSavedProperty(BooleanProperty property){
        saved = property;
    }

    CoorsMap getCoorsMap() { return coorsMap; }
    Graph<Company, Link> getGraph() { return graph; }

    void setView(DoubleProperty scaleProp, GraphOnList<Company, Link> graph, CoorsMap coorsMap, boolean isDraggable) {
        this.graph = graph;
        scale = scaleProp;
        this.coorsMap = coorsMap;
        draggable = isDraggable;

        visualEdgeMap.clear();
        visualVertexMap.clear();
        surface.getChildren().clear();

        for (Iterator<Company> iterator = graph.getVertexIterator(); iterator.hasNext();) add(iterator.next());
        for (Iterator<Link> it = graph.getEdgeIterator(); it.hasNext();) add(it.next());
    }

    void cropView(){

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        for (VisualVertex vertex : visualVertexMap.values()) {
            minX = Math.min(minX, vertex.getPane().getLayoutX());
            minY = Math.min(minY, vertex.getPane().getLayoutY());
        }
        if (minX > 0 && minY > 0) shiftGraph(-minX,-minY);
        if (saved != null) saved.setValue(false);
    }

    private void shiftGraph(double xShift, double yShift){
        for (VisualVertex vertex : visualVertexMap.values()) vertex.moveXY(xShift, yShift);
    }

    public void add(Company company){

        double scaleValue = scale.getValue();
        scale.setValue(100.0);

        Pair<Parent, VisualVertex> fxml = Loader.loadFXML("VisualVertex");
        surface.getChildren().add(fxml.getOne());
        VisualVertex vertex = fxml.getTwo();

        vertex.setTitle(company.titleProperty());
        if (coorsMap.get(company.getID()) == null) coorsMap.put(company.getID(),
                new CoorsMap.Coors(
                        surface.getLayoutX() + (new Random()).nextInt(1000),
                        surface.getLayoutY() + (new Random()).nextInt(500)));

        vertex.xCenterProperty().addListener((o, ov, nv) -> coorsMap.get(company.getID()).x = nv.doubleValue());
        vertex.yCenterProperty().addListener((o, ov, nv) -> coorsMap.get(company.getID()).y = nv.doubleValue());
        scale.addListener(vertex.scaleListener);
        visualVertexMap.put(company, vertex);
        if (draggable) makeNodeDraggable(vertex);
        vertex.setXY(coorsMap.get(company.getID()));

        scale.setValue(scaleValue);
    }

    void flush(){
        for (VisualVertex vertex : visualVertexMap.values()){
            scale.removeListener(vertex.scaleListener);
            surface.getChildren().remove(vertex.getPane());
        }
        for (VisualEdge edge : visualEdgeMap.values()){
            edge.getPriceLabel().textProperty().unbind();
            scale.removeListener(edge.getScaleListener());
            surface.getChildren().remove(edge.getPriceLabel());
            surface.getChildren().remove(edge.getLine());
        }
        coorsMap.clear();
        visualVertexMap.clear();
        visualEdgeMap.clear();
    }

    public void add(Link link){
        Double scaleValue = scale.getValue();
        scale.setValue(100.0);

        VisualVertex one = visualVertexMap.get(link.one());
        VisualVertex two = visualVertexMap.get(link.two());

        VisualEdge edge =  new VisualEdge(
                one.xCenterProperty(), one.yCenterProperty(),
                two.xCenterProperty(), two.yCenterProperty(), link.priceProperty());
        edge.getPriceLabel().visibleProperty().bind(priceShown);

        scale.addListener(edge.getScaleListener());
        visualEdgeMap.put(link, edge);

        surface.getChildren().add(0, edge.getLine());
        surface.getChildren().add(edge.getPriceLabel());

        scale.setValue(scaleValue);
    }

    public void remove(Company company){

        for (Iterator<Link> it = graph.getEdgeIteratorForVertex(company); it.hasNext();)
            remove(it.next());

        scale.removeListener(visualVertexMap.get(company).scaleListener);
        surface.getChildren().remove(visualVertexMap.get(company).getPane());

        visualVertexMap.remove(company);
        coorsMap.remove(company.getID());
    }

    public void remove(Link link) {

        VisualEdge edge = getEdge(link);
        if (edge == null) return;

        visualEdgeMap.remove(link);
        surface.getChildren().remove(edge.getPriceLabel());
        surface.getChildren().remove(edge.getLine());
        scale.removeListener(edge.getScaleListener());
    }

    private VisualEdge getEdge(Link link) {

        VisualEdge out = visualEdgeMap.get(link);
        if (out == null) out = visualEdgeMap.get(link.getMating());
        return out;
    }

    private void makeNodeDraggable(VisualVertex v) {

        v.getCircle().setOnMousePressed(event ->
                v.setDragContext(event.getScreenX(), event.getScreenY()));

        v.getCircle().setOnMouseDragged(event -> {
            v.moveXY(
                    event.getScreenX() - v.getDragContext().getOne(),
                    event.getScreenY() - v.getDragContext().getTwo());

            v.setDragContext(event.getScreenX(), event.getScreenY());
        });

        v.getCircle().setOnMouseReleased(event -> {if (saved != null) saved.setValue(false);});
    }
}