package ivascape.view.serve;

import ivascape.MainApp;
import ivascape.models.Company;
import ivascape.models.Link;
import ivascape.logic.*;
import ivascape.models.Project;
import ivascape.view.main.GraphViewController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
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

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class AnalyseWindowController {

    private Stage analyseStage;
    private Project project = Project.getInstance();

    public void setAnalyseStage(Stage analyseStage) {
        this.analyseStage = analyseStage;

        analyseStage.setOnCloseRequest(event -> {
            VisualVertex.setColor(VisualVertex.defaultColor);
            VisualEdge.setColor(VisualEdge.defaultColor);
        });
    }

    @FXML
    private HBox componentTables;

    @FXML
    private Label cAmount;

    public AnalyseWindowController(){}

    @FXML
    private void initialize(){

        List<GenericGraph<Company, Link>> components =  project.getComponents();

        if (components.size() == 0) {
            Label label = new Label(MainApp.bundle.getString("editwindows.emptygraph"));
            label.setFont(new Font("System",15));
            componentTables.getChildren().add(label);
        } else
        for (GenericGraph<Company, Link> component: components
             ) {

            VBox form = new VBox();
            form.setFocusTraversable(false);
            form.setAlignment(Pos.CENTER);

            try {

                FXMLLoader loader = new FXMLLoader(
                        MainApp.class.getResource("view/main/GraphView.fxml"),
                        MainApp.bundle);

                Pane surface = loader.load();
                surface.setMouseTransparent(true);
                GraphViewController controller = loader.getController();

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
                controller.reloadView();
                controller.cropIt();
                controller.setScale(50);

            } catch (IOException e){
                e.printStackTrace();
            }

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
                    + MainApp.bundle.getString("editwindows.component"));

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
