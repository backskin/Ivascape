package ivascape.view.main;

import ivascape.MainApp;
import ivascape.models.Company;
import ivascape.models.Project;
import ivascape.view.serve.CompanyEditDialogController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;

import static ivascape.view.serve.MyAlerts.*;

public class CompaniesViewController {

    private int lastSelected = 0;

    private MainWindowController MWController;
    private Project project = Project.getInstance();

    void setMWController(MainWindowController MWController) {

        this.MWController = MWController;
    }

    private GraphViewController GVController;

    void setGVController(GraphViewController GVController) {
        this.GVController = GVController;
    }

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

        Iterator<Company> companyIterator = project.getGraph().getVertexIterator();

        while (companyIterator.hasNext()) list.add(companyIterator.next());

        return list;
    }

    public void reloadView(){

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

        TextFlow nocontent = new TextFlow(new Text(MainApp.bundle.getString("tabletext.nocontent")));

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

            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("view/serve/CompanyEditDialog.fxml"),
                    MainApp.bundle);

            VBox editDialog = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(MainApp.bundle.getString("edittabs.header.newcmp"));
            Scene scene = new Scene(editDialog);
            dialogStage.setScene(scene);

            CompanyEditDialogController CEDController = loader.getController();
            CEDController.setDialogStage(dialogStage);

            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(MWController.getMainStage());
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

            if (CEDController.isOkClicked()){

                reloadView();

                GVController.addVertex(CEDController.getEditCompany());
                MWController.reloadTV();
            }
        } catch (IOException e){

            getAlert(MyAlertType.UNKNOWN, MWController.getMainStage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEdit() {

        if (companiesTable.getSelectionModel().getSelectedItem() != null) {
            try {

                FXMLLoader loader = new FXMLLoader(
                        MainApp.class.getResource("view/serve/CompanyEditDialog.fxml"),
                        MainApp.bundle);

                VBox editDialog = loader.load();

                Stage dialogStage = new Stage();
                dialogStage.setTitle(MainApp.bundle.getString("edittabs.header.editcmp"));
                dialogStage.setScene(new Scene(editDialog));
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.initOwner(MWController.getMainStage());
                dialogStage.getIcons().add(new Image("resources/ico.png"));

                CompanyEditDialogController CEDController = loader.getController();

                CEDController.setDialogStage(dialogStage);
                CEDController.setEditCompany(companiesTable.getSelectionModel().getSelectedItem());

                dialogStage.setResizable(false);

                dialogStage.showAndWait();

                if (CEDController.isOkClicked()) {

                    Company tmpCompany = companiesTable.getSelectionModel().getSelectedItem();

                    reloadView();

                    GVController.reloadView();
                    MWController.reloadTV();

                    if (companiesTable.getItems().contains(tmpCompany))

                        companiesTable.getSelectionModel().select(tmpCompany);
                }

            } catch (IOException e) {

                getAlert(MyAlertType.UNKNOWN, MWController.getMainStage());
                e.printStackTrace();
            }
        }
    }
    @FXML
    private void handleDelete(){

        if (companiesTable.getSelectionModel().getSelectedItem() != null) {

            Alert alert = getAlert(MyAlertType.DELETE_CONFIRM, MWController.getMainStage());

            if (alert.getResult().getButtonData() == ButtonBar.ButtonData.OK_DONE) {

                GVController.delVertex(companiesTable.getSelectionModel().getSelectedItem());
                project.removeCompany(companiesTable.getSelectionModel().getSelectedItem());

                reloadView();
                MWController.reloadTV();
            }
        }
    }
}
