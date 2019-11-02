package backsoft.ivascape.viewcontrol;

import backsoft.ivascape.handler.FileHandler;
import backsoft.ivascape.handler.Loader;
import backsoft.ivascape.logic.Pair;
import backsoft.ivascape.logic.CoorsMap;
import backsoft.ivascape.model.IvascapeGraph;
import backsoft.ivascape.model.Project;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ResultWindowController {

    private Stage stage;

    private final IvascapeGraph graph;
    private final CoorsMap map;

    private Double totalPrice = 0.0;

    @FXML
    private Slider zoomSlider;

    @FXML
    private ToggleButton showHidePrices;

    @FXML
    private Label totalPriceLabel;

    @FXML
    private ScrollPane pane;

    @FXML
    private VBox resTable;

    void setStage(Stage resStage) {

        stage = resStage;
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(Loader.getMainStage());

        resStage.setOnCloseRequest(event -> {
            VisualVertex.setColor(VisualVertex.defaultColor);
            VisualEdge.setColor(VisualEdge.defaultColor);
        });
    }

    public ResultWindowController(){

        graph = Project.get().applyPrimAlgorithm();
        map = Project.get().getCoorsMap();
    }

    @FXML
    private void initialize(){

        Pair<Parent, GraphViewController> resultFxml = Loader.loadFXML("GraphView");

        pane.setContent(resultFxml.getOne());

        GraphViewController gvController = resultFxml.getTwo();

        gvController.setView(
                graph, map,
                Color.CORNFLOWERBLUE,
                Color.CORNFLOWERBLUE,true);

        zoomSlider.setValue(100.0);

        zoomSlider.valueProperty().addListener((observable, oldValue, newValue) ->

                gvController.setScale(newValue.doubleValue())
        );

        showHidePrices.selectedProperty().addListener((observable, oldValue, newValue) -> gvController.setPricesVisible(newValue));

        gvController.updateView();

        for (int i = 0; i < graph.size(); i ++){

            for (int j = i; j < graph.size(); j++){

                if (graph.getEdge(i,j) != null){

                    Pair<Parent, ResultTableElementController> fxml = Loader.loadFXML("ResultTableElement");

                    resTable.getChildren().add(fxml.getOne());
                    ResultTableElementController controller = fxml.getTwo();

                    controller.setLink(graph.getEdge(i,j));

                    totalPrice += graph.getEdge(i,j).getPrice();
                }
            }
        }

        totalPriceLabel.setText(String.format("%.2f", (Math.round(totalPrice*100)/100.0)));

    }

    @FXML
    private void handleSaveAs(){

        double sliderValue = zoomSlider.getValue();
        zoomSlider.setValue(100);
        FileHandler.dialogSaveAs(null, graph, map);
        zoomSlider.setValue(sliderValue);
    }

    @FXML
    private void handleExport(){

        FileHandler.dialogExport(graph, stage);

    }

    @FXML
    private void handleClose(){

        VisualVertex.setColor(VisualVertex.defaultColor);
        VisualEdge.setColor(VisualEdge.defaultColor);
        stage.close();
    }
}