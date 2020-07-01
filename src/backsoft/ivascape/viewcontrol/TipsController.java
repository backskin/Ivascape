package backsoft.ivascape.viewcontrol;

import backsoft.ivascape.handler.Loader;
import backsoft.ivascape.handler.Preferences;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TipsController {

    @FXML
    private ImageView splash;
    private Stage tipsStage;

    public void setStage(Stage stage){

        tipsStage = stage;
        tipsStage.setTitle(Preferences.get().getStringFromBundle("tips.caption"));
        tipsStage.initModality(Modality.WINDOW_MODAL);
        tipsStage.initOwner(Loader.getMainStage());
    }

    @FXML
    private void initialize(){
        splash.setImage(Loader.loadImageResource(Preferences.get().getCurrentLoc().getLanguage()+"splash"));
    }

    @FXML
    private void handleClose(){ tipsStage.close(); }
}