package backsoft.ivascape.viewcontrol;

import backsoft.ivascape.handler.FileHandler;
import backsoft.ivascape.handler.Loader;
import backsoft.ivascape.handler.Preferences;
import backsoft.ivascape.logic.Pair;
import backsoft.ivascape.logic.Triplet;
import backsoft.ivascape.model.Company;
import backsoft.ivascape.model.CoorsMap;
import backsoft.ivascape.model.IvascapeGraph;
import backsoft.ivascape.model.Project;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

import static backsoft.ivascape.viewcontrol.MyAlerts.AlertType.UNKNOWN;
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
        rus.setSelected(Preferences.current().getCurrentLoc().equals(Preferences.ruLoc));
        rus.setDisable(rus.isSelected());
        eng.setSelected(!rus.isSelected());
        eng.setDisable(eng.isSelected());

        eng.selectedProperty().addListener((observable, oldValue, newValue) -> {

                Preferences.current().changeLoc();
                Preferences.current().setWindowParams(mainStage);
            try {
                Loader.reloadApp();
            } catch (IOException e) {
                e.printStackTrace();
            }
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

        if (!project.isEmpty() && project.getGraph().size() > 0) {

            updateView();
        }
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)
                -> preferences.setCurrentTab(tabPane.getSelectionModel().getSelectedIndex()));
        tabPane.getSelectionModel().select(preferences.getCurrentTab());
    }

    private <T>T loadViewToTab(String path, AnchorPane tab) {

        try {
            Pair<Parent, T> fxmlData = Loader.loadFXML(path);
            Node tmp = fxmlData.getOne();

            AnchorPane.setTopAnchor(tmp, 0.0);
            AnchorPane.setLeftAnchor(tmp, 0.0);
            AnchorPane.setRightAnchor(tmp, 0.0);
            AnchorPane.setBottomAnchor(tmp, 0.0);

            tab.getChildren().add(tmp);

            return fxmlData.getTwo();

        } catch (IOException e) {

            getAlert(UNKNOWN, mainStage, e.getMessage());
            return null;
        }
    }

    private void updateView(){

        ViewUpdater.current().updateAll();
    }

    void updateStatusbar(){

        fileName.setText(project.getFile() != null ? project.getFile().getName() : "Empty");
        cAmount.setText(project.getGraph().size() + "");
        lAmount.setText(project.linksAmount() + "");
        SaveAs.setDisable(project.getGraph().size() == 0);
        Save.setDisable(project.getGraph().size() == 0 || project.isSaved());
    }

    @FXML
    private void handleHelpTips(){

        try {
            Pair<Parent, TipsController> fxmlData = Loader.loadFXML("Tips");

            TipsController tipsController = fxmlData.getTwo();
            Stage tipsStage = new Stage();
            tipsController.setStage(tipsStage);
            tipsStage.setScene(new Scene(fxmlData.getOne()));
            tipsStage.setTitle(Preferences.getBundle().getString("tips.caption"));
            tipsStage.initModality(Modality.WINDOW_MODAL);
            tipsStage.initOwner(mainStage);
            tipsStage.getIcons().add(new Image("resources/ico.png"));
            tipsStage.setResizable(false);
            tipsStage.showAndWait();

        } catch (IOException e){

            getAlert(UNKNOWN,mainStage);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleHelpAbout(){

        getAlert(MyAlerts.AlertType.ABOUT, mainStage);
    }

    @FXML
    private void handleAddVertex(){

        try {
            Loader.loadDialogEditCompany(null);
        } catch (IOException e) {
            getAlert(UNKNOWN, mainStage, e.getMessage());
        }
    }

    @FXML
    private void handleAddEdge(){

        try {
            Loader.loadDialogEditLink((Company) null);
        } catch (IOException e) {
            getAlert(UNKNOWN, mainStage, e.getMessage());
        }
    }

    @FXML
    private void handleEditRun(){

        if (!project.isGraphStrong()){
            getAlert(MyAlerts.AlertType.ALGORITHM_EXEC, mainStage);

        } else try {
            Pair<Parent, ResultWindowController> fxmlData = Loader.loadFXML("ResultWindow");

            ResultWindowController RWController = fxmlData.getTwo();
            Stage resStage = new Stage();
            RWController.setStage(resStage);
            Scene s = new Scene(fxmlData.getOne());
            resStage.setScene(s);
            resStage.initModality(Modality.WINDOW_MODAL);
            resStage.initOwner(mainStage);
            resStage.getIcons().add(new Image("resources/ico.png"));
            resStage.setTitle(Preferences.getBundle().getString("result_window"));

            Platform.runLater(() -> {
                resStage.setMinWidth(resStage.getWidth());
                resStage.setMinHeight(resStage.getHeight());
            });

            resStage.showAndWait();

        } catch (IOException e) {

            getAlert(UNKNOWN, mainStage, e.getMessage());
        }
    }

    @FXML
    private void handleEditAnalyse(){

        try {

            Pair<Parent, AnalyseWindowController> fxmlData = Loader.loadFXML("AnalyseWindow");

            Stage stage = new Stage();
            fxmlData.getTwo().setStage(stage);

            stage.setScene(new Scene(fxmlData.getOne()));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(mainStage);
            stage.getIcons().add(Loader.getImageRsrc("ico"));
            stage.setTitle(Preferences.getBundle().getString("editwindows.analysetitle"));
            stage.setWidth(460.0);
            stage.setHeight(400.0);
            stage.setMinWidth(400.0);
            stage.setMinHeight(400.0);
            stage.showAndWait();

        } catch (IOException e){

            getAlert(UNKNOWN, mainStage);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleFileOpen(){

        if (project.isSaved() ||
                getAlert(MyAlerts.AlertType.CLOSE_UNSAVED, mainStage)
                        .getResult().getButtonData().isDefaultButton()) {

            Triplet<File, IvascapeGraph, CoorsMap> output = FileHandler.loadFile(project.getFile());

            if (output != null) {

                project.load(output.getOne(), output.getTwo(), output.getThree());
                updateView();
            }
        }
    }

    @FXML
    private void handleFileNew(){

        if (project.isSaved() ||
                getAlert(MyAlerts.AlertType.CLOSE_UNSAVED, mainStage)
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

        if (project.getGraph().size() > 0) {

            File file = FileHandler.saveAs(
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

        FileHandler.exportToXLS(project.getGraph(), mainStage);
    }

    @FXML
    private void handleClose() {

        if (project.getGraph().size() > 0 &&
                !project.isSaved() &&
                getAlert(MyAlerts.AlertType.ON_EXIT, mainStage, "NOTSAVED")
                        .getResult().getButtonData().isCancelButton() )
            return;

        Platform.exit();
        System.exit(0);
    }
}