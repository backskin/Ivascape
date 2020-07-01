package backsoft.ivascape.viewcontrol;

import backsoft.ivascape.handler.AlertHandler;
import backsoft.ivascape.handler.Loader;
import backsoft.ivascape.handler.Preferences;
import backsoft.ivascape.model.Company;
import backsoft.ivascape.model.Project;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

import static backsoft.ivascape.handler.AlertHandler.AlertType.DELETE_CONFIRM;

public class CompaniesViewController {

    private Company lastSelected;
    private final Project project = Project.get();
    private final Preferences prefs = Preferences.get();

    @FXML
    private TableView<Company> companiesTable;
    @FXML
    private TableColumn<Company,String> companyNameColumn;
    @FXML
    private Label Title;
    @FXML
    private Label Money;
    @FXML
    private Text Address;
    @FXML
    private Label Date;
    @FXML
    private Button Edit;
    @FXML
    private Button Delete;

    void updateView() {

        companiesTable.getItems().clear();
        showDetails(null);
        if (!project.isEmpty()) {
            companiesTable.getItems().clear();
            project.getIteratorOfCompanies().forEachRemaining(company -> companiesTable.getItems().add(company));
            companiesTable.getSelectionModel().select(lastSelected);
        }
    }

    private void showDetails(Company company){

            Title.setText(company == null ? "" :company.getTitle());
            Money.setText(company == null ? "" :Double.toString(company.getMoney()));
            Address.setText(company == null ? "" :company.getAddress());
            Date.setText(company == null ? "" :company.getDate().toString());
    }

    @FXML
    private void initialize(){
        TextFlow nocontent = new TextFlow(new Text(prefs.getStringFromBundle("tabletext.nocontent")));
        nocontent.setTextAlignment(TextAlignment.CENTER);
        nocontent.setPadding(new Insets(8, 8, 0, 8));
        companiesTable.setPlaceholder(nocontent);

        companyNameColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        companiesTable.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            lastSelected = nv;
            showDetails(lastSelected);
            Edit.setDisable(nv == null);
            Delete.setDisable(nv == null);
        });
        updateView();
    }

    @FXML
    public void handleNew(){ Loader.loadDialogEditCompany(""); }

    @FXML
    private void handleEdit() { Loader.loadDialogEditCompany(lastSelected.getID()); }

    @FXML
    private void handleDelete(){

        if (lastSelected != null && AlertHandler.makeAlert(DELETE_CONFIRM)
                .setOwner(Loader.getMainStage()).showAndGetResult()) {
            project.remove(lastSelected);
            ViewUpdater.current().updateCompaniesView().updateLinksView();
        }
    }
}
