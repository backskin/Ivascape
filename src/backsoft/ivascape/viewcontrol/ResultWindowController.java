package backsoft.ivascape.viewcontrol;

import backsoft.ivascape.handler.FileHandler;
import backsoft.ivascape.handler.Loader;
import backsoft.ivascape.logic.CoorsMap;
import backsoft.ivascape.logic.Pair;
import backsoft.ivascape.model.IvascapeGraph;
import backsoft.ivascape.model.Link;
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

import java.util.Iterator;

public class ResultWindowController {

    private Stage stage;

    private Double totalPrice = 0.0;
    private CoorsMap coorsMap;
    private Project project = Project.get();
    private IvascapeGraph primGraph;
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

    public ResultWindowController(){}

    @FXML
    private void initialize(){

        VisualVertex.setColor(Color.DODGERBLUE);
        VisualEdge.setColor(Color.DODGERBLUE);

        Pair<Parent, GraphViewController> resultFxml = Loader.loadFXML("GraphView");
        pane.setContent(resultFxml.getOne());
        GraphViewController gvController = resultFxml.getTwo();
        coorsMap = project.copyCoorsMap();
        primGraph = project.applyPrimAlgorithm();
        gvController.setView(zoomSlider.valueProperty(), primGraph,
                coorsMap,true);
        gvController.cropView();
        gvController.priceShownProperty().bind(showHidePrices.selectedProperty());

        for (Iterator<Link> it = gvController.getGraph().getEdgeIterator(); it.hasNext();){
            Link link = it.next();
            Pair<Parent, ResultTableElementController> fxml = Loader.loadFXML("ResultTableElement");
            resTable.getChildren().add(fxml.getOne());
            fxml.getTwo().setLink(link);
            totalPrice += link.getPrice();
        }
        totalPriceLabel.setText(String.format("%.2f", totalPrice));

        VisualVertex.setColor(VisualVertex.defaultColor);
        VisualEdge.setColor(VisualEdge.defaultColor);
    }

    @FXML
    private void handleSaveAs(){

        double sliderValue = zoomSlider.getValue();
        zoomSlider.setValue(100);
        FileHandler.dialogSaveAs(null, primGraph, coorsMap);
        zoomSlider.setValue(sliderValue);
    }

    @FXML
    private void handleExport(){

        FileHandler.dialogExport(primGraph, stage);
    }

    @FXML
    private void handleClose(){
        stage.close();
    }
}