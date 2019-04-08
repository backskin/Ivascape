package ivascape.view.serve;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.Locale;

public class TipsController {

    @FXML
    private ImageView splash;

    private Stage tipsStage;

    public void setStage(Stage stage){

        tipsStage = stage;
    }

    public TipsController(){
    }

    @FXML
    private void initialize(){

        splash.setImage(new Image("resources/" + Locale.getDefault().getLanguage() + "splash.png"));
    }

    @FXML
    private void handleGotIt(){

        tipsStage.close();
    }
}