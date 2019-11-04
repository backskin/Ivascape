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

    private final Map<Integer, VisualVertex> visVerHashMap = new HashMap<>();
    private final Map<Link, VisualEdge> visEdgeMap = new HashMap<>();
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
        for (VisualVertex vertex : visVerHashMap.values()) {
            minX = Math.min(minX, vertex.getPane().getLayoutX());
            minY = Math.min(minY, vertex.getPane().getLayoutY());
        }
        if (minX > 0 && minY > 0) shiftGraph(-minX,-minY);
        surfaceSaved.setValue(false);
    }

    private void shiftGraph(double xShift, double yShift){
        for (VisualVertex vertex : visVerHashMap.values()) vertex.moveXY(xShift, yShift);
    }

    private void loadVertex(StringProperty title, int hashcode, double x, double y){

        Double scaleValue = scale.getValue();
        scale.setValue(100.0);

        Pair<Parent, VisualVertex> fxml = Loader.loadFXML("VisualVertex");
        surface.getChildren().add(fxml.getOne());
        VisualVertex vertex = fxml.getTwo();

        vertex.setTitle(title);

        coorsMap.put(hashcode, new CoorsMap.Coors(x, y));
        vertex.xCenterProperty().addListener((o, ov, nv) -> coorsMap.get(hashcode).x = nv.doubleValue());
        vertex.yCenterProperty().addListener((o, ov, nv) -> coorsMap.get(hashcode).y = nv.doubleValue());
        vertex.bind(scale);
        visVerHashMap.put(hashcode, vertex);
        if (draggable) makeNodeDraggable(vertex);
        vertex.setXY(x, y);
        scale.setValue(scaleValue);
    }

    public void add(Company company){
        double randomX = 5.0 + (new Random()).nextInt(1000);
        double randomY = 5.0 + (new Random()).nextInt(1000);
        loadVertex(company.titleProperty(), company.hashCode(), randomX, randomY);
    }

    public void add(Link link){
        Double scaleValue = scale.getValue();
        scale.setValue(100.0);

        VisualVertex one = visVerHashMap.get(link.one().hashCode());
        VisualVertex two = visVerHashMap.get(link.two().hashCode());

        VisualEdge edge =  new VisualEdge(
                one.xCenterProperty(), one.yCenterProperty(),
                two.xCenterProperty(), two.yCenterProperty(), link.priceProperty());
        edge.getPriceLabel().visibleProperty().bind(priceShown);

        scale.addListener(edge.getScaleListener());
        visEdgeMap.put(link, edge);

        surface.getChildren().add(0, edge.getLine());
        surface.getChildren().add(edge.getPriceLabel());

        scale.setValue(scaleValue);
    }

    public void remove(Company company){

        for (Iterator<Link> it = graph.getEdgeIteratorForVertex(company); it.hasNext();)
            remove(it.next());
        visVerHashMap.get(company.hashCode()).unbind(scale);

        surface.getChildren().remove(visVerHashMap.get(company.hashCode()).getPane());

        visVerHashMap.remove(company.hashCode());
        coorsMap.remove(company.hashCode());
    }

    public void remove(Link link) {

        VisualEdge edge = getEdge(link);
        if (edge == null) return;

        visEdgeMap.remove(link);
        surface.getChildren().remove(edge.getPriceLabel());
        surface.getChildren().remove(edge.getLine());
        scale.removeListener(edge.getScaleListener());
    }

    private VisualEdge getEdge(Link link) {

        VisualEdge out = visEdgeMap.get(link);
        if (out == null) out = visEdgeMap.get(link.getMating());
        return out;
    }

    void updateView(){

        visEdgeMap.clear();
        visVerHashMap.clear();
        surface.getChildren().clear();

        for (Iterator<Company> iterator = graph.getVertexIterator(); iterator.hasNext();) {
            Company company = iterator.next();
            loadVertex(company.titleProperty(), company.hashCode(),
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