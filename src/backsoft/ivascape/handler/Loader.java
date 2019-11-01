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

    public static Stage getMainStage() {
        return stage;
    }
    private static Stage stage;

    public static <T> Pair<Parent, T> loadFXML(String fxmlDocName) {

        FXMLLoader loader = new FXMLLoader(
                FXApp.class.getResource("fxml/" + fxmlDocName + ".fxml"),
                Preferences.getCurrent().getBundle());
        try {
            return new Pair<>(loader.load(), loader.getController());
        } catch (IOException e) {
            AlertHandler.makeAlert(ISSUE).customContent("\n"+e.getMessage()).show();
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

        stage.showAndWait();
    }

    public static Image loadImageResource(String img) {

        try {
            return new Image(new FileInputStream(new File("resources/"
                    + img + ".png")));
        } catch (FileNotFoundException e) {
            AlertHandler.makeAlert(ISSUE).customContent("\n"+e.getMessage()).show();
            throw new RuntimeException();
        }
    }

    public static void start(Stage mainStage) {

        stage = mainStage;
        Stage startScreenStage = new Stage();
        startScreenStage.initStyle(StageStyle.UNDECORATED);

        if (welcomeScreen(startScreenStage) == TERMINATED) {
            Platform.exit();
            System.exit(0);
        }
        reloadApp();
    }

    private static boolean welcomeScreen(Stage stage) {

        stage.setTitle(Preferences.getCurrent().getBundle().getString("welcome"));
        Pair<Parent, StartWindowController> fxmlData = loadFXML("StartWindow");
        StartWindowController controller = fxmlData.getTwo();
        controller.setStartStage(stage);

        openInAWindow(stage, fxmlData.getOne(), false);

        if (controller.isLocaleChanged()) {

            stage.close();
            return welcomeScreen(stage);
        }

        return controller.getStatus();
    }

    public static void reloadApp() {

        if (stage.isShowing()) stage.close();
        stage.setOnCloseRequest(Preferences::onExit);
        stage.setTitle(Preferences.getCurrent().getBundle().getString("program_name"));
        Pair<Parent, RootLayoutController> fxml = loadFXML("RootLayout");
        ViewUpdater.putRootController(fxml.getTwo());
        openInAWindow(stage,fxml.getOne(),false);
        Preferences.getCurrent().applyWinParams(stage);
    }

    public static void loadDialogEditLink(Integer... hashes) {

        Pair<Parent, LinkEditDialogController> fxmlData = loadFXML("LinkEditDialog");
        LinkEditDialogController controller = fxmlData.getTwo();

        Stage dialogStage = new Stage();
        dialogStage.setTitle(Preferences.getCurrent().getBundle().getString((hashes != null) ?
                "edittabs.header.editlink" : "edittabs.header.newlink"));
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(stage);
        controller.setDialogStage(dialogStage);
        controller.setFields(hashes);

        openInAWindow(dialogStage, fxmlData.getOne(), false);

        if (controller.isConfirmed()) {
            ViewUpdater.current().updateLinksView().updateMapView();
        }
    }

    public static Integer loadDialogEditCompany(Integer comHash) {

        Pair<Parent, CompanyEditDialogController> fxmlData = loadFXML("CompanyEditDialog");
        CompanyEditDialogController CEDController = fxmlData.getTwo();

        Stage dialogStage = new Stage();

        dialogStage.setTitle(Preferences.getCurrent().getBundle().getString((comHash != 0) ?
                "edittabs.header.editcmp" : "edittabs.header.newcmp"));

        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(stage);
        CEDController.setDialogStage(dialogStage);

        CEDController.setEditCompany(comHash);

        openInAWindow(dialogStage, fxmlData.getOne(), false);

        if (CEDController.isOkClicked()) {

            ViewUpdater.current().updateAll();
            return CEDController.getEditCompany().hashCode();
        }
        return 0;
    }
}