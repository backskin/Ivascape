package backsoft.ivascape.viewcontrol;

import backsoft.ivascape.handler.Loader;
import backsoft.ivascape.handler.Preferences;
import backsoft.ivascape.logic.Pair;
import backsoft.ivascape.model.Company;
import backsoft.ivascape.model.IvascapeGraph;
import backsoft.ivascape.model.Project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Iterator;
import java.util.List;

public class AnalyseWindow {

    private Stage analyseStage;
    private final Project project = Project.get();
    private final Preferences prefs = Preferences.get();
    @FXML
    private HBox componentTables;
    @FXML
    private Label cAmount;

    public AnalyseWindow(){}

    void setStage(Stage analyseStage) {
        this.analyseStage = analyseStage;

        analyseStage.setOnCloseRequest(event -> {
            VisualVertex.setColor(VisualVertex.defaultColor);
            VisualEdge.setColor(VisualEdge.defaultColor);
        });
    }

    @FXML
    private void initialize(){

        List<IvascapeGraph> components =  project.getComponents();

        if (components.size() == 0) {
            Label label = new Label(prefs.getValueFromBundle("editwindows.emptygraph"));

            label.setFont(new Font("System",15));
            componentTables.getChildren().add(label);
        } else
        for (IvascapeGraph component: components
             ) {

            VBox form = new VBox();
            form.setFocusTraversable(false);
            form.setAlignment(Pos.CENTER);

            Pair<Parent, GraphViewController> fxml = Loader.loadFXML("GraphView");

            Pane surface = (Pane) fxml.getOne();
            surface.setMouseTransparent(true);
            GraphViewController controller = fxml.getTwo();

            controller.setGraph(
                    component,
                    project.getCoorsMap(),
                    Color.DARKCYAN,
                    Color.DARKCYAN,
                    false);

            ScrollPane scrollPane = new ScrollPane();
            form.getChildren().add(scrollPane);
            scrollPane.setContent(surface);
            VBox.setVgrow(scrollPane,Priority.ALWAYS);
            scrollPane.setPrefHeight(125);

            controller.updateView();

            controller.cropIt();
            controller.setScale(50);

            TableView<Company> componentTable = new TableView<>();
            form.getChildren().add(componentTable);
            form.setFocusTraversable(false);
            VBox.setVgrow(componentTable, Priority.ALWAYS);
            componentTables.getChildren().add(form);
            componentTable.setPrefHeight(120);
            HBox.setHgrow(form, Priority.ALWAYS);
            componentTable.setSelectionModel(null);
            HBox.setHgrow(componentTable, Priority.ALWAYS);

            ObservableList<Company> list = FXCollections.observableArrayList();

            Iterator<Company> iterator = component.getVertexIterator();
            while (iterator.hasNext()) list.add(iterator.next());

            componentTable.setItems(list);
            componentTable.setFocusTraversable(false);
            TableColumn<Company,String> column = new TableColumn<>();
            column.setSortable(false);
            column.setText((components.indexOf(component)+1)
                    + prefs.getValueFromBundle("editwindows.component"));

            componentTable.getColumns().add(column);
            componentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            column.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        }

        cAmount.setText(Integer.toString(components.size()));
    }

    @FXML
    private void handleClose(){

        VisualVertex.setColor(VisualVertex.defaultColor);
        VisualEdge.setColor(VisualEdge.defaultColor);
        analyseStage.close();
    }
}
