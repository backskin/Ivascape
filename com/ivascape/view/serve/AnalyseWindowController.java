package ivascape.view.serve;

import ivascape.MainApp;
import ivascape.controller.GraphWorker;
import ivascape.model.Company;
import ivascape.model.Graph;
import ivascape.controller.IvascapeProject;
import ivascape.model.Link;
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
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class AnalyseWindowController {

    private Stage analyseStage;

    public void setAnalyseStage(Stage analyseStage) {
        this.analyseStage = analyseStage;

        analyseStage.setOnCloseRequest(event -> {
            VisualVertex.resetColor();
            VisualEdge.resetColor();
        });
    }

    @FXML
    private HBox ComponentTables;

    @FXML
    private Label cAmount;

    public AnalyseWindowController(){

    }

    @FXML
    private void initialize(){

        List<Graph<Company,Link>> components =
                IvascapeProject.getGraphWorker().getConnectComponents();

        if (components.size() == 0) {
            Label label = new Label(MainApp.bundle.getString("editwindows.emptygraph"));
            label.setFont(new Font("System",15));
            ComponentTables.getChildren().add(label);
        }
        for (Graph<Company,Link> component: components
             ) {

            VBox form = new VBox();

            form.setFocusTraversable(false);

            form.setAlignment(Pos.CENTER);

            TableView<Company> componentTable = new TableView<>();

            try {

                FXMLLoader loader = new FXMLLoader(
                        MainApp.class.getResource("view/main/GraphView.fxml"),
                        MainApp.bundle);

                Pane surface = loader.load();

                surface.setFocusTraversable(false);
                surface.setMouseTransparent(true);
                GraphViewController controller = loader.getController();

                controller.setGraph(
                        component,
                        IvascapeProject.getVerCoorsMap(),
                        Color.DARKCYAN,
                        Color.DARKCYAN,
                        false);

                controller.reloadView();

                controller.cropIt();

                controller.setScale(40);

                ScrollPane scrollPane = new ScrollPane();

                scrollPane.setContent(surface);

                form.getChildren().add(scrollPane);

                VBox.setVgrow(scrollPane,Priority.ALWAYS);

                scrollPane.setPrefHeight(125);

            } catch (IOException e){

                e.printStackTrace();
            }

            componentTable.setFocusTraversable(false);
            form.getChildren().add(componentTable);
            form.setFocusTraversable(false);
            VBox.setVgrow(componentTable, Priority.ALWAYS);

            ComponentTables.getChildren().add(form);


            componentTable.setMinWidth(260);

            componentTable.setPrefHeight(120);

            HBox.setHgrow(form, Priority.ALWAYS);

            componentTable.setSelectionModel(null);

            HBox.setHgrow(componentTable, Priority.ALWAYS);

            ObservableList<Company> list = FXCollections.observableArrayList();

            Iterator<Company> iterator = (new GraphWorker<>(component))
                    .getSortedVerIterator(Comparator.comparing(Company::getTitle));

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
