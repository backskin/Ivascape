package ivascape.view.serve;

import ivascape.MainApp;
import ivascape.controller.FileWorker;
import ivascape.model.Graph;
import ivascape.controller.IvascapeProject;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.Locale;

public class StartWindowController {

    private Stage startStage;

    private boolean restart = false;

    private boolean terminated = true;

    public StartWindowController() {
    }

    @FXML
    private ImageView splash;

    @FXML
    private void initialize() {

        splash.setImage(new Image("resources/" + Locale.getDefault().getLanguage() + "splash.png"));

    }

    @FXML
    public void setStartStage(Stage startStage){

        this.startStage = startStage;

    }

    public boolean isTerminated(){

        return terminated;
    }

    public boolean isRestart() {
        return restart;
    }

    @FXML
    private void handleExit(){

        startStage.close();
    }

    @FXML
    private void handleNew(){

        IvascapeProject.EraseProject();
        IvascapeProject.NewProject();
        terminated = false;
        startStage.close();
    }

    @FXML
    private void handleOpen() {

        IvascapeProject.EraseProject();

        Graph graph = FileWorker.loadFile(startStage);

        if (graph == null) {

            return;
        }
        IvascapeProject.setProject(graph);
        IvascapeProject.setSaved(true);
        terminated = false;
        startStage.close();
    }

    @FXML
    private void handleLang(){

        Locale.setDefault(Locale.getDefault().equals(MainApp.ruLoc) ? MainApp.enLoc : MainApp.ruLoc);
        restart = true;
        terminated = false;
        startStage.close();
    }
}
