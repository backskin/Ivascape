package backsoft.ivascape.viewcontrol;

import backsoft.ivascape.handler.Loader;
import backsoft.ivascape.handler.Preferences;
import backsoft.ivascape.logic.Pair;
import backsoft.ivascape.model.Company;
import backsoft.ivascape.model.IvascapeGraph;
import backsoft.ivascape.model.Project;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Iterator;

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

        } else for (IvascapeGraph component: project.getComponents()) {

            VBox form = new VBox();
            form.setFocusTraversable(false);
            form.setAlignment(Pos.CENTER);

            Pair<Parent, GraphViewController> fxml = Loader.loadFXML("GraphView");
            Pane surface = (Pane) fxml.getOne();

            surface.setMouseTransparent(true);
            GraphViewController controller = fxml.getTwo();

            DoubleProperty scale = new SimpleDoubleProperty(50);
            controller.setView(scale, component, project.getCoorsMap(), false);
            controller.cropView();

            ScrollPane scrollPane = new ScrollPane();
            StackPane stackPane = new StackPane(surface);
            stackPane.setAlignment(Pos.CENTER);
            form.getChildren().add(stackPane);
            scrollPane.setContent(surface);
            VBox.setVgrow(scrollPane,Priority.ALWAYS);
            scrollPane.setPrefHeight(125);

            TableView<Company> componentTable = new TableView<>();
            componentTable.setFocusTraversable(false);
            form.getChildren().add(componentTable);
            form.setFocusTraversable(false);
            VBox.setVgrow(componentTable, Priority.ALWAYS);
            componentTables.getChildren().add(form);
            componentTable.setPrefHeight(120);
            HBox.setHgrow(form, Priority.ALWAYS);
            componentTable.setSelectionModel(null);
            HBox.setHgrow(componentTable, Priority.ALWAYS);

            ObservableList<Company> list = FXCollections.observableList(new ArrayList<>(){{
                for (Iterator<Company> iterator = component.getVertexIterator(); iterator.hasNext();)
                    add(iterator.next());
            }});

            componentTable.setItems(list);
            TableColumn<Company,String> column = new TableColumn<>();
            column.setSortable(false);
            column.setText(++i + prefs.getStringFromBundle("editwindows.component"));

            componentTable.getColumns().add(column);
            componentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            column.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        }

        cAmount.setText(String.valueOf(i));
    }

    @FXML
    private void handleClose(){

        analyseStage.close();
    }
}
