package ivascape.view.serve;

import ivascape.MainApp;
import ivascape.model.CoorsMap;
import ivascape.handler.FileHandler;
import ivascape.model.IvaGraph;
import ivascape.model.Project;
import ivascape.logic.*;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.util.Locale;

public class StartWindowController {

    private Stage startStage;

    private static boolean restart = false;
    private boolean terminate = false;

    private static double xOffset = .0;
    private static double yOffset = .0;
    private static double x = .0;
    private static double y = .0;

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

        Project.get().newProject();
        startStage.close();
    }

    @FXML
    private void handleOpen() {

        Triplet<File, IvaGraph, CoorsMap> output = FileHandler.loadFile(null, startStage);

        if (output == null) return;

        Project.get().loadProject(output.getOne(), output.getTwo(), output.getThree());
        startStage.close();
    }

    @FXML
    private void handleLang(){

        MainApp.changeLoc();
        restart = true;
        startStage.close();
    }
}


