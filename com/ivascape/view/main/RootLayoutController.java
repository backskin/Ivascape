package ivascape.view.main;
import ivascape.MainApp;
import ivascape.models.CoorsMap;
import ivascape.controller.FileHandler;
import ivascape.models.IvaGraph;
import ivascape.models.Project;
import ivascape.logic.*;
import ivascape.view.serve.*;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;

import static ivascape.MainApp.enLoc;
import static ivascape.MainApp.ruLoc;
import static ivascape.view.serve.MyAlerts.*;

public class RootLayoutController {

    private Stage mainStage;

    private MainWindowController MWController;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    private MainApp mainApp;

    private Project project = Project.getInstance();

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

    public RootLayoutController(){}

    @FXML
    private void initialize(){

        saveIcon.setFill(Color.GREEN);
        rus.setSelected(Locale.getDefault().equals(ruLoc));
        rus.setDisable(rus.isSelected());
        eng.setSelected(!rus.isSelected());
        eng.setDisable(eng.isSelected());

        eng.selectedProperty().addListener((observable, oldValue, newValue) -> {

                Locale.setDefault(newValue? enLoc : ruLoc);
            try {
                mainApp.reloadApp();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        rus.selectedProperty().addListener((observable, oldValue, newValue) -> eng.setSelected(!newValue));

        isSaved.setText(MainApp.bundle.getString(project.isSaved() ? "bottombar.saved" : "bottombar.unsaved"));
        saveIcon.setFill(project.isSaved() ? Color.GREEN : Color.DARKRED);

        addEdge.setDisable(true);
        Save.setDisable(true);
        SaveAs.setDisable(true);

        project.savedProperty().addListener((observable, oldValue, newValue) -> {

            Save.setDisable(newValue);
            SaveAs.setDisable(false);

            isSaved.setText(MainApp.bundle.getString(newValue ? "bottombar.saved" : "bottombar.unsaved"));
            saveIcon.setFill(newValue ? Color.GREEN : Color.DARKRED);
        });
    }

    public void reloadView(){

        MWController.reloadAll();
    }

    void reloadStatusBar(){

        fileName.setText(project.getFile() != null ? project.getFile().getName() : "Empty");
        cAmount.setText(Integer.toString(project.getGraph().size()));
        lAmount.setText(Integer.toString(project.linksAmount()));
        SaveAs.setDisable(project.getGraph().size() == 0);
        Save.setDisable(project.getGraph().size() == 0 || project.isSaved());
    }

    public void initMainWindow(Stage mainStage){

        this.mainStage = mainStage;

        try {
            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("view/main/MainWindow.fxml"),
                    MainApp.bundle);

            TabPane mainWindow = loader.load();

            MWController = loader.getController();
            MWController.setMainStage(mainStage);
            MWController.setRootController(this);

            MWController.getMVController().getGVController().getSurfaceChangedProperty().addListener(
                    (observable, oldValue, newValue) -> {
                        if (newValue) {
                            project.setSaved(false);
                        }
                    });

            MWController.getMVController().getGVController()
                    .getSurface().getChildren()
                    .addListener(
                            (ListChangeListener<Node>) c
                                    ->
                                    addEdge.setDisable(
                                            MWController.getMVController()
                                                    .getGVController()
                                                    .getSurface()
                                                    .getChildren()
                                                    .size()
                                                    < 2));

            rootLayout.setCenter(mainWindow);

            mainStage.setScene(new Scene(rootLayout));

            if (!project.isEmpty() && project.getGraph().size() > 0){

                reloadView();
            }

            mainStage.show();
        }
        catch (IOException e){

            getAlert(MyAlertType.UNKNOWN, null);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleHelpTips(){

        try {
            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("view/serve/Tips.fxml"),
                    MainApp.bundle);

            VBox tips = loader.load();
            TipsController tipsController = loader.getController();
            Stage tipsStage = new Stage();
            tipsController.setStage(tipsStage);
            tipsStage.setScene(new Scene(tips));
            tipsStage.setTitle(MainApp.bundle.getString("tips.caption"));
            tipsStage.initModality(Modality.WINDOW_MODAL);
            tipsStage.initOwner(mainStage);
            tipsStage.getIcons().add(new Image("resources/ico.png"));
            tipsStage.setResizable(false);
            tipsStage.showAndWait();

        } catch (IOException e){

            getAlert(MyAlertType.UNKNOWN,mainStage);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleHelpAbout(){

        getAlert(MyAlertType.ABOUT, mainStage);
    }

    @FXML
    private void handleAddVertex(){

        MWController.getCVController().handleNew();
    }

    @FXML
    private void handleAddEdge(){

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
            LEDController.setList(project.getCompaniesList());
            dialogStage.showAndWait();

            if (LEDController.isOkClicked()) {
                if (LEDController.isUpdatePrice())
                    MWController.getMVController().getGVController().editEdge(LEDController.getEditLink());
                else
                    MWController.getMVController().getGVController().addEdge(LEDController.getEditLink());
                MWController.reloadCV();
                MWController.reloadTV();
                MWController.getMVController().getGVController().reloadView();
            }

        } catch (IOException e){

            getAlert(MyAlerts.MyAlertType.UNKNOWN, MWController.getMainStage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditRun(){

        if (!project.isGraphStrong()){

            getAlert(MyAlertType.ALGORITHM_EXEC, mainStage);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("view/serve/ResultWindow.fxml"),
                    MainApp.bundle);

            VBox resultWindow = loader.load();
            ResultWindowController RWController = loader.getController();
            Stage resStage = new Stage();
            RWController.setResultStage(resStage);
            Scene s = new Scene(resultWindow);
            resStage.setScene(s);
            resStage.initModality(Modality.WINDOW_MODAL);
            resStage.initOwner(mainStage);
            resStage.getIcons().add(new Image("resources/ico.png"));
            resStage.setTitle(MainApp.bundle.getString("result_window"));

            Platform.runLater(() -> {
                resStage.setMinWidth(resStage.getWidth());
                resStage.setMinHeight(resStage.getHeight());
            });

            resStage.showAndWait();


        } catch (IOException e){

            getAlert(MyAlertType.UNKNOWN,mainStage);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditAnalyse(){

        try {
            project.setCoorsMap(MWController.getMVController().getGVController().getCoorsMap());

            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("view/serve/AnalyseWindow.fxml"),
                    MainApp.bundle);

            VBox analyseWindow = loader.load();
            AnalyseWindowController AWController = loader.getController();

            Stage analyseStage = new Stage();

            AWController.setAnalyseStage(analyseStage);

            analyseStage.setScene(new Scene(analyseWindow));
            analyseStage.initModality(Modality.WINDOW_MODAL);
            analyseStage.initOwner(mainStage);
            analyseStage.getIcons().add(new Image("resources/ico.png"));
            analyseStage.setTitle(MainApp.bundle.getString("editwindows.analysetitle"));
            analyseStage.setWidth(460.0);
            analyseStage.setHeight(400.0);
            analyseStage.setMinWidth(400.0);
            analyseStage.setMinHeight(400.0);
            analyseStage.showAndWait();

        } catch (IOException e){

            getAlert(MyAlertType.UNKNOWN,mainStage);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleFileOpen(){

        boolean permit = !project.isSaved()
                && project.getGraph().size() > 0
                && getAlert(
                        MyAlertType.CLOSE_UNSAVED, mainStage).getResult().getButtonData().isCancelButton();

        if (permit) {

            Pair<IvaGraph, CoorsMap> output = FileHandler.loadFile(mainStage);

            if (output != null) {

                project.loadProject(output.getKey(),output.getValue());
                reloadView();
            }
        }
    }

    @FXML
    private void handleFileNew(){

        boolean permit = true;

        if (!project.isSaved() && project.getGraph().size() > 0) {

            if (getAlert(MyAlertType.CLOSE_UNSAVED,mainStage).getResult().getButtonData().isCancelButton()) {

                permit = false;
            }
        }

        if (permit) {

            project.newProject();
            reloadView();
        }
    }

    @FXML
    private  void handleFileSave(){

        if (project.getFile() == null)
            handleFileSaveAs();
        else
            project.saveProject();
        reloadStatusBar();
    }

    @FXML
    private void handleFileSaveAs(){

        if (project.getGraph().size() > 0) {

            project.setCoorsMap(MWController.getMVController().getGVController().getCoorsMap());

            project.setSaved(
                    FileHandler.saveProject(mainStage, project.getFile()) || project.isSaved());
        }

        reloadStatusBar();
    }

    @FXML
    private void handleExport(){

        FileHandler.exportToXLS(project.getGraph(), mainStage);
    }

    @FXML
    private void handleClose() {

        if (project.getGraph().size() > 0
                && !project.isSaved()
                && (getAlert(MyAlertType.ON_EXIT, mainStage, "NOTSAVED").getResult()
                                .getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE)) {
            return;
        }

        Platform.exit();
        System.exit(0);
    }
}