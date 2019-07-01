package ivascape.view.serve;

import ivascape.MainApp;
import ivascape.controller.FileHandler;
import ivascape.controller.Project;
import ivascape.model.*;
import ivascape.view.main.GraphViewController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;

import static ivascape.view.serve.MyAlerts.getAlert;

public class ResultWindowController {

    private Stage resultStage;

    private GraphViewController GVController;

    private final IvaGraph graph;
    private final Map map;

    private Double totalPrice = 0.0;

    @FXML
    private Slider zoomSlider;

    @FXML
    private ToggleButton showHidePrices;

    @FXML
    private Label totalPriceLabel;

    @FXML
    private ScrollPane visualResult;

    @FXML
    private VBox tabledResult;

    public void setResultStage(Stage resultStage) {

        this.resultStage = resultStage;
        resultStage.setOnCloseRequest(event -> {
            VisualVertex.setColor(VisualVertex.defaultColor);
            VisualEdge.setColor(VisualEdge.defaultColor);
        });
    }

    public ResultWindowController(){

        graph = Project.getInstance().algorithmResult();
        map = Project.getInstance().getCoorsMap();
    }

    @FXML
    private void initialize(){

        try {

            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("view/main/GraphView.fxml"),
                    MainApp.bundle);

            visualResult.setContent(loader.load());

            GVController = loader.getController();

            GVController.setGraph(
                    graph, map,
                    Color.CORNFLOWERBLUE,
                    Color.CORNFLOWERBLUE,true);

            zoomSlider.setValue(100.0);

            zoomSlider.valueProperty().addListener((observable, oldValue, newValue) ->

                    GVController.setScale(newValue.doubleValue())
            );

            showHidePrices.selectedProperty().addListener((observable, oldValue, newValue) -> GVController.setPricesVisible(newValue));

            GVController.reloadView();

            for (int i = 0; i < graph.size(); i ++){

                for (int j = i; j < graph.size(); j++){

                    if (graph.getEdge(i,j) != null){

                        FXMLLoader anotherLoader = new FXMLLoader(
                                MainApp.class.getResource("view/serve/ResultTableElement.fxml"),
                                MainApp.bundle);

                        tabledResult.getChildren().add(anotherLoader.load());

                        ResultTableElementController controller = anotherLoader.getController();

                        controller.setLink(graph.getEdge(i,j));

                        totalPrice += graph.getEdge(i,j).getPrice();
                    }
                }
            }
            totalPrice = Math.round(totalPrice*100)/100.0;

            totalPriceLabel.setText(totalPrice.toString());

        } catch (IOException e){

            getAlert(MyAlerts.MyAlertType.UNKNOWN,resultStage);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSaveAs(){

        double tmpscale = zoomSlider.getValue();
        zoomSlider.setValue(100);
        FileHandler.saveProject(resultStage,null);
        zoomSlider.setValue(tmpscale);
    }

    @FXML
    private void handleExport(){

        FileHandler.exportToXLS(graph, resultStage);

    }

    @FXML
    private void handleClose(){

        VisualVertex.setColor(VisualVertex.defaultColor);
        VisualEdge.setColor(VisualEdge.defaultColor);
        resultStage.close();
    }
}