package ivascape.view.main;

import ivascape.controller.GraphWorker;
import ivascape.model.Company;
import ivascape.model.Graph;
import ivascape.model.Link;
import ivascape.model.Pair;
import ivascape.view.serve.VisualEdge;
import ivascape.view.serve.VisualVertex;
import javafx.beans.property.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import static ivascape.view.serve.MyAlerts.*;

public class GraphViewController {

    private Graph<Company,Link> graph;

    private final Map<Node,VisualVertex> dragMap = new HashMap<>();

    private final Map<Company,VisualVertex> visVerMap = new HashMap<>();

    private final Map<Link,VisualEdge> visEdgeMap = new HashMap<>();

    private boolean draggable = false;

    private final Pair<Double,Double> dragContext = new Pair<>();

    private final DoubleProperty scale = new SimpleDoubleProperty(100.0);

    private final BooleanProperty priceShown =  new SimpleBooleanProperty(false);

    private final BooleanProperty surfaceChanged = new SimpleBooleanProperty(false);


    public BooleanProperty surfaceChangedProperty() {
        return surfaceChanged;
    }

    public void setSurfaceChanged(boolean surfaceChanged) {
        this.surfaceChanged.set(surfaceChanged);
    }

    public void setScale(double scale){

        this.scale.setValue(scale);
    }

    public void cropIt(){

        Iterator verIter = new GraphWorker<>(graph).getVerIterator();
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        while (verIter.hasNext()){

            VisualVertex vv = visVerMap.get(verIter.next());
            minX = minX > vv.x() ? vv.x() : minX;
            minY = minY > vv.y() ? vv.y() : minY;

            if (minX == 0.0 && minY == 0.0)
                return;
        }

        verIter =  new GraphWorker<>(graph).getVerIterator();
        while (verIter.hasNext()){

            VisualVertex vv = visVerMap.get(verIter.next());
            vv.setAllCoors(vv.getItem().getLayoutX() - minX, vv.getItem().getLayoutY() - minY);
        }

        surfaceChanged.setValue(true);
    }

    @FXML
    private Pane surface;

    public Pane getSurface() {
        return surface;
    }

    public GraphViewController(){}

    @FXML
    private void initialize(){

        surface.setFocusTraversable(false);
    }

    public void setVertexRadius(double radius){

        VisualVertex.setCircleRadius(radius);
    }

    public void setPricesVisible(boolean answer){

        priceShown.setValue(answer);
    }

    public Map<String,Pair<Double,Double>> getCoorsMap(){

        double tmpValue = scale.getValue();

        scale.setValue(100.0);

        Map<String ,Pair<Double,Double>> verCoorsMap = new HashMap<>();

        for (int i = 0; i < graph.getVerSize(); i++){

            verCoorsMap.put(graph.getVer(i).getTitle(), getCoors(graph.getVer(i)));
        }
        scale.setValue(tmpValue);

        return verCoorsMap;
    }


    private Pair<Double,Double> getCoors(Company company){

        VisualVertex vv = visVerMap.get(company);

        return new Pair<>(vv.x(),vv.y());
    }


    public void setGraph(Graph<Company, Link> graph, Map<String,Pair<Double,Double>> verCoorsMap, Color vertexColor, Color edgeColor, boolean isDraggable) {

        VisualVertex.setColor(vertexColor);
        VisualEdge.setColor(edgeColor);

        this.graph = graph;
        draggable = isDraggable;

        double tmpValue = scale.getValue();
        scale.setValue(100.0);

        reloadVertices(verCoorsMap);

        scale.setValue(tmpValue);
    }


    private void reloadVertices(Map<String,Pair<Double,Double>> verCoorsMap){

        for (int i = 0; i < graph.getVerSize(); i ++){

            FXMLLoader loader = new FXMLLoader(
                    VisualVertex.class.getResource("VisualVertex.fxml"));

            try {
                loader.load();

            } catch (IOException e){

                getAlert(MyAlertType.UNKNOWN, null);
                e.printStackTrace();
            }

            VisualVertex vertex = loader.getController();

            vertex.setTitle(graph.getVer(i).getTitle());

            dragMap.put(vertex.getCircle(),vertex);

            visVerMap.put(graph.getVer(i),vertex);

            vertex.setScaleListener();

            scale.addListener(vertex.getScaleListener());

            vertex.setAllCoors(
                    verCoorsMap.get(graph.getVer(i).getTitle()).getOne() ,
                    verCoorsMap.get(graph.getVer(i).getTitle()).getTwo());
        }

        reloadEdges();
    }

    private void reloadEdges(){

        for (int i = 0; i < graph.getVerSize(); i ++){

            for (int j = i; j < graph.getVerSize(); j ++){

                if (graph.getEdge(i,j) != null){

                    VisualVertex II = visVerMap.get(graph.getVer(i));

                    VisualVertex JJ = visVerMap.get(graph.getVer(j));

                    VisualEdge vE =  new VisualEdge(II,JJ,graph.getEdge(i,j).getPrice());

                    vE.setListeners();

                    priceShown.addListener(vE.getListener(0));

                    scale.addListener(vE.getListener(1));

                    visEdgeMap.put(graph.getEdge(i, j), vE);
                }
            }
        }
    }

