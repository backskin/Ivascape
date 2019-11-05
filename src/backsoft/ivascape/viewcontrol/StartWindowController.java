package backsoft.ivascape.viewcontrol;

import backsoft.ivascape.handler.FileHandler;
import backsoft.ivascape.handler.Loader;
import backsoft.ivascape.handler.Preferences;
import backsoft.ivascape.model.Project;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class StartWindowController {

    public static final boolean TERMINATED = true;

    public ImageView bckgImage;
    private Stage startStage;
    private boolean restart = false;
    private boolean status = false;

    private double xOffset = .0;
    private double yOffset = .0;

    @FXML
    private ImageView splash;

    @FXML
    private void initialize() {

        splash.setImage(Loader.loadImageResource(Preferences.get()
                .getCurrentLoc().getLanguage() + "splash"));
        bckgImage.setImage(Loader.loadImageResource("startbg"));

        splash.setOnMousePressed(event -> {
            xOffset = event.getScreenX();
            yOffset = event.getScreenY();
        });

        splash.setOnMouseDragged(event -> {
            startStage.setX(startStage.getX() + event.getScreenX() - xOffset);
            startStage.setY(startStage.getY() + event.getScreenY() - yOffset);
            xOffset = event.getScreenX();
            yOffset = event.getScreenY();
        });
    }

    public void setStage(Stage stage){

        this.startStage = stage;

        startStage.setOnCloseRequest(windowEvent -> {
            if (!restart) {
                Platform.exit();
                System.exit(0);
            }
        });
    }

    public boolean getStatus(){ return status;}

    @FXML
    private void handleExit(){ startStage.close(); status = TERMINATED;}

    @FXML
    private void handleNew(){

        Project.get().erase();
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
