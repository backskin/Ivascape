package ivascape.view.main;

import ivascape.MainApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

import static ivascape.view.serve.MyAlerts.*;

public class MainWindowController {

    private Stage mainStage;

    private RootLayoutController rootController;

    private CompaniesViewController CVController;

    private LinksViewController TVController;

    private MapViewController MVController;

    public MapViewController getMVController() {
        return MVController;
    }

    public CompaniesViewController getCVController() {
        return CVController;
    }

    public LinksViewController getTVController() {
        return TVController;
    }

    @FXML
    private TabPane tabPane;
    @FXML
    private AnchorPane CompaniesView;
    @FXML
    private AnchorPane TableView;
    @FXML
    private AnchorPane MapView;

    public  MainWindowController(){}

    @FXML
    private void initialize(){

        CVController = loadViewToTab("view/main/CompaniesView.fxml", CompaniesView);
        TVController = loadViewToTab("view/main/LinksView.fxml", TableView);
        MVController = loadViewToTab("view/main/MapView.fxml", MapView);
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> MainApp.currentTab = tabPane.getSelectionModel().getSelectedIndex());
        tabPane.getSelectionModel().select(MainApp.currentTab);
    }

    public void setMainStage(Stage mainStage) {

        this.mainStage = mainStage;
        CVController.setMWController(this);
        TVController.setMWController(this);
        CVController.setGVController(MVController.getGVController());
        TVController.setGVController(MVController.getGVController());
    }

    public Stage getMainStage() {
        return mainStage;
    }

    public void setRootController(RootLayoutController rootController) {

        this.rootController = rootController;
    }

    private <T> T loadViewToTab(String path, AnchorPane tab) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource(path),
                    MainApp.bundle);

            Node tmp = loader.load();

            AnchorPane.setTopAnchor(tmp, 0.0);
            AnchorPane.setLeftAnchor(tmp, 0.0);
            AnchorPane.setRightAnchor(tmp, 0.0);
            AnchorPane.setBottomAnchor(tmp, 0.0);

            tab.getChildren().add(tmp);

            return loader.getController();

        } catch (IOException e) {

            getAlert(MyAlertType.UNKNOWN, mainStage);
            e.printStackTrace();
        }

        return null;
    }

    public void reloadCV(){
        CVController.reloadView();
    }

    public void reloadTV(){
        TVController.reloadView();
    }

    public void saveGV(){

        MVController.saveGV();
    }

    public void reloadAll(){

        reloadCV();
        reloadTV();
        MVController.reloadView();
        rootController.reloadStatusBar();
    }
}
