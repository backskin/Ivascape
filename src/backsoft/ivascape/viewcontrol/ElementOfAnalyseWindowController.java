package backsoft.ivascape.viewcontrol;

import backsoft.ivascape.handler.Loader;
import backsoft.ivascape.handler.Preferences;
import backsoft.ivascape.logic.Pair;
import backsoft.ivascape.model.Company;
import backsoft.ivascape.model.IvascapeGraph;
import backsoft.ivascape.model.Project;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;

public class ElementOfAnalyseWindowController {

    @FXML
    private TableColumn<Company,String> tableColumn;
    @FXML
    private TableView<Company> tableView;
    @FXML
    private ScrollPane scrollPane;

    @FXML
    void initialize(){
    }

    void setComponent(int number, IvascapeGraph component, DoubleProperty scale){

        Pair<Parent, GraphViewController> fxml = Loader.loadFXML("GraphView");
        Pane surface = (Pane) fxml.getOne();
        surface.setMouseTransparent(true);

        fxml.getTwo().setView(scale, component, Project.get().copyCoorsMap(), false);
        fxml.getTwo().cropView();
        scrollPane.setContent(surface);
        component.getVertexIterator().forEachRemaining(company -> tableView.getItems().add(company));
        tableColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        tableColumn.setText(number + Preferences.get().getStringFromBundle(
                "editwindows.component"));
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
}