    public void addVertex(Company company){

        FXMLLoader loader = new FXMLLoader(VisualVertex.class.getResource("VisualVertex.fxml"));

        try { loader.load(); }

        catch (IOException e){

            getAlert(MyAlertType.UNKNOWN,null);
            e.printStackTrace();
        }

        VisualVertex vertex = loader.getController();

        vertex.setTitle(company.getTitle());

        vertex.setScaleListener();

        scale.addListener(vertex.getScaleListener());

        dragMap.put(vertex.getCircle(),vertex);

        makeNodeDraggable(vertex.getCircle());

        visVerMap.put(company,vertex);

        vertex.setAllCoors(
                5.0 + (new Random()).nextInt(400),
                5.0 + (new Random()).nextInt(400)
        );

        surface.getChildren().add(vertex.getItem());
    }

    public void delVertex(Company company){

        scale.removeListener(visVerMap.get(company).getScaleListener());

        delEdgesOfVertex(company);

        surface.getChildren().remove(visVerMap.get(company).getItem());

        dragMap.remove(visVerMap.get(company).getCircle());

        visVerMap.remove(company);
    }

    private void delEdgesOfVertex(Company company){

        for (int i = 0; i < graph.getVerSize(); i++)

            if (graph.getVer(i) == company) {

                for (int j = 0; j < graph.getVerSize(); j++)

                    if (graph.getEdge(i, j) != null
                            && visEdgeMap.get(graph.getEdge(i, j)) != null)

                        delEdge(graph.getEdge(i, j));
                return;
            }
    }

    public void addEdge(Link link){

        VisualVertex II = visVerMap.get(link.getOne());

        VisualVertex JJ = visVerMap.get(link.getTwo());

        VisualEdge vE =  new VisualEdge(II,JJ,link.getPrice());

        vE.setListeners();

        vE.getPrice().setVisible(priceShown.getValue());

        priceShown.addListener(vE.getListener(0));

        scale.addListener(vE.getListener(1));

        visEdgeMap.put(link, vE);
    }

    public void editEdge(Link link){

        if (visEdgeMap.get(link) != null)

            visEdgeMap.get(link).setPrice(link.getPrice());

        else visEdgeMap.get(link.getMating())
                .setPrice(link.getPrice());
    }

    public void delEdge(Link link){

        if (visEdgeMap.get(link) != null) {

            priceShown.removeListener(visEdgeMap.get(link).getListener(0));
            scale.removeListener(visEdgeMap.get(link).getListener(1));

            surface.getChildren().remove(visEdgeMap.get(link).getPrice());
            surface.getChildren().remove(visEdgeMap.get(link).getLine());
            visEdgeMap.remove(link);

        } else {

            priceShown.removeListener(
                    visEdgeMap.get(link.getMating()
                    ).getListener(0));

            scale.removeListener(
                    visEdgeMap.get(link.getMating()
            ).getListener(1));

            surface.getChildren().remove(visEdgeMap.get(link.getMating()).getPrice());
            surface.getChildren().remove(visEdgeMap.get(link.getMating()).getLine());

            visEdgeMap.remove(link.getMating());
        }
    }

    public void reloadView(){

        Double tmpscale = scale.getValue();
        scale.setValue(100.0);
        boolean tmpprvis = priceShown.getValue();

        surface.getChildren().clear();

        for (int i = 0; i < graph.getVerSize(); i++){

            for (int j = i; j < graph.getVerSize(); j++){

                if (graph.getEdge(i,j) != null)

                    if (visEdgeMap.get(graph.getEdge(i, j)) != null) {

                        surface.getChildren().add(visEdgeMap.get(graph.getEdge(i, j)).getLine());

                        surface.getChildren().add(visEdgeMap.get(graph.getEdge(i, j)).getPrice());

                    } else if(visEdgeMap.get(graph.getEdge(j, i)) != null) {

                        surface.getChildren().add(visEdgeMap.get(graph.getEdge(j, i)).getLine());

                        surface.getChildren().add(visEdgeMap.get(graph.getEdge(j, i)).getPrice());
                    }
            }
            if (draggable)
                makeNodeDraggable(visVerMap.get(graph.getVer(i)).getCircle());

            surface.getChildren().add(visVerMap.get(graph.getVer(i)).getItem());
        }

        priceShown.setValue(tmpprvis);
        scale.setValue(tmpscale);
    }

    private void makeNodeDraggable( final Node node) {

        node.setOnMousePressed(vertexPressedHandler);
        node.setOnMouseDragged(vertexDraggedHandler);
        node.setOnMouseReleased(vertexReleasedHandler);
    }

    private final EventHandler<MouseEvent> vertexPressedHandler = event -> {

        Node node = dragMap.get(event.getSource()).getItem();

        dragContext.setOne(node.getBoundsInParent().getMinX() - event.getScreenX());
        dragContext.setTwo(node.getBoundsInParent().getMinY() - event.getScreenY());
    };

    private final EventHandler<MouseEvent> vertexDraggedHandler = event -> {

        Node node = dragMap.get(event.getSource()).getItem();

        node.relocate(
                event.getScreenX() + dragContext.getOne() + node.getBoundsInParent().getWidth()/2.0
                        - dragMap.get(event.getSource()).getCircle().getRadius() > 0.0
                        ?
                        event.getScreenX() + dragContext.getOne()
                        : node.getLayoutX(),

                event.getScreenY() + dragContext.getTwo() + node.getBoundsInParent().getHeight()/2.0
                        - dragMap.get(event.getSource()).getCircle().getRadius() > 0.0
                        ?
                        event.getScreenY() + dragContext.getTwo()
                        : node.getLayoutY()
        );
    };

    private final EventHandler<MouseEvent> vertexReleasedHandler
            = event -> surfaceChanged.setValue(true);
}