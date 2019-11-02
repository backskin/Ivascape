package backsoft.ivascape.handler;

import backsoft.ivascape.FXApp;
import backsoft.ivascape.viewcontrol.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import backsoft.ivascape.logic.Pair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static backsoft.ivascape.handler.AlertHandler.AlertType.ISSUE;
import static backsoft.ivascape.viewcontrol.StartWindowController.TERMINATED;

public class Loader {

    private static final Preferences prefs = Preferences.getCurrent();
    private static Stage primaryStage;

    public static Stage getMainStage() {
        return primaryStage;
    }

    public static <T> Pair<Parent, T> loadFXML(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(FXApp.class.getResource("fxml/"+ fxml + ".fxml"), prefs.getBundle());
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

        Platform.runLater(()->{
            stage.setMinHeight(stage.getHeight());
            stage.setMinWidth(stage.getWidth());
        });

        if (stage == primaryStage) stage.show(); else stage.showAndWait();
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
        startScreenStage.initStyle(StageStyle.UNDECORATED);

        if (welcomeScreen(startScreenStage) == TERMINATED) {
            Platform.exit();
            System.exit(0);
        }
        reloadApp();
    }

    private static boolean welcomeScreen(Stage stage) {

        stage.setTitle(prefs.getValueFromBundle("welcome"));
        Pair<Parent, StartWindowController> fxmlData = loadFXML("StartWindow");
        StartWindowController controller = fxmlData.getTwo();
        controller.setStartStage(stage);

        openInAWindow(stage, fxmlData.getOne(), false);

        stage.close();
        if (controller.isLocaleChanged()) {
            return welcomeScreen(stage);
        }
        return controller.getStatus();
    }

    public static void reloadApp() {

        if (primaryStage.isShowing()) primaryStage.close();

        primaryStage.setTitle(prefs.getValueFromBundle("maintitle"));
        Pair<Parent, MainController> fxml = loadFXML("MainWindow");
        ViewUpdater.current().putRootController(fxml.getTwo());
        openInAWindow(primaryStage, fxml.getOne(),false);
        prefs.applyWinParams(primaryStage);
    }

    public static void loadDialogEditLink(Integer... hashes) {

        Pair<Parent, LinkEditDialogController> fxmlData = loadFXML("LinkEditDialog");
        LinkEditDialogController controller = fxmlData.getTwo();

        Stage dialogStage = new Stage();
        dialogStage.setTitle(prefs.getValueFromBundle((hashes != null) ?
                "edittabs.header.editlink" : "edittabs.header.newlink"));
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        controller.setDialogStage(dialogStage);
        controller.setFields(hashes);

        openInAWindow(dialogStage, fxmlData.getOne(), false);

        if (controller.isConfirmed()) {
            ViewUpdater.current().updateLinksView().updateGraphView();
        }
    }

    public static Integer loadDialogEditCompany(Integer comHash) {

        Pair<Parent, CompanyEditDialogController> fxmlData = loadFXML("CompanyEditDialog");
        CompanyEditDialogController CEDController = fxmlData.getTwo();

        Stage dialogStage = new Stage();

        dialogStage.setTitle(prefs.getValueFromBundle((comHash != 0) ?
                "edittabs.header.editcmp" : "edittabs.header.newcmp"));

        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        CEDController.setDialogStage(dialogStage);

        CEDController.setEditCompany(comHash);

        openInAWindow(dialogStage, fxmlData.getOne(), false);

        if (CEDController.isOkClicked()) {

            ViewUpdater.current()
                    .updateCompaniesView()
                    .updateGraphView()
                    .updateLinksView();

            return CEDController.getEditCompany().hashCode();
        }
        return 0;
    }
}