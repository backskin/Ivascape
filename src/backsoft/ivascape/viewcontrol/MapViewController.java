package backsoft.ivascape.viewcontrol;

import backsoft.ivascape.handler.Loader;
import backsoft.ivascape.logic.Pair;
import backsoft.ivascape.model.Project;

import javafx.beans.property.BooleanProperty;
import javafx.scene.Parent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;

public class MapViewController {

    private GraphViewController GVController;
    private final Project project = Project.get();
    
    @FXML
    private Button cropViewButton;
    @FXML
    private Button resetZoomButton;
    @FXML
    private ToggleButton togglePricesVisible;
    @FXML
    private Slider zoomSlider;
    @FXML
    private ScrollPane surfaceScrollPane;

    GraphViewController getGVController(){
        return GVController;
    }

    void updateView(){

        zoomSlider.setValue(100);
        VisualVertex.setColor(VisualVertex.defaultColor);
        VisualEdge.setColor(VisualEdge.defaultColor);
        GVController.setView(
                project.getGraph(),
                project.getCoorsMap(),
                true);

        GVController.updateView();
    }

    void bindToSurfaceChanged(BooleanProperty property){
        GVController.bindToSurfaceChanged(property);
    }

    @FXML
    private void handleCrop(){ GVController.cropView(); }
    @FXML
    private void handleResetZoom(){ zoomSlider.setValue(100); }
    @FXML
    private void initialize(){

        Pair<Parent, GraphViewController> fxml = Loader.loadFXML("GraphView");
        surfaceScrollPane.setContent(fxml.getOne());
        GVController = fxml.getTwo();

        togglePricesVisible.selectedProperty().addListener((o, b0, value) ->
                GVController.setPricesVisible(value));

        GVController.scaleProperty().bindBidirectional(zoomSlider.valueProperty());

        project.companiesAmountProperty().addListener(c -> {
            zoomSlider.setDisable(project.isEmpty());
            togglePricesVisible.setDisable(project.isEmpty());
            resetZoomButton.setDisable(project.isEmpty());
            cropViewButton.setDisable(project.isEmpty());
        });

        updateView();
    }
}