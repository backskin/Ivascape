package backsoft.ivascape.viewcontrol;

import backsoft.ivascape.handler.Loader;
import backsoft.ivascape.handler.Preferences;
import backsoft.ivascape.model.Company;
import backsoft.ivascape.model.Project;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;

import static backsoft.ivascape.viewcontrol.MyAlerts.AlertType.DELETE_CONFIRM;
import static backsoft.ivascape.viewcontrol.MyAlerts.AlertType.UNKNOWN;
import static backsoft.ivascape.viewcontrol.MyAlerts.getAlert;

public class CompaniesViewController {

    private int lastSelected = 0;

    private Project project = Project.get();

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

    public CompaniesViewController(){
    }

    private ObservableList<Company> getCompaniesViewItems(){

        ObservableList<Company> list = FXCollections.observableArrayList();
        for (Iterator<Company> i = project.getIteratorOfComs(); i.hasNext();)
            list.add(i.next());
        return list;
    }

    void updateView(){

        ObservableList<Company> companies = getCompaniesViewItems();

        companiesTable.getItems().clear();
        companiesTable.setItems(companies);
        companiesTable.getItems().sort(Comparator.comparing(Company::getTitle));

        if (companies.isEmpty()){
            Edit.setDisable(true);
            Delete.setDisable(true);

        } else {
            Edit.setDisable(false);
            Delete.setDisable(false);
            companiesTable.getSelectionModel().select(lastSelected < companies.size() ? lastSelected : 0);
            showCompanyDetails(companiesTable.getSelectionModel().getSelectedItem());
        }
    }

    private void showCompanyDetails(Company company){

        if (company != null){

            Title.setText(company.getTitle());
            Money.setText(Double.toString(company.getMoneyCapital()));
            Address.setText(company.getAddress());
            Date.setText(company.getDate().toString());

        } else {
            Title.setText("");
            Money.setText("");
            Address.setText("");
            Date.setText("");
        }
    }

    @FXML
    private void initialize(){

        TextFlow nocontent = new TextFlow(new Text(Preferences.getBundle().getString("tabletext.nocontent")));

        nocontent.setTextAlignment(TextAlignment.CENTER);
        nocontent.setPadding(new Insets(8,8,0,8));

        companiesTable.setPlaceholder(nocontent);

        companyNameColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());

        companiesTable.getSelectionModel().selectedItemProperty().
                addListener((observable, oldValue, newValue) -> {

                    lastSelected = companiesTable.getSelectionModel().getSelectedIndex() < 0 ? lastSelected : companiesTable.getSelectionModel().getSelectedIndex();
                    showCompanyDetails(newValue);
                    if (observable == null) {

                        Edit.setDisable(true);
                        Delete.setDisable(true);

                    } else {

                        Edit.setDisable(false);
                        Delete.setDisable(false);
                    }
                } );

        Edit.setDisable(true);
        Delete.setDisable(true);

        showCompanyDetails(null);
    }

    @FXML
    public void handleNew(){

        try {
            companiesTable.getSelectionModel().select(Loader.loadDialogEditCompany(null));
        } catch (IOException e) {
            getAlert(UNKNOWN, Loader.getMainStage(), e.getMessage());
        }
    }

    @FXML
    private void handleEdit() {

        Company selected = companiesTable.getSelectionModel().getSelectedItem();

        if (selected != null) {

            try {
                companiesTable.getSelectionModel().select(Loader.loadDialogEditCompany(selected));
            } catch (IOException e) {
                getAlert(UNKNOWN, Loader.getMainStage(), e.getMessage());
            }
        }
    }

    @FXML
    private void handleDelete(){

        Company company = companiesTable.getSelectionModel().getSelectedItem();

        if (company == null) return;

        if (getAlert(DELETE_CONFIRM, Loader.getMainStage())
                    .getResult().getButtonData().isDefaultButton()) {

            project.remove(company);

            ViewUpdater.current().updateAll();
        }
    }
}
