package backsoft.ivascape.viewcontrol;

import backsoft.ivascape.handler.AlertHandler;
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

import static backsoft.ivascape.handler.AlertHandler.AlertType.*;

public class RootLayoutController {

    @FXML
    private TabPane tabPane;
    @FXML
    private AnchorPane CompaniesPane;
    @FXML
    private AnchorPane TablePane;
    @FXML
    private AnchorPane MapPane;
    @FXML
    private BorderPane rootPane;
    @FXML
    private Circle saveIcon;
    @FXML
    private Label filenameLabel;
    @FXML
    private Label comsAmountLabel;
    @FXML
    private Label linksAmountLabel;
    @FXML
    private Label isSaved;
    @FXML
    private MenuItem addEdgeMenuItem;
    @FXML
    private MenuItem saveAsMenuItem;
    @FXML
    private MenuItem saveMenuItem;
    @FXML
    private CheckMenuItem rus;
    @FXML
    private CheckMenuItem eng;

    private final Stage mainStage = Loader.getMainStage();
    private final Project project = Project.get();
    private final Preferences preferences = Preferences.getCurrent();

    @FXML
    private void initialize(){

        saveIcon.setFill(Color.GREEN);
        rus.setSelected(preferences.getCurrentLoc().getLanguage().equals("ru"));
        rus.setDisable(rus.isSelected());
        eng.setSelected(!rus.isSelected());
        eng.setDisable(eng.isSelected());

        eng.selectedProperty().addListener((observable, oldValue, newValue) -> {

            preferences.saveWinParams(mainStage);
            preferences.changeLoc();
            Loader.reloadApp();
        });

        rus.selectedProperty().addListener((observable, oldValue, newValue) -> eng.setSelected(!newValue));

        isSaved.setText(preferences.getBundle()
                .getString(project.isSaved() ? "bottombar.saved" : "bottombar.unsaved"));

        saveIcon.setFill(project.isSaved() ? Color.GREEN : Color.DARKRED);

        addEdgeMenuItem.setDisable(true);
        saveMenuItem.setDisable(true);
        saveAsMenuItem.setDisable(true);

        project.savedProperty().addListener((observable, oldValue, newValue) -> {

            saveMenuItem.setDisable(newValue);
            saveAsMenuItem.setDisable(false);

            isSaved.setText(preferences.getBundle()
                    .getString(newValue ? "bottombar.saved" : "bottombar.unsaved"));

            saveIcon.setFill(newValue ? Color.GREEN : Color.DARKRED);
        });

        MapViewController mvController = loadViewToTab("MapView", MapPane);
        CompaniesViewController cvController = loadViewToTab("CompaniesView", CompaniesPane);
        LinksViewController lvController = loadViewToTab("LinksView", TablePane);
        ViewUpdater.putTabControllers(cvController, lvController, mvController);
        mvController.bindSurfaceChangedBidirectional(project.savedProperty());

        mvController.getGVController().getSurface().getChildren()
                .addListener(
                        (ListChangeListener<Node>) c
                                ->
                                addEdgeMenuItem.setDisable(mvController.getGVController()
                                        .getSurface()
                                        .getChildren()
                                        .size()
                                        < 2));

        mainStage.setScene(new Scene(rootPane));

        if (!project.isEmpty())
            ViewUpdater.current().updateAll();

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

    void updateStatusbar(){

        filenameLabel.setText(project.getFile() != null ? project.getFile().getName() : "Empty");
        comsAmountLabel.setText(project.amountOfComs() + "");
        linksAmountLabel.setText(project.amountOfLinks() + "");
        saveAsMenuItem.setDisable(project.isEmpty());
        saveMenuItem.setDisable(project.isEmpty() || project.isSaved());
    }

    @FXML
    private void handleHelpTips(){

        Pair<Parent, TipsController> fxmlData = Loader.loadFXML("Tips");

        TipsController tipsController = fxmlData.getTwo();
        Stage tipsStage = new Stage();
        tipsController.setStage(tipsStage);
        tipsStage.setTitle(preferences.getBundle().getString("tips.caption"));
        tipsStage.initModality(Modality.WINDOW_MODAL);
        tipsStage.initOwner(mainStage);

        Loader.openInAWindow(tipsStage,fxmlData.getOne(),true);
    }

    @FXML
    private void handleHelpAbout(){
        AlertHandler.makeAlert(ABOUT).setOwner(mainStage).show();
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
            AlertHandler.makeAlert(ALGO_ISSUE).setOwner(mainStage).show();
        } else {
            Pair<Parent, ResultWindowController> fxmlData = Loader.loadFXML("ResultWindow");
            Stage resStage = new Stage();
            resStage.setTitle(preferences.getBundle().getString("result_window"));
            fxmlData.getTwo().setStage(resStage);

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
        stage.setTitle(preferences.getBundle().getString("editwindows.analysetitle"));

        Loader.openInAWindow(stage, fxmlData.getOne(), true);
    }

    @FXML
    private void handleFileOpen() {

        if ((project.isSaved() || AlertHandler.makeAlert(CLOSE_CURR_CONFIRM).setOwner(mainStage)
                .showAndGetResult())
                && (project.load(FileHandler.dialogLoad(project.getFile()))))

            ViewUpdater.current().updateAll();
    }


    @FXML
    private void handleFileNew(){

        if (project.isSaved() || AlertHandler.makeAlert(CLOSE_CURR_CONFIRM).setOwner(mainStage)
                .showAndGetResult()) {

            Project.newProject();
            ViewUpdater.current().updateAll();
        }
    }

    @FXML
    private  void handleFileSave(){

        if (project.getFile() != null) {
            project.saveProject();
        } else {
            handleFileSaveAs();
        }
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

        if (project.isSaved() || AlertHandler.makeAlert(EXIT_CONFIRM).showAndGetResult()) {
            Platform.exit();
            System.exit(0);
        }
    }
}