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

public class MapViewController implements ViewController {

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

    void updateGraphView(){
        GVController.updateView();
    }

    @Override
    public void updateView(){

        zoomSlider.setValue(100);

        GVController.setGraph(
                project.getGraph(),
                project.getCoorsMap(),
                VisualVertex.defaultColor,
                VisualEdge.defaultColor,
                true);

        GVController.updateView();
    }

    void bindToSurfaceChanged(BooleanProperty property){
        GVController.bindToSurfaceChanged(property);
    }

    @FXML
    private void handleCrop(){ GVController.cropIt(); }
    @FXML
    private void handleResetZoom(){ zoomSlider.setValue(100); }
    @FXML
    private void initialize(){

        Pair<Parent, GraphViewController> fxml = Loader.loadFXML("GraphView");
        surfaceScrollPane.setContent(fxml.getOne());
        GVController = fxml.getTwo();

        togglePricesVisible.selectedProperty().addListener(
                (o, b0, value) -> GVController.setPricesVisible(value));

        zoomSlider.valueProperty().addListener(
                (o, b0, value) -> GVController.setScale(value.doubleValue()));

        project.companiesAmountProperty().addListener(c -> {
            zoomSlider.setDisable(project.isEmpty());
            togglePricesVisible.setDisable(project.isEmpty());
            resetZoomButton.setDisable(project.isEmpty());
            cropViewButton.setDisable(project.isEmpty());

            if (!project.isEmpty()) zoomSlider.setValue(100);
        });
    }
}