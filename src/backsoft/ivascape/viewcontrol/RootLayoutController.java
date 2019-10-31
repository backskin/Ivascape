package backsoft.ivascape.viewcontrol;

import backsoft.ivascape.handler.FileHandler;
import backsoft.ivascape.handler.Loader;
import backsoft.ivascape.handler.Preferences;
import backsoft.ivascape.logic.Pair;
import backsoft.ivascape.model.Project;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;

import static backsoft.ivascape.viewcontrol.MyAlerts.AlertType.CLOSE_REQUEST;
import static backsoft.ivascape.viewcontrol.MyAlerts.getAlert;

public class RootLayoutController {


    private final Stage mainStage = Loader.getMainStage();
    private Project project = Project.get();

    @FXML
    private TabPane tabPane;

    @FXML
    private AnchorPane CompaniesView;

    @FXML
    private AnchorPane TableView;

    @FXML
    private AnchorPane MapView;

    @FXML
    private BorderPane rootLayout;

    @FXML
    private Circle saveIcon;

    @FXML
    private Label fileName;

    @FXML
    private Label cAmount;

    @FXML
    private Label lAmount;

    @FXML
    private Label isSaved;

    @FXML
    private MenuItem addEdge;

    @FXML
    private MenuItem SaveAs;

    @FXML
    private MenuItem Save;

    @FXML
    private CheckMenuItem rus;

    @FXML
    private CheckMenuItem eng;

    private GraphViewController gvController;

    @FXML
    private void initialize(){

        Preferences preferences = Preferences.current();

        saveIcon.setFill(Color.GREEN);
        rus.setSelected(Preferences.current().getCurrentLoc().getLanguage().equals("ru"));
        rus.setDisable(rus.isSelected());
        eng.setSelected(!rus.isSelected());
        eng.setDisable(eng.isSelected());

        eng.selectedProperty().addListener((observable, oldValue, newValue) -> {

            Preferences.current().changeLoc();
            Preferences.current().setWindowParams(mainStage);
            Loader.reloadApp();
        });

        rus.selectedProperty().addListener((observable, oldValue, newValue) -> eng.setSelected(!newValue));

        isSaved.setText(Preferences.getBundle()
                .getString(project.isSaved() ? "bottombar.saved" : "bottombar.unsaved"));

        saveIcon.setFill(project.isSaved() ? Color.GREEN : Color.DARKRED);

        addEdge.setDisable(true);
        Save.setDisable(true);
        SaveAs.setDisable(true);

        project.savedProperty().addListener((observable, oldValue, newValue) -> {

            Save.setDisable(newValue);
            SaveAs.setDisable(false);

            isSaved.setText(Preferences.getBundle()
                    .getString(newValue ? "bottombar.saved" : "bottombar.unsaved"));

            saveIcon.setFill(newValue ? Color.GREEN : Color.DARKRED);
        });

        MapViewController mvController = loadViewToTab("MapView", MapView);
        CompaniesViewController cvController = loadViewToTab("CompaniesView", CompaniesView);
        LinksViewController lvController = loadViewToTab("LinksView", TableView);
        ViewUpdater.current().put(mvController).put(cvController).put(lvController);

        assert mvController != null;
        gvController = mvController.getGVController();

        gvController.getSurfaceChangedProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue) {
                        project.setSaved(false);
                    }
                });

        gvController.getSurface().getChildren()
                .addListener(
                        (ListChangeListener<Node>) c
                                ->
                                addEdge.setDisable(gvController
                                        .getSurface()
                                        .getChildren()
                                        .size()
                                        < 2));

        mainStage.setScene(new Scene(rootLayout));

        if (!project.isEmpty()) updateView();

        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)
                -> preferences.setCurrentTab(tabPane.getSelectionModel().getSelectedIndex()));
        tabPane.getSelectionModel().select(preferences.getCurrentTab());
    }

    private <T>T loadViewToTab(String path, AnchorPane tab) {

        Pair<Parent, T> fxmlData = Loader.loadFXML(path);
        Node tmp = fxmlData.getOne();

        AnchorPane.setTopAnchor(tmp, 0.0);
        AnchorPane.setLeftAnchor(tmp, 0.0);
        AnchorPane.setRightAnchor(tmp, 0.0);
        AnchorPane.setBottomAnchor(tmp, 0.0);

        tab.getChildren().add(tmp);

        return fxmlData.getTwo();

    }

    private void updateView(){

        ViewUpdater.current().updateAll();
    }

    void updateStatusbar(){

        fileName.setText(project.getFile() != null ? project.getFile().getName() : "Empty");
        cAmount.setText(project.amountOfComs() + "");
        lAmount.setText(project.amountOfLinks() + "");
        SaveAs.setDisable(project.isEmpty());
        Save.setDisable(project.isEmpty() || project.isSaved());
    }

    @FXML
    private void handleHelpTips(){

        Pair<Parent, TipsController> fxmlData = Loader.loadFXML("Tips");

        TipsController tipsController = fxmlData.getTwo();
        Stage tipsStage = new Stage();
        tipsController.setStage(tipsStage);
        tipsStage.setTitle(Preferences.getBundle().getString("tips.caption"));
        tipsStage.initModality(Modality.WINDOW_MODAL);
        tipsStage.initOwner(mainStage);

        Loader.openInAWindow(tipsStage,fxmlData.getOne(),true);
    }

    @FXML
    private void handleHelpAbout(){

        getAlert(MyAlerts.AlertType.ABOUT, mainStage);
    }

    @FXML
    private void handleAddVertex(){

        Loader.loadDialogEditCompany(0);
    }

    @FXML
    private void handleAddEdge(){

        Loader.loadDialogEditLink();
    }

    @FXML
    private void handleEditRun(){

        if (!project.isGraphStrong()){
            getAlert(MyAlerts.AlertType.ALGO_FAIL, mainStage);

        } else {
            Pair<Parent, ResultWindowController> fxmlData = Loader.loadFXML("ResultWindow");

            ResultWindowController RWController = fxmlData.getTwo();
            Stage resStage = new Stage();
            RWController.setStage(resStage);
            resStage.initModality(Modality.WINDOW_MODAL);
            resStage.initOwner(mainStage);
            resStage.setTitle(Preferences.getBundle().getString("result_window"));

            Loader.openInAWindow(resStage, fxmlData.getOne(), true);
        }
    }

    @FXML
    private void handleEditAnalyse(){

        Pair<Parent, AnalyseWindowController> fxmlData = Loader.loadFXML("AnalyseWindow");
        Stage stage = new Stage();
        fxmlData.getTwo().setStage(stage);

        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(mainStage);

        stage.setTitle(Preferences.getBundle().getString("editwindows.analysetitle"));
//        stage.setWidth(460.0);
//        stage.setHeight(400.0);
//        stage.setMinWidth(400.0);
//        stage.setMinHeight(400.0);

        Loader.openInAWindow(stage, fxmlData.getOne(), true);
    }

    @FXML
    private void handleFileOpen(){

        if (project.isSaved() ||
                getAlert(CLOSE_REQUEST, mainStage)
                        .getResult().getButtonData().isDefaultButton()) {

            if (project.load(FileHandler.dialogLoad(project.getFile()))) updateView();
        }
    }

    @FXML
    private void handleFileNew(){

        if (project.isSaved() ||
                getAlert(CLOSE_REQUEST, mainStage)
                        .getResult().getButtonData().isDefaultButton()) {

            Project.newProject();
            updateView();
        }
    }

    @FXML
    private  void handleFileSave(){

        if (project.getFile() == null)
            handleFileSaveAs();
        else
            project.saveProject();

        updateStatusbar();
    }

    @FXML
    private void handleFileSaveAs(){

        if (!project.isEmpty()) {

            File file = FileHandler.dialogSaveAs(
                    project.getFile(),
                    project.getGraph(),
                    project.getCoorsMap());

            if (file == null) return;
            project.setFile(file);
            project.setSaved(true);
        }

        updateStatusbar();
    }

    @FXML
    private void handleExport(){

        FileHandler.dialogExport(project.getGraph(), mainStage);
    }

    @FXML
    private void handleClose() {

        if (!project.isSaved() &&
                getAlert(MyAlerts.AlertType.ON_EXIT, mainStage, "NOTSAVED")
                        .getResult().getButtonData().isCancelButton())
            return;

        Platform.exit();
        System.exit(0);
    }
}