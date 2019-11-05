package backsoft.ivascape.viewcontrol;

import backsoft.ivascape.handler.Loader;
import backsoft.ivascape.logic.Pair;
import backsoft.ivascape.model.Project;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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

    private BooleanProperty emptiness = new SimpleBooleanProperty(true);

    GraphViewController getGVController(){
        return GVController;
    }

    void updateView(){

        VisualVertex.setColor(VisualVertex.defaultColor);
        VisualEdge.setColor(VisualEdge.defaultColor);
        GVController.flush();
        GVController.setView(zoomSlider.valueProperty(),
                project.getGraph(), project.getCoorsMap(), true);
    }

    void setSavedProperty(BooleanProperty property){
        GVController.setSavedProperty(property);
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

        emptiness.addListener(((o, ov, t1) -> {
            zoomSlider.setDisable(t1);
            togglePricesVisible.setDisable(t1);
            resetZoomButton.setDisable(t1);
            cropViewButton.setDisable(t1);
        }));

        emptiness.bind(project.companiesAmountProperty().lessThan(1));


        updateView();
    }
}