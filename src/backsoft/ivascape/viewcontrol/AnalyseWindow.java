package backsoft.ivascape.viewcontrol;

import backsoft.ivascape.handler.Loader;
import backsoft.ivascape.handler.Preferences;
import backsoft.ivascape.logic.Pair;
import backsoft.ivascape.model.IvascapeGraph;
import backsoft.ivascape.model.Project;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AnalyseWindow {

    private Stage analyseStage;
    private final Project project = Project.get();
    private final Preferences prefs = Preferences.get();
    @FXML
    private HBox componentTables;
    @FXML
    private Label cAmount;

    public AnalyseWindow(){}

    void setStage(Stage stage) {
        this.analyseStage = stage;
        analyseStage.setTitle(prefs.getStringFromBundle("editwindows.analysetitle"));
        analyseStage.initModality(Modality.WINDOW_MODAL);
        analyseStage.initOwner(Loader.getMainStage());
        analyseStage.setOnCloseRequest(event -> {
            VisualVertex.setColor(VisualVertex.defaultColor);
            VisualEdge.setColor(VisualEdge.defaultColor);
        });
        Platform.runLater(() -> {
            analyseStage.setMinHeight(analyseStage.getHeight());
            analyseStage.setMinWidth(analyseStage.getWidth());
        });
    }

    @FXML
    private void initialize(){

        int i = 0;
        VisualVertex.setColor(Color.DARKCYAN);
        VisualEdge.setColor(Color.DARKCYAN);

        if (project.isEmpty()) {
            Label label = new Label(prefs.getStringFromBundle("editwindows.emptygraph"));
            label.setFont(new Font(15));
            componentTables.getChildren().add(label);

        } else {
            DoubleProperty scale = new SimpleDoubleProperty(100);
            for (IvascapeGraph component: project.getComponents()) {
                Pair<Parent, ElementOfAnalyseWindowController> fxml = Loader.loadFXML("ElementOfAnalyseWindow");
                fxml.getTwo().setComponent(++i, component, scale);
                scale.setValue(75);
                HBox.setHgrow(fxml.getOne(), Priority.ALWAYS);
                componentTables.getChildren().add(fxml.getOne());
            }
        }
        VisualVertex.setColor(VisualVertex.defaultColor);
        VisualEdge.setColor(VisualEdge.defaultColor);
        cAmount.setText(String.valueOf(i));
    }

    @FXML
    private void handleClose(){

        analyseStage.close();
    }
}
