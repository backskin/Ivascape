package backsoft.ivascape.viewcontrol;

import backsoft.ivascape.handler.Loader;
import backsoft.ivascape.handler.Preferences;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.ResourceBundle;

import static javafx.scene.control.Alert.AlertType.*;

public class MyAlerts {

    public enum AlertType {
        ON_EXIT, CLOSE_REQUEST, LOAD_FAILED,
        SAVE_FAILED, DELETE_CONFIRM, INVALID_FIELDS, UPDATE_EDGE,
        ALGO_FAIL, ISSUE, ABOUT
    }

    public static Alert getAlert(AlertType type, String... args) {
        return getAlert(type, null, args);
    }

    public static Alert getAlert(AlertType type, Stage owner, String... args) {

        Alert alert;
        Preferences currentPreferences = Preferences.current();
        assert currentPreferences != null;
        ResourceBundle bundle = Preferences.getBundle();

        switch (type) {

            case ON_EXIT: {
                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle(bundle.getString("alert.text.onexit"));
                alert.setHeaderText(bundle.getString("alert.text.conf.onexit"));
                alert.setContentText(args.length > 0 ? bundle.getString("alert.text.warn.onexit") : bundle.getString("alert.text.warn.onsafeexit"));
                alert.getButtonTypes().setAll(
                        new ButtonType(bundle.getString("alert.confirm"), ButtonBar.ButtonData.OK_DONE),
                        new ButtonType(bundle.getString("alert.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE));
                break;
            }

            case CLOSE_REQUEST: {
                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle(bundle.getString("alert.text.unsave"));
                alert.setHeaderText(bundle.getString("alert.text.conf.unsave"));
                alert.setContentText(bundle.getString("alert.text.warn.unsave"));
                alert.getButtonTypes().setAll(
                        new ButtonType(bundle.getString("alert.confirm"), ButtonBar.ButtonData.OK_DONE),
                        new ButtonType(bundle.getString("alert.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE));
                break;
            }

            case DELETE_CONFIRM: {
                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle(bundle.getString("alert.text.delete"));
                alert.setHeaderText(bundle.getString("alert.text.conf.delete"));
                alert.setContentText(bundle.getString("alert.text.warn.delete"));
                alert.getButtonTypes().setAll(
                        new ButtonType(bundle.getString("alert.confirm"), ButtonBar.ButtonData.OK_DONE),
                        new ButtonType(bundle.getString("alert.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE));
                break;
            }

            case SAVE_FAILED: {

                alert = new Alert(ERROR);
                alert.setTitle(bundle.getString("alert.text.saveerr"));
                alert.setHeaderText(bundle.getString("alert.text.conf.saveerr"));
                alert.setContentText(bundle.getString("alert.text.warn.saveerr"));
                alert.getButtonTypes().setAll(
                        new ButtonType(bundle.getString("alert.OK"), ButtonBar.ButtonData.OK_DONE));
                break;
            }

            case LOAD_FAILED: {

                alert = new Alert(ERROR);
                alert.setTitle(bundle.getString("alert.text.loaderr"));
                alert.setHeaderText(bundle.getString("alert.text.conf.loaderr"));
                alert.setContentText(bundle.getString("alert.text.warn.loaderr"));
                alert.getButtonTypes().setAll(
                        new ButtonType(bundle.getString("alert.OK"), ButtonBar.ButtonData.OK_DONE));
                break;
            }
            case INVALID_FIELDS: {

                alert = new Alert(ERROR);
                alert.setTitle(bundle.getString("alert.text.invalidfield"));
                alert.setHeaderText(bundle.getString("alert.text.conf.invalidfield"));
                alert.setContentText(args[0]);
                alert.getButtonTypes().setAll(
                        new ButtonType(bundle.getString("alert.OK"), ButtonBar.ButtonData.OK_DONE));
                break;
            }

            case UPDATE_EDGE: {

                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle(bundle.getString("alert.text.linkupd"));
                alert.setHeaderText(bundle.getString("alert.text.conf.linkupd"));
                alert.setContentText(bundle.getString("alert.text.warn.linkupd"));
                alert.getButtonTypes().setAll(
                        new ButtonType(bundle.getString("alert.confirm"), ButtonBar.ButtonData.OK_DONE),
                        new ButtonType(bundle.getString("alert.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE));
                break;
            }

            case ALGO_FAIL: {

                alert = new Alert(WARNING);
                alert.setTitle(bundle.getString("alert.text.prim"));
                alert.setHeaderText(bundle.getString("alert.text.conf.prim"));
                alert.setContentText(bundle.getString("alert.text.warn.prim"));
                break;
            }

            case ABOUT: {

                alert = new Alert(INFORMATION);
                ImageView imageView = new ImageView(Loader.getImageRsrc("ico"));
                imageView.setFitHeight(64);
                imageView.setFitWidth(64);
                alert.setGraphic(imageView);
                alert.setTitle(bundle.getString("alert.text.about"));
                alert.setHeaderText(bundle.getString("alert.text.conf.about"));
                alert.setContentText(bundle.getString("alert.text.warn.about"));
                break;
            }

            case ISSUE: {
                alert = new Alert(ERROR);
                alert.setTitle("Ivascape inner program error");
                alert.setHeaderText("A program failure. Ivascape will be closed");
                alert.setContentText(Arrays.toString(args));
                if (owner != null) alert.initOwner(owner);
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(Loader.getImageRsrc("ico"));
                alert.showAndWait();

                Platform.exit();
                System.exit(0);
            }

            default: {
                alert = new Alert(ERROR);
                alert.setTitle("Unknown error");
                alert.setHeaderText("Oops...");
                alert.setContentText(Arrays.toString(args));
                break;
            }
        }
        if (owner != null) alert.initOwner(owner);
        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons()
                .add(Loader.getImageRsrc("ico"));
        alert.showAndWait();
        return alert;
    }
}