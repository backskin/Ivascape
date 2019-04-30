package ivascape.view.main;

import ivascape.MainApp;
import ivascape.controller.IvascapeProject;
import ivascape.view.serve.VisualEdge;
import ivascape.view.serve.VisualVertex;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;

import java.io.IOException;

import static ivascape.view.serve.MyAlerts.*;

public class MapViewController {

    private GraphViewController GVController;


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

    public GraphViewController getGVController() {
        return GVController;
    }

    public void reloadView(){

        if (!IvascapeProject.isSaved()) saveGV();
        zoomSlider.setValue(100);
        GVController.setGraph(
                IvascapeProject.getProject(),
                IvascapeProject.getVerCoorsMap(),
                VisualVertex.defaultColor,
                VisualEdge.defaultColor,
                true);

        GVController.reloadView();
    }

    public void saveGV(){

        if (!IvascapeProject.isEmpty() && GVController.getSurface().getChildren().size() > 0)
            IvascapeProject.setVerCoorsMap(GVController.getCoorsMap());
    }

    public MapViewController(){}

    @FXML
    private void initialize(){

        try {

            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("view/main/GraphView.fxml"),
                    MainApp.bundle);

            surface.setContent(loader.load());

            GVController = loader.getController();

            IvascapeProject.savedProperty().addListener((observable, oldValue, newValue) ->  { if (newValue) GVController.setSurfaceChanged(false);});

            GVController.setVertexRadius(VisualVertex.defaultCircleRadius);

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

            getAlert(MyAlertType.UNKNOWN, null);
            e.printStackTrace();
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