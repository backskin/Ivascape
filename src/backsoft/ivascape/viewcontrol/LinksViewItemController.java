package backsoft.ivascape.viewcontrol;

import backsoft.ivascape.handler.Loader;
import backsoft.ivascape.model.Company;
import backsoft.ivascape.model.Project;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class LinksViewItemController {

    private Company company;

    private final Project project = Project.get();

    @FXML
    TitledPane theItem;

    @FXML
    VBox cellsBox;

    @FXML
    Button addButton;

    public LinksViewItemController(){}

    @FXML
    private void handleAdd(){

        Loader.loadDialogEditLink(company.hashCode());
    }

    public void setCompany(Company company) {

        this.company = company;
        theItem.setText(company.getTitle());

        if (project.getCompaniesList().size() < 2)
            addButton.setDisable(true);
    }

    void setCells(List<VBox> cells){

        cellsBox.getChildren().clear();
        cellsBox.getChildren().addAll(cells);
    }
}
