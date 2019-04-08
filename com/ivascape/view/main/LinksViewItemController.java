package ivascape.view.main;

import ivascape.MainApp;
import ivascape.model.Company;
import ivascape.controller.IvascapeProject;
import ivascape.view.serve.LinkEditDialogController;
import ivascape.view.serve.MyAlerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

import static ivascape.view.serve.MyAlerts.getAlert;

public class LinksViewItemController {

    private List<VBox> cells;

    private Company company;

    private MainWindowController MWController;

    private GraphViewController GVController;

    public void setGVController(GraphViewController GVController) {
        this.GVController = GVController;
    }

    @FXML
    TitledPane theItem;

    @FXML
    VBox cellsBox;

    @FXML
    Button Add;

    public LinksViewItemController(){}

    @FXML
    private void initialize(){

        reloadView();
    }

    public void setCompany(Company company) {

        this.company = company;
        theItem.setText(company.getTitle());

        if (IvascapeProject.companiesAmount() < 2)
            Add.setDisable(true);
    }

    public void setMWController(MainWindowController MWController) {

        this.MWController = MWController;
    }

    public void setCells(List<VBox> cells){

        this.cells = cells;

        reloadView();
    }

    private void reloadView(){

        if (cells != null) {

            cellsBox.getChildren().clear();
            cellsBox.getChildren().addAll(cells);
        }
    }

    @FXML
    private void handleAdd(){
        try {
            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("view/serve/LinkEditDialog.fxml"),
                    MainApp.bundle);

            VBox editDialog = loader.load();

            LinkEditDialogController LEDController = loader.getController();
            Stage dialogStage = new Stage();
            dialogStage.setTitle(MainApp.bundle.getString("edittabs.header.newlink"));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setResizable(false);
            dialogStage.initOwner(MWController.getMainStage());
            dialogStage.getIcons().add(new Image("resources/ico.png"));
            Scene scene = new Scene(editDialog);
            dialogStage.setScene(scene);

            LEDController.setDialogStage(dialogStage);

            LEDController.setCompanies(IvascapeProject.getCompaniesListExcept(company));

            LEDController.setCompanyOne(company);

            dialogStage.showAndWait();

            if (LEDController.isOkClicked()) {

                GVController.addEdge(LEDController.getEditLink());
                GVController.reloadView();
                MWController.reloadTV();
            }

        } catch (IOException e){

            getAlert(MyAlerts.MyAlertType.UNKNOWN, MWController.getMainStage());
            e.printStackTrace();
        }

    }
}
