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

    private static Random r = new Random();

    private final Map<Company, VisualVertex> visualVertexMap = new HashMap<>();
    private final Map<Link, VisualEdge> visualEdgeMap = new HashMap<>();
    private CoorsMap coorsMap = new CoorsMap();
    private boolean draggable = false;
    private Graph<Company, Link> graph;

    @FXML
    private Pane surface;

    public void normalScale() {
        scaleBackup = scale.get();
        scale.set(100);
    }

    public void restoreScale(){
        scale.set(scaleBackup);
    }

    private DoubleProperty scale;
    private double scaleBackup;

    private final BooleanProperty priceShown =  new SimpleBooleanProperty(false);
    private BooleanProperty saved;

    void setPricesVisible(boolean visibility){ priceShown.setValue(visibility); }

    BooleanProperty priceShownProperty() { return priceShown; }
    void setSavedProperty(BooleanProperty property){
        saved = property;
    }

    Graph<Company, Link> getGraph() { return graph; }

    void setView(DoubleProperty scaleProp, GraphOnList<Company, Link> graph, CoorsMap coorsMap, boolean isDraggable) {
        this.graph = graph;
        scale = scaleProp;
        this.coorsMap = coorsMap;
        draggable = isDraggable;

        visualEdgeMap.clear();
        visualVertexMap.clear();
        surface.getChildren().clear();
        normalScale();
        for (Iterator<Company> iterator = graph.getVertexIterator(); iterator.hasNext();) add(iterator.next());
        for (Iterator<Link> it = graph.getEdgeIterator(); it.hasNext();) add(it.next());
        restoreScale();
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

    public void add(Company company){

        String ID = company.getID();
        Pair<Parent, VisualVertex> fxml = Loader.loadFXML("VisualVertex");
        surface.getChildren().add(fxml.getOne());
        VisualVertex vertex = fxml.getTwo();
        vertex.setTitle(company.titleProperty());

        CoorsMap.Coors coors = coorsMap.get(ID);
        if (coors == null) {
            coors = new CoorsMap.Coors(r.nextInt(1000), r.nextInt(500));
            coorsMap.put(ID, coors);
        }
        vertex.setXY(coors);
        visualVertexMap.put(company, vertex);
        scale.addListener(vertex.scaleListener);

        if (draggable) {

            vertex.getPane().layoutXProperty().addListener((o, ov, nv) -> coorsMap.get(ID).x = (Double) nv);
            vertex.getPane().layoutYProperty().addListener((o, ov, nv) -> coorsMap.get(ID).y = (Double) nv);
            makeNodeDraggable(vertex);
        }
    }

    public void add(Link link){
        VisualVertex one = visualVertexMap.get(link.one());
        VisualVertex two = visualVertexMap.get(link.two());

        VisualEdge edge =  new VisualEdge(
                one.xCenterProperty(), one.yCenterProperty(),
                two.xCenterProperty(), two.yCenterProperty(), link.priceProperty(), draggable);
        scale.addListener(edge.getScaleListener());

        surface.getChildren().add(0, edge.getLine());
        visualEdgeMap.put(link, edge);

        if (draggable) {

            edge.getPriceLabel().visibleProperty().bind(priceShown);
            surface.getChildren().add(1, edge.getPriceLabel());
        }
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