package backsoft.ivascape.handler;

import backsoft.ivascape.model.Company;
import backsoft.ivascape.FXApp;
import backsoft.ivascape.model.Link;
import backsoft.ivascape.viewcontrol.CompanyEditDialogController;
import backsoft.ivascape.viewcontrol.RootLayoutController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import backsoft.ivascape.logic.Pair;
import backsoft.ivascape.viewcontrol.ViewUpdater;
import backsoft.ivascape.viewcontrol.LinkEditDialogController;
import backsoft.ivascape.viewcontrol.StartWindowController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Loader {

    public static Stage getMainStage() {
        return stage;
    }

    public static void setMainStage(Stage mainStage) {
        Loader.stage = mainStage;
    }

    private static Stage stage;

    public static <T> Pair<Parent, T> loadFXML(String fxmlDocName) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                FXApp.class.getResource("fxml/" + fxmlDocName + ".fxml"),
                Preferences.getBundle());

        return new Pair<>(loader.load(), loader.getController());
    }

    public static Image getImageRsrc(String img) throws FileNotFoundException {

        return new Image(new FileInputStream(new File("resources/"
                + img + ".png")));
    }

    public static void loadStartWindow() throws IOException {

            Stage startupStage = new Stage();
            Pair<Parent, StartWindowController> fxmlData = loadFXML("StartWindow");
            startupStage.getIcons().add(Loader.getImageRsrc("ico"));
            startupStage.setTitle(Preferences.getBundle().getString("welcome"));
            startupStage.setScene(new Scene(fxmlData.getOne()));

            StartWindowController controller = fxmlData.getTwo();
            controller.setStartStage(startupStage);

            startupStage.setMinHeight(startupStage.getHeight());
            startupStage.setMinWidth(startupStage.getWidth());
            startupStage.initStyle(StageStyle.UNDECORATED);
            startupStage.showAndWait();

            if (controller.isTerminate()) {
                Platform.exit();
                System.exit(0);
            }
    }

    public static void reloadApp() throws IOException {

        if (stage.isShowing())
            stage.close();
        stage.getIcons().add(Loader.getImageRsrc("ico"));
        stage.setOnCloseRequest(Preferences::onExit);

        stage.setTitle(Preferences.getBundle().getString("program_name"));

        Pair<Parent, RootLayoutController> fxmlData = loadFXML("RootLayout");
        ViewUpdater.current().put(fxmlData.getTwo());

        Preferences.WinParams params = Preferences.current().getWindowParams();

        stage.show();
        if (params != null) {
            stage.setWidth(params.width);
            stage.setHeight(params.height);
            stage.setX(params.x);
            stage.setY(params.y);
        } else {
            stage.setMinWidth(stage.getWidth());
            stage.setMinHeight(stage.getHeight());
        }
    }

    public static void loadDialogEditLink(Link link) throws IOException {
        loadDialogEditLink(link.one(), link.two());
    }

    public static void loadDialogEditLink(Company... companies) throws IOException {

        Pair<Parent, LinkEditDialogController> fxmlData = loadFXML("LinkEditDialog");

        LinkEditDialogController controller = fxmlData.getTwo();

        Stage dialogStage = new Stage();
        dialogStage.setTitle(Preferences.getBundle().getString((companies != null) ?
                "edittabs.header.editlink" : "edittabs.header.newlink"));
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setResizable(false);
        dialogStage.initOwner(stage);
        dialogStage.getIcons().add(Loader.getImageRsrc("ico"));
        Scene scene = new Scene(fxmlData.getOne());
        dialogStage.setScene(scene);
        dialogStage.setResizable(false);

        controller.setDialogStage(dialogStage);
        controller.setFields(companies);

        dialogStage.showAndWait();
        if (controller.isConfirmed()) {
            ViewUpdater.current().updateLinksView().updateMapView();
        }
    }

    public static Company loadDialogEditCompany(Company company) throws IOException {

        Pair<Parent, CompanyEditDialogController> fxmlData = loadFXML("CompanyEditDialog");
        CompanyEditDialogController CEDController = fxmlData.getTwo();

        Stage dialogStage = new Stage();

        dialogStage.setTitle(Preferences.getBundle().getString((company != null) ?
                "edittabs.header.editcmp" : "edittabs.header.newcmp"));
        dialogStage.setScene(new Scene(fxmlData.getOne()));
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(Loader.getMainStage());
        dialogStage.getIcons().add(getImageRsrc("ico"));

        CEDController.setDialogStage(dialogStage);
        if (company != null)
            CEDController.setEditCompany(company);

        dialogStage.setResizable(false);

        dialogStage.showAndWait();

        if (CEDController.isOkClicked()) {

            ViewUpdater.current().updateCompaniesView().updateLinksView().updateGraphView();
            return CEDController.getEditCompany();
        }
        return null;
    }
}