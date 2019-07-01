package ivascape.view.serve;

import ivascape.MainApp;
import ivascape.controller.CoorsMap;
import ivascape.controller.FileHandler;
import ivascape.controller.Project;
import ivascape.model.*;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.Map;

public class StartWindowController {

    private Stage startStage;

    private static boolean restart = false;
    private boolean terminate = false;

    private static double xOffset = 0;
    private static double yOffset = 0;
    private static double x = 0;
    private static double y = 0;

    public StartWindowController() {
    }

    @FXML
    private ImageView splash;

    @FXML
    private void initialize() {

        splash.setImage(new Image("resources/"
                + Locale.getDefault().getLanguage() + "splash.png"));

        splash.setOnMousePressed(event -> {
            xOffset = startStage.getX() - event.getScreenX();
            yOffset = startStage.getY() - event.getScreenY();
        });

        splash.setOnMouseDragged(event -> {
            startStage.setX(event.getScreenX() + xOffset);
            startStage.setY(event.getScreenY() + yOffset);
            x = startStage.getX();
            y = startStage.getY();
        });

    }

    public void setStartStage(Stage startStage){
        this.startStage = startStage;
        if (restart && (x + y > 0)) {
            startStage.setX(x);
            startStage.setY(y);
        }
        restart = false;

    }

    public boolean isTerminate(){ return terminate;}

    public boolean isRestart() { return restart; }

    @FXML
    private void handleExit(){ startStage.close(); terminate = true;}

    @FXML
    private void handleNew(){

        Project.getInstance().newProject();
        startStage.close();
    }

    @FXML
    private void handleOpen() {

        Pair<IvaGraph, CoorsMap> output = FileHandler.loadFile(startStage);

        if (output == null || output.getKey() == null) return;

        Project.getInstance().loadProject(output.getKey(), output.getValue());
        startStage.close();
    }

    @FXML
    private void handleLang(){

        MainApp.changeLoc();
        restart = true;
        startStage.close();
    }
}


