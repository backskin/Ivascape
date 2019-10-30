package backsoft.ivascape.viewcontrol;

import backsoft.ivascape.handler.Loader;
import backsoft.ivascape.logic.Pair;
import backsoft.ivascape.model.Project;
import javafx.scene.Parent;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;

import java.io.IOException;

import static backsoft.ivascape.viewcontrol.MyAlerts.*;

public class MapViewController {

    private GraphViewController GVController;
    private Project project = Project.get();
    
    @FXML
    private Button cropView;

    @FXML
    private Button ResetZoom;

    @FXML
    private ToggleButton showHidePrices;

    @FXML
    private Slider zoomSlider;

    @FXML
    private ScrollPane surface;

    GraphViewController getGVController() {
        return GVController;
    }

    void updateView(){

        zoomSlider.setValue(100);
        GVController.setGraph(
                project.getGraph(),
                project.getCoorsMap(),
                VisualVertex.defaultColor,
                VisualEdge.defaultColor,
                true);

        GVController.updateView();
    }

    public MapViewController(){}

    @FXML
    private void initialize(){

        try {

            Pair<Parent, GraphViewController> fxml = Loader.loadFXML("GraphView");

            surface.setContent(fxml.getOne());

            GVController = fxml.getTwo();

            project.savedProperty().addListener(
                    (obs, ol, val) -> {
                        if (val) GVController.getSurfaceChangedProperty().setValue(false);
                    });

            showHidePrices.selectedProperty().addListener((observable, oldValue, newValue) -> GVController.setPricesVisible(newValue));

            zoomSlider.valueProperty().addListener((observable, oldValue, newValue)

                    -> GVController.setScale(newValue.doubleValue()));

            GVController.getSurface().getChildren().addListener((ListChangeListener<Node>) c -> {

                if (GVController.getSurface().getChildren().size() < 1) {

                    zoomSlider.setValue(100);
                    zoomSlider.setDisable(true);
                    showHidePrices.setSelected(false);
                    showHidePrices.setDisable(true);
                    ResetZoom.setDisable(true);
                    cropView.setDisable(true);

                } else {
                    zoomSlider.setDisable(false);
                    showHidePrices.setDisable(false);
                    ResetZoom.setDisable(false);
                    cropView.setDisable(false);
                }
            });

        } catch (IOException e){

            getAlert(AlertType.UNKNOWN, Loader.getMainStage(), e.getMessage());
        }
    }

    @FXML
    private void cropIt(){

        GVController.cropIt();
    }

    @FXML
    private void handleReset(){

        zoomSlider.setValue(100);
    }
}