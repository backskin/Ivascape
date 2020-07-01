package backsoft.ivascape.handler;

import backsoft.ivascape.FXApp;
import backsoft.ivascape.viewcontrol.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import backsoft.ivascape.logic.Pair;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static backsoft.ivascape.handler.AlertHandler.AlertType.ISSUE;

public class Loader {

    private static final Preferences prefs = Preferences.get();
    private static Stage primaryStage;

    public static Stage getMainStage() {
        return primaryStage;
    }

    public static <T> Pair<Parent, T> loadFXML(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(FXApp.class.getResource("fxml"+File.separator+ fxml + ".fxml"), prefs.getBundle());
            return new Pair<>(loader.load(), loader.getController());
        } catch (IOException e) {
            AlertHandler.makeAlert(ISSUE).customContent(e.toString()).show();
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static void openInAWindow(Stage stage, Parent parent, boolean resizable){
        stage.getIcons().add(Loader.loadImageResource("ico"));
        stage.setResizable(resizable);
        stage.setScene(new Scene(parent));

        if (stage == primaryStage) {
            stage.show();
            stage.setMinHeight(stage.getHeight());
            stage.setMinWidth(stage.getWidth());
            prefs.applyWinParams(primaryStage);
        } else stage.showAndWait();
    }

    public static Image loadImageResource(String img) {

        try {
            return new Image(new FileInputStream(new File(
                    "resources/" + img + ".png")));
        } catch (FileNotFoundException e) {
            AlertHandler.makeAlert(ISSUE).customContent(e.getMessage()).show();
            throw new RuntimeException();
        }
    }

    public static void start(Stage mainStage) {

        primaryStage = mainStage;
        primaryStage.setOnCloseRequest(Preferences::onExit);
        Stage startScreenStage = new Stage();

        startScreenStage.initStyle(StageStyle.TRANSPARENT);
        welcomeScreen(startScreenStage);
        reloadApp();
    }

    private static void welcomeScreen(Stage stage) {

        stage.setTitle(prefs.getStringFromBundle("welcome"));
        Pair<Parent, StartWindowController> fxmlData = loadFXML("StartWindow");
        StartWindowController controller = fxmlData.getTwo();
        controller.setStage(stage);

        openInAWindow(stage, fxmlData.getOne(), false);
        if (controller.isLocaleChanged()) welcomeScreen(stage);
    }

    public static void reloadApp() {

        if (primaryStage.isShowing()) primaryStage.close();
        openInAWindow(primaryStage, loadFXML("RootLayout").getOne(),true);
    }

    public static <T>T loadViewToTab(String path, AnchorPane tab) {

        Pair<Parent, T> fxmlData = Loader.loadFXML(path);
        Node tmp = fxmlData.getOne();

        AnchorPane.setTopAnchor(tmp, 0.0);
        AnchorPane.setLeftAnchor(tmp, 0.0);
        AnchorPane.setRightAnchor(tmp, 0.0);
        AnchorPane.setBottomAnchor(tmp, 0.0);

        tab.getChildren().add(tmp);

        return fxmlData.getTwo();
    }

    public static void loadDialogEditLink(String... companies) {

        Pair<Parent, DialogEditLinkController> fxmlData = loadFXML("DialogEditLink");
        DialogEditLinkController controller = fxmlData.getTwo();

        Stage dialogStage = new Stage();
        controller.setStage(dialogStage);
        controller.setFields(companies);

        openInAWindow(dialogStage, fxmlData.getOne(), false);

        if (controller.isConfirmed()) {
            ViewUpdater.current().updateLinksView();
        }
    }

    public static Object loadDialogEditCompany(String comID) {

        Pair<Parent, DialogEditCompanyController> fxmlData = loadFXML("DialogEditCompany");
        DialogEditCompanyController controller = fxmlData.getTwo();

        Stage dialogStage = new Stage();
        controller.setStage(dialogStage);
        controller.setFields(comID);

        openInAWindow(dialogStage, fxmlData.getOne(), false);

        if (controller.isConfirmed()) {

            ViewUpdater.current().updateLinksView().updateCompaniesView();
            return controller.getEditCompany();
        }
        return null;
    }
}