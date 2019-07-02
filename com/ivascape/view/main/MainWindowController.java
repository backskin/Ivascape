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

    private LinksViewController LVController;

    private MapViewController MVController;

    MapViewController getMVController() {
        return MVController;
    }

    CompaniesViewController getCVController() {
        return CVController;
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
        LVController = loadViewToTab("view/main/LinksView.fxml", TableView);
        MVController = loadViewToTab("view/main/MapView.fxml", MapView);
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> MainApp.currentTab = tabPane.getSelectionModel().getSelectedIndex());
        tabPane.getSelectionModel().select(MainApp.currentTab);
    }

    void setMainStage(Stage mainStage) {

        this.mainStage = mainStage;
        CVController.setMWController(this);
        LVController.setMWController(this);
        CVController.setGVController(MVController.getGVController());
        LVController.setGVController(MVController.getGVController());
    }

    Stage getMainStage() {
        return mainStage;
    }

    void setRootController(RootLayoutController rootController) {

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

    void reloadCV(){
        CVController.reloadView();
    }

    void reloadLV(){
        LVController.reloadView();
    }

    void reloadMV(){

        MVController.reloadView();
    }

    void reloadGV(){

        MVController.getGVController().reloadView();
    }

    void reloadAll(){

        reloadCV();
        reloadLV();
        MVController.reloadView();
        rootController.reloadStatusBar();
    }
}
