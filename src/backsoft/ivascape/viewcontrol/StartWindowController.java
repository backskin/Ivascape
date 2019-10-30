package backsoft.ivascape.viewcontrol;

import backsoft.ivascape.handler.FileHandler;
import backsoft.ivascape.handler.Loader;
import backsoft.ivascape.handler.Preferences;
import backsoft.ivascape.logic.Triplet;
import backsoft.ivascape.model.CoorsMap;
import backsoft.ivascape.model.IvascapeGraph;
import backsoft.ivascape.model.Project;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static backsoft.ivascape.viewcontrol.MyAlerts.AlertType.UNKNOWN;

public class StartWindowController {

    public ImageView bckgImage;
    private Stage startStage;

    private static boolean restart = false;
    private boolean terminate = false;

    private double xOffset = .0;
    private double yOffset = .0;
    private double x = .0;
    private double y = .0;



    @FXML
    private ImageView splash;

    @FXML
    private void initialize() {

        try {
            splash.setImage(Loader.getImageRsrc(Preferences.current()
                    .getCurrentLoc().getLanguage() + "splash"));
            bckgImage.setImage(Loader.getImageRsrc("startbg"));

        } catch (FileNotFoundException e) {
            MyAlerts.getAlert(UNKNOWN, e.getMessage());
        }

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

    @FXML
    private void handleExit(){ startStage.close(); terminate = true;}

    @FXML
    private void handleNew(){

        Project.newProject();
        startStage.close();
    }

    @FXML
    private void handleOpen() {

        Triplet<File, IvascapeGraph, CoorsMap> output = FileHandler.loadFile(null);

        if (output == null) return;

        Project.get().load(output.getOne(), output.getTwo(), output.getThree());
        startStage.close();
    }

    @FXML
    private void handleLang() throws IOException {

        Preferences.current().changeLoc();
        restart = true;
        startStage.close();
        Loader.loadStartWindow();
    }
}
