package backsoft.ivascape.handler;

import backsoft.ivascape.model.Project;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import static backsoft.ivascape.handler.AlertHandler.AlertType.*;

public class Preferences {

    static class WinParams{

        final double x;
        final double y;
        final double width;
        final double height;

        WinParams(double x, double y, double width, double height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }

    private static Preferences instance = null;
    private static final Locale ruLoc = new Locale("ru","RU");
    private static final Locale enLoc = new Locale("en", "US");
    private Locale currentLoc;
    private WinParams windowParams;
    private ResourceBundle bundle;
    private int currentTab = 0;

    private Preferences() {

        System.setProperty("prism.allowhidpi", "true");
        currentLoc = Locale.getDefault();
        instance = this;
    }

    public Locale getCurrentLoc() {
        return currentLoc;
    }

    public void saveWinParams(Stage stage){

        windowParams = new WinParams(stage.getX(), stage.getY(),
                stage.getWidth(), stage.getHeight());
    }

    void applyWinParams(Stage stage) {

        if (windowParams != null) {
            stage.setWidth(windowParams.width);
            stage.setHeight(windowParams.height);
            stage.setX(windowParams.x);
            stage.setY(windowParams.y);
            windowParams = null;
        }
    }

    private void reloadBundle() {
        try {
            bundle = new PropertyResourceBundle(new BufferedReader(new InputStreamReader(
                    getClass().getResourceAsStream(
                            "../../../translate_"
                                    + Locale.getDefault().toString()
                                    + ".properties"), StandardCharsets.UTF_8)));
        } catch (IOException e) {
            AlertHandler.makeAlert(ISSUE).customContent(e.toString()).show();
            throw new RuntimeException();
        }
    }

    public int getCurrentTab() {
        return currentTab;
    }
    public void setCurrentTab(int currentTab) {
        this.currentTab = currentTab;
    }

    public static Preferences get() {
        return (instance == null) ? new Preferences() : instance;
    }

    static void onExit(WindowEvent event){

        if (!(Project.get().isEmpty() || Project.get().isSaved()) &&
                AlertHandler.makeAlert(EXIT_CONFIRM).showAndGetResult())
            event.consume();
    }

    public void changeLoc() {

        currentLoc = currentLoc.equals(ruLoc) ? enLoc: ruLoc;
        Locale.setDefault(currentLoc);
        reloadBundle();
    }

    public String getValueFromBundle(String resourceKey){
        try {
            return getBundle().getString(resourceKey);
        } catch (MissingResourceException e){
            AlertHandler.makeAlert(ISSUE).customContent(e.toString()).show();
            e.printStackTrace();
            Platform.exit();
            System.exit(-1);
            throw new RuntimeException();
        }
    }

    ResourceBundle getBundle() {
        if (bundle == null) {
            reloadBundle();
        }
        return bundle;
    }
}
