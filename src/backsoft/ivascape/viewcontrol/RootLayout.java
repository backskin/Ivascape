package backsoft.ivascape.viewcontrol;

import backsoft.ivascape.handler.*;
import backsoft.ivascape.logic.Pair;
import backsoft.ivascape.model.Project;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.File;

import static backsoft.ivascape.handler.AlertHandler.AlertType.*;
import static backsoft.ivascape.handler.Loader.loadViewToTab;
import static javafx.scene.paint.Color.DARKRED;
import static javafx.scene.paint.Color.GREEN;

public class RootLayout {

    @FXML
    private TabPane tabPane;
    @FXML
    private AnchorPane CompaniesPane;
    @FXML
    private AnchorPane TablePane;
    @FXML
    private AnchorPane MapPane;
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
    private CheckMenuItem rusItem;
    @FXML
    private CheckMenuItem engItem;

    private final Stage mainStage = Loader.getMainStage();
    private Project project = Project.get();

    private final Preferences preferences = Preferences.get();

    @FXML
    private void initialize(){
        Loader.getMainStage().setTitle(preferences.getStringFromBundle("maintitle"));

        saveAsMenuItem.setDisable(true);
        addEdgeMenuItem.setDisable(true);
        updateStatusBar(true);

        rusItem.setSelected(preferences.getCurrentLoc().getLanguage().equals("ru"));
        engItem.setSelected(!rusItem.isSelected());
        rusItem.setDisable(rusItem.isSelected());
        engItem.setDisable(engItem.isSelected());

        rusItem.selectedProperty().addListener((val, on, bn) -> engItem.setSelected(!bn));
        engItem.selectedProperty().addListener((val) -> {
            preferences.saveWinParams(mainStage);
            preferences.changeLoc();
            Loader.reloadApp();
        });

        MapViewController mvController = loadViewToTab("MapView", MapPane);
        CompaniesViewController cvController = loadViewToTab("CompaniesView", CompaniesPane);
        LinksViewController lvController = loadViewToTab("LinksView", TablePane);

        ViewUpdater.putTabControllers(cvController, lvController, mvController);
        mvController.bindToSurfaceChanged(project.savedProperty());

        project.companiesAmountProperty().addListener((o, vo, vn) -> {
            saveAsMenuItem.setDisable(vn.intValue() < 1);
            addEdgeMenuItem.setDisable(vn.intValue() < 2);
            comsAmountLabel.setText(vn.intValue() + "");
        });

        project.linksAmountProperty().addListener((o, vo, vn) -> linksAmountLabel.setText(vn.intValue() + ""));
        project.savedProperty().addListener((o, bo, bn) -> updateStatusBar(bn));

        project.companiesAmountProperty().addListener((val, number, t1) ->
                addEdgeMenuItem.setDisable(t1.intValue() < 2));

        tabPane.getSelectionModel().selectedItemProperty().addListener((observable)
                -> preferences.setCurrentTab(tabPane.getSelectionModel().getSelectedIndex()));
        tabPane.getSelectionModel().select(preferences.getCurrentTab());
    }

    private void updateStatusBar(boolean newValue){
        isSaved.setText(preferences.getStringFromBundle(newValue ? "bottombar.saved" : "bottombar.unsaved"));
        saveIcon.setFill(newValue ? GREEN : DARKRED);
        saveMenuItem.setDisable(newValue);
        if (newValue)
            filenameLabel.setText(project.getFile() != null ?
                    project.getFile().getName() : "Empty");
    }

    @FXML
    private void handleHelpTips(){

        Pair<Parent, TipsController> fxmlData = Loader.loadFXML("Tips");

        TipsController tipsController = fxmlData.getTwo();
        Stage tipsStage = new Stage();
        tipsController.setStage(tipsStage);

        Loader.openInAWindow(tipsStage, fxmlData.getOne(),true);
    }

    @FXML
    private void handleHelpAbout(){
        AlertHandler.makeAlert(ABOUT).setOwner(mainStage).show();
    }

    @FXML
    private void handleAddVertex(){
        Loader.loadDialogEditCompany(null);
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
            resStage.setTitle(preferences.getStringFromBundle("result_window"));
            fxmlData.getTwo().setStage(resStage);

            Loader.openInAWindow(resStage, fxmlData.getOne(), true);
        }
    }

    @FXML
    private void handleEditAnalyse(){

        Pair<Parent, AnalyseWindow> fxmlData = Loader.loadFXML("AnalyseWindow");
        Stage stage = new Stage();
        fxmlData.getTwo().setStage(stage);

        Loader.openInAWindow(stage, fxmlData.getOne(), true);
    }

    @FXML
    private void handleFileOpen() {

        if ((project.isSaved() || AlertHandler.makeAlert(CLOSE_CURR_CONFIRM).setOwner(mainStage)
                .showAndGetResult())) {
            project.load(FileHandler.dialogLoad(project.getFile()));
        }
        ViewUpdater.current().updateAll();
    }


    @FXML
    private void handleFileNew(){

        if (project.isSaved() || AlertHandler.makeAlert(CLOSE_CURR_CONFIRM).setOwner(mainStage)
                .showAndGetResult()) {

            Project.get().erase();
        }
        ViewUpdater.current().updateAll();
    }

    @FXML
    private  void handleFileSave(){

        if (project.getFile() != null) {
            project.saveProject();
        } else {
            handleFileSaveAs();
        }
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

    @FXML
    public void _debug_gen() {

        String output = AlertHandler.makeAlert(DEBUG).debugGetInput();
        try {
            IvascapeGenerator.generate(Integer.parseInt(output));
            ViewUpdater.current().updateLinksView().updateCompaniesView();
        } catch (NumberFormatException e){
            System.out.println(e.toString());
        }
    }
}