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

    WinParams getSavedWinParams() {
        return windowParams;
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

    public void setWindowParams(Stage stage){

        this.windowParams = new WinParams(stage.getX(), stage.getY(),
                stage.getWidth(), stage.getHeight());
    }

    void applyWinParams(WinParams windowParams, Stage stage){

        if (windowParams != null) {
            stage.setWidth(windowParams.width);
            stage.setHeight(windowParams.height);
            stage.setX(windowParams.x);
            stage.setY(windowParams.y);
        } else if (!stage.isShowing()) {

            stage.show();
            stage.setMinWidth(stage.getWidth());
            stage.setMinHeight(stage.getHeight());
        }
    }

    ResourceBundle reloadBundle() throws IOException {
            bundle = new PropertyResourceBundle(new BufferedReader(new InputStreamReader(
                    getClass().getResourceAsStream(Locale.getDefault().equals(enLoc) ?
                            "../../../translate_en_US.properties" :
                            "../../../translate_ru_RU.properties"), StandardCharsets.UTF_8)));
            return bundle;
    }

    private WinParams windowParams;

    private Project project = Project.get();
    private static final Locale ruLoc = new Locale("ru","RU");
    private static final Locale enLoc = new Locale("en", "US");
    private Locale currentLoc;

    public int getCurrentTab() {
        return currentTab;
    }

    public void setCurrentTab(int currentTab) {
        this.currentTab = currentTab;
    }

    private int currentTab = 0;

    private ResourceBundle bundle;

    public static Preferences current() {

        return (instance == null) ? new Preferences() : instance;
    }

    static void onExit(WindowEvent event){

        if (!(instance.project.isEmpty() || instance.project.isSaved()) &&
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
            MyAlerts.getAlert(MyAlerts.AlertType.ISSUE, e.getMessage());
        }
    }

    public static ResourceBundle getBundle() {
        if (current().bundle == null) {
            try {
                instance.reloadBundle();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return instance.bundle;
    }
}
