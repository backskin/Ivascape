package ivascape.view.main;
import ivascape.MainApp;
import ivascape.controller.FileWorker;
import ivascape.model.Company;
import ivascape.model.Graph;
import ivascape.controller.IvascapeProject;
import ivascape.model.Link;
import ivascape.model.Pair;
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
import java.util.Map;

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
                MWController.saveGV();
            try {
                mainApp.reloadApp();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        rus.selectedProperty().addListener((observable, oldValue, newValue) -> eng.setSelected(!newValue));

        isSaved.setText(MainApp.bundle.getString(IvascapeProject.isSaved() ? "bottombar.saved" : "bottombar.unsaved"));
        saveIcon.setFill(IvascapeProject.isSaved() ? Color.GREEN : Color.DARKRED);

        addEdge.setDisable(true);
        Save.setDisable(true);
        SaveAs.setDisable(true);

        IvascapeProject.savedProperty().addListener((observable, oldValue, newValue) -> {

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

        fileName.setText(IvascapeProject.getProjectName());

        cAmount.setText(Integer.toString(IvascapeProject.companiesAmount()));

        lAmount.setText(Integer.toString(IvascapeProject.linksAmount()));

        SaveAs.setDisable(IvascapeProject.companiesAmount() == 0);

        Save.setDisable(IvascapeProject.companiesAmount() == 0 || IvascapeProject.isSaved());
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

            MWController.getMVController().getGVController().surfaceChangedProperty().addListener(
                    (observable, oldValue, newValue) -> {
                        if (newValue) {
                            IvascapeProject.setSaved(false);
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

            if (!IvascapeProject.isEmpty() && IvascapeProject.companiesAmount() > 0){

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

            LEDController.setCompanies(IvascapeProject.getCompaniesList());

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

        if (!IvascapeProject.isProjectStrongGraph()){

            getAlert(MyAlertType.ALGORITHM_EXEC, mainStage);
            return;
        }

        try {
            IvascapeProject.setVerCoorsMap(MWController.getMVController().getGVController().getCoorsMap());

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
            IvascapeProject.setVerCoorsMap(MWController.getMVController().getGVController().getCoorsMap());

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

        boolean permit = true;


        Pair<Graph<Company, Link>, Map<String, Pair<Double, Double>>>
                output = FileWorker.loadFile(mainStage);



        if (output != null) {

            IvascapeProject.EraseProject();
            Graph graph = output.getOne();
            IvascapeProject.setProject(graph);
            IvascapeProject.setSaved(true);
            IvascapeProject.setVerCoorsMap(output.getTwo());
            reloadView();
        }
    }

    @FXML
    private void handleFileNew(){

        boolean permit = true;

        if (!IvascapeProject.isSaved() && IvascapeProject.companiesAmount() > 0) {

            if (getAlert(MyAlertType.CLOSE_UNSAVED,mainStage).getResult().getButtonData().isCancelButton()) {

                permit = false;
            }
        }

        if (permit) {

            IvascapeProject.EraseProject();
            IvascapeProject.NewProject();

            reloadView();
        }
    }

    @FXML
    private  void handleFileSave(){
        if (IvascapeProject.companiesAmount() > 0) {

            IvascapeProject.setVerCoorsMap(MWController.getMVController().getGVController().getCoorsMap());

            if (IvascapeProject.getFile() == null)

                IvascapeProject.setSaved( FileWorker.saveProject(mainStage) || IvascapeProject.isSaved());

            else IvascapeProject.setSaved(FileWorker.saveProject(mainStage,IvascapeProject.getFile()) || IvascapeProject.isSaved());
        }

        reloadStatusBar();
    }

    @FXML
    private void handleFileSaveAs(){

        if (IvascapeProject.companiesAmount() > 0) {

            IvascapeProject.setVerCoorsMap(MWController.getMVController().getGVController().getCoorsMap());

            IvascapeProject.setSaved(FileWorker.saveProject(mainStage) || IvascapeProject.isSaved());
        }
        reloadStatusBar();
    }

    @FXML
    private void handleClose() {

        if (IvascapeProject.companiesAmount() >= 1 && !IvascapeProject.isSaved()) {
            if (getAlert(MyAlertType.ON_EXIT, mainStage, "NOTSAVED").getResult().getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE)
            return;
        }

        Platform.exit();
        System.exit(0);
    }
}