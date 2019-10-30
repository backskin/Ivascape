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

    WinParams getWindowParams() {
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

    void setWindowParams(double x, double y, double width, double height) {

        this.windowParams = new WinParams(x, y, width, height);
    }

    public void setWindowParams(Stage stage){

        setWindowParams(stage.getX(), stage.getY(),
                stage.getWidth(), stage.getHeight());
    }

    void reloadBundle() throws IOException {
            bundle = new PropertyResourceBundle(new BufferedReader(new InputStreamReader(
                    getClass().getResourceAsStream(Locale.getDefault().equals(enLoc) ?
                            "../../../translate_en_US.properties" :
                            "../../../translate_ru_RU.properties"), StandardCharsets.UTF_8)));
    }

    private WinParams windowParams;

    private Project project = Project.get();
    public static final Locale ruLoc = new Locale("ru","RU");
    public static final Locale enLoc = new Locale("en", "US");
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

    public static void onExit(WindowEvent event){

        if (!(instance.project.isEmpty() || instance.project.isSaved()) &&
                MyAlerts.getAlert(MyAlerts.AlertType.ON_EXIT, "NOTSAVED").getResult().getButtonData()
                        == ButtonBar.ButtonData.CANCEL_CLOSE)
            event.consume();
    }

    public void changeLoc(){

        currentLoc = currentLoc.equals(ruLoc) ? enLoc: ruLoc;
        Locale.setDefault(currentLoc);
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
