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

    private final DoubleProperty scale = new SimpleDoubleProperty(100.0);
    private final BooleanProperty priceShown =  new SimpleBooleanProperty(false);
    private final BooleanProperty surfaceSaved = new SimpleBooleanProperty(true);

    void setPricesVisible(boolean visibility){ priceShown.setValue(visibility); }
    DoubleProperty scaleProperty() { return scale; }
    BooleanProperty priceShownProperty() { return priceShown; }
    void bindToSurfaceChanged(BooleanProperty property){
        property.bindBidirectional(surfaceSaved);
    }

    CoorsMap getCoorsMap() { return coorsMap; }
    Graph<Company, Link> getGraph() { return graph; }

    void setView(GraphOnList<Company, Link> graph, CoorsMap coorsMap, boolean isDraggable) {
        this.graph = graph;
        this.coorsMap = coorsMap;
        draggable = isDraggable;
    }

    void cropView(){

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        for (VisualVertex vertex : visualVertexMap.values()) {
            minX = Math.min(minX, vertex.getPane().getLayoutX());
            minY = Math.min(minY, vertex.getPane().getLayoutY());
        }
        if (minX > 0 && minY > 0) shiftGraph(-minX,-minY);
        surfaceSaved.setValue(false);
    }

    private void shiftGraph(double xShift, double yShift){
        for (VisualVertex vertex : visualVertexMap.values()) vertex.moveXY(xShift, yShift);
    }

    private void loadVertex(Company company, double x, double y){

        Double scaleValue = scale.getValue();
        scale.setValue(100.0);

        Pair<Parent, VisualVertex> fxml = Loader.loadFXML("VisualVertex");
        surface.getChildren().add(fxml.getOne());
        VisualVertex vertex = fxml.getTwo();

        vertex.setTitle(company.titleProperty());

        coorsMap.put(company.hashCode(), new CoorsMap.Coors(x, y));
        vertex.xCenterProperty().addListener((o, ov, nv) -> coorsMap.get(company.hashCode()).x = nv.doubleValue());
        vertex.yCenterProperty().addListener((o, ov, nv) -> coorsMap.get(company.hashCode()).y = nv.doubleValue());
        vertex.bind(scale);
        visualVertexMap.put(company, vertex);
        if (draggable) makeNodeDraggable(vertex);
        vertex.setXY(x, y);
        scale.setValue(scaleValue);
    }

    public void add(Company company){
        double randomX = 5.0 + (new Random()).nextInt(1000);
        double randomY = 5.0 + (new Random()).nextInt(1000);
        loadVertex(company, randomX, randomY);
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
        visualVertexMap.get(company).unbind(scale);

        surface.getChildren().remove(visualVertexMap.get(company).getPane());

        visualVertexMap.remove(company);
        coorsMap.remove(company.hashCode());
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

    void updateView(){

        visualEdgeMap.clear();
        visualVertexMap.clear();
        surface.getChildren().clear();

        for (Iterator<Company> iterator = graph.getVertexIterator(); iterator.hasNext();) {
            Company company = iterator.next();
            loadVertex(company,
                    surface.getLayoutX() + coorsMap.get(company.hashCode()).x,
                    surface.getLayoutY() + coorsMap.get(company.hashCode()).y);
        }
        for (Iterator<Link> it = graph.getEdgeIterator(); it.hasNext();) add(it.next());
    }

    private void makeNodeDraggable(VisualVertex v) {

        v.getCircle().setOnMousePressed(event ->
                v.setDragContext(new Pair<>(event.getScreenX(), event.getScreenY())));

        v.getCircle().setOnMouseDragged(event -> {
            v.moveXY(
                    event.getScreenX() - v.getDragContext().getOne(),
                    event.getScreenY() - v.getDragContext().getTwo());

            v.setDragContext(new Pair<>(event.getScreenX(), event.getScreenY()));
        });

        v.getCircle().setOnMouseReleased(event -> surfaceSaved.setValue(false));
    }
}