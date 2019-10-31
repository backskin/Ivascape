package backsoft.ivascape.handler;

import backsoft.ivascape.model.Project;
import backsoft.ivascape.viewcontrol.MyAlerts;
import javafx.stage.Stage;
import javafx.scene.control.ButtonBar;
import javafx.stage.WindowEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class Preferences {

    private static Preferences instance = null;

    private Preferences() {

        System.setProperty("prism.allowhidpi", "true");
        currentLoc = Locale.getDefault();
        instance = this;
    }

    public Locale getCurrentLoc() {
        return currentLoc;
    }

    static class WinParams{

        double x;
        double y;
        double width;
        double height;

        WinParams(double x, double y, double width, double height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }

    private WinParams windowParams;
    private int currentTab = 0;
    private ResourceBundle bundle;

    public void saveWinParams(Stage stage){

        windowParams = new WinParams(stage.getX(), stage.getY(),
                stage.getWidth(), stage.getHeight());
    }

    public void applyWinParams(Stage stage) {

        if (windowParams != null) {
            stage.setWidth(windowParams.width);
            stage.setHeight(windowParams.height);
            stage.setX(windowParams.x);
            stage.setY(windowParams.y);
        }
    }

    ResourceBundle reloadBundle() throws IOException {
            bundle = new PropertyResourceBundle(new BufferedReader(new InputStreamReader(
                    getClass().getResourceAsStream(Locale.getDefault().equals(enLoc) ?
                            "../../../translate_en_US.properties" :
                            "../../../translate_ru_RU.properties"), StandardCharsets.UTF_8)));
            return bundle;
    }

    private static final Locale ruLoc = new Locale("ru","RU");
    private static final Locale enLoc = new Locale("en", "US");
    private Locale currentLoc;

    public int getCurrentTab() {
        return currentTab;
    }

    public void setCurrentTab(int currentTab) {
        this.currentTab = currentTab;
    }


    public static Preferences getCurrent() {

        return (instance == null) ? new Preferences() : instance;
    }

    static void onExit(WindowEvent event){

        if (!(Project.get().isEmpty() || Project.get().isSaved()) &&
                MyAlerts.getAlert(MyAlerts.AlertType.ON_EXIT, "NOTSAVED").getResult().getButtonData()
                        == ButtonBar.ButtonData.CANCEL_CLOSE)
            event.consume();
    }

    public void changeLoc() {

        currentLoc = currentLoc.equals(ruLoc) ? enLoc: ruLoc;
        Locale.setDefault(currentLoc);

        try {
            reloadBundle();
        } catch (IOException e) {
            MyAlerts.getAlert(MyAlerts.AlertType.ISSUE, e.getLocalizedMessage());
        }
    }

    public ResourceBundle getBundle() {
        if (bundle == null) {
            try {
                reloadBundle();
            } catch (IOException e) {
                MyAlerts.getAlert(MyAlerts.AlertType.ISSUE, e.getLocalizedMessage());
            }
        }
        return bundle;
    }
}
