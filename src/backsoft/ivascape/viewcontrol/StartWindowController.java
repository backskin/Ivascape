package backsoft.ivascape.viewcontrol;

import backsoft.ivascape.handler.FileHandler;
import backsoft.ivascape.handler.Loader;
import backsoft.ivascape.handler.Preferences;
import backsoft.ivascape.model.Project;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class StartWindowController {

    public static final boolean TERMINATED = true;

    public ImageView bckgImage;
    private Stage startStage;
    private boolean restart = false;
    private boolean status = false;

    private static double xOffset = .0;
    private static double yOffset = .0;
    private static double x = .0;
    private static double y = .0;

    @FXML
    private ImageView splash;

    @FXML
    private void initialize() {

        splash.setImage(Loader.loadImageResource(Preferences.get()
                .getCurrentLoc().getLanguage() + "splash"));
        bckgImage.setImage(Loader.loadImageResource("startbg"));

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
        if (restart) {
            startStage.setX(x > 0 ? x : 0);
            startStage.setY(y > 0 ? y : 0);
        }
        restart = false;
    }

    public boolean getStatus(){ return status;}

    @FXML
    private void handleExit(){ startStage.close(); status = TERMINATED;}

    @FXML
    private void handleNew(){

        Project.newProject();
        startStage.close();
    }

    @FXML
    private void handleOpen() {

        if (!Project.get().load(FileHandler.dialogLoad(null))) return;
        startStage.close();
    }

    @FXML
    private void handleLang() {

        restart = true;
        Preferences.get().changeLoc();
        startStage.close();
    }

    public boolean isLocaleChanged() {

        return restart;
    }
}
