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

public class MyAlertDialog {

    public enum AlertType {
        EXIT_WITHOUT_SAVE_REQUEST, CLOSE_REQUEST, LOAD_FAILED,
        SAVE_FAILED, DELETE_CONFIRM, INVALID_FIELDS, UPDATE_EDGE,
        ALGO_FAIL, ISSUE, ABOUT
    }
    private Alert alert;
    private String resourceString;
    private ResourceBundle bundle = Preferences.getCurrent().getBundle();

    private Alert confirmationmessage(ResourceBundle bundle, String type){
        Alert alert = new Alert(CONFIRMATION);
        alert.setHeaderText(bundle.getString("alert.text.header."+type));
        alert.setContentText(bundle.getString("alert.text.body."+type));
        alert.getButtonTypes().setAll(
                new ButtonType(bundle.getString("alert.confirm"), ButtonBar.ButtonData.OK_DONE),
                new ButtonType(bundle.getString("alert.exit"), ButtonBar.ButtonData.CANCEL_CLOSE));
        return alert;
    }

    private MyAlertDialog setTitle(){
        return setCustomTitle(bundle.getString("alert.text."+resourceString));
    }

    private MyAlertDialog setHeader(){
        return setCustomHeader(bundle.getString("alert.text.header"+resourceString));
    }

    private MyAlertDialog setContent(){
        return setCustomContent(bundle.getString("alert.text.body"+resourceString));
    }

    private MyAlertDialog setCustomTitle(String title){
        alert.setTitle(title);
        return this;
    }

    private MyAlertDialog setCustomHeader(String header){
        alert.setHeaderText(header);
        return this;
    }

    private MyAlertDialog setCustomContent(String content){
        alert.setContentText(content);
        return this;
    }

    private MyAlertDialog setConfirmButtons(){
        alert.getButtonTypes().setAll(
                new ButtonType(bundle.getString("alert.confirm"), ButtonBar.ButtonData.OK_DONE),
                new ButtonType(bundle.getString("alert.exit"), ButtonBar.ButtonData.CANCEL_CLOSE));
        return this;
    }

    private MyAlertDialog setOkButton(){
        alert.getButtonTypes().setAll(
                new ButtonType(bundle.getString("alert.OK"), ButtonBar.ButtonData.OK_DONE));
        return this;
    }

    public MyAlertDialog setOwner(Stage owner){
        alert.initOwner(owner);
        return this;
    }

    private MyAlertDialog fill(AlertType type){
        switch (type){
            case EXIT_WITHOUT_SAVE_REQUEST:
            case CLOSE_REQUEST:
            case DELETE_CONFIRM:
            case UPDATE_EDGE:
                alert = new Alert(CONFIRMATION);
                return setTitle().setHeader().setContent().setConfirmButtons();
            case ALGO_FAIL:
            case SAVE_FAILED:
            case LOAD_FAILED:
            case INVALID_FIELDS:
                alert = new Alert(ERROR);
                return setTitle().setHeader().setContent().setOkButton();
            case ISSUE:
                return setCustomTitle("Ivascape inner program error")
                        .setCustomHeader("A program failure. Ivascape will be closed");
            case ABOUT:
                alert = new Alert(INFORMATION);
                ImageView imageView = new ImageView(Loader.getImageRsrc("ico"));
                imageView.setFitHeight(64);
                imageView.setFitWidth(64);
                alert.setGraphic(imageView);

        }
    }

    private MyAlertDialog setType(AlertType type){
        switch (type) {

            case EXIT_WITHOUT_SAVE_REQUEST:
                resourceString = "onexit";
                break;

            case CLOSE_REQUEST:
                resourceString = "unsave";
                break;

            case DELETE_CONFIRM:
                resourceString = "delete";
                break;

            case UPDATE_EDGE:
                resourceString = "linkupd";
                break;

            case ABOUT:
                resourceString = "about";
                break;

            case ALGO_FAIL:
                resourceString = "prim";
                break;

            case SAVE_FAILED:
                resourceString = "saveerr";
                break;

            case LOAD_FAILED:
                resourceString = "loaderr";
                break;

            case INVALID_FIELDS:
                resourceString = "invalidfield";
                break;
        }
        return this;
    }

    public static MyAlertDialog get(AlertType type, String... args) {

        Alert alert;
        ResourceBundle bundle = Preferences.getCurrent().getBundle();

        switch (type) {

            case ABOUT: {

                alert = new Alert(INFORMATION);
                ImageView imageView = new ImageView(Loader.getImageRsrc("ico"));
                imageView.setFitHeight(64);
                imageView.setFitWidth(64);
                alert.setGraphic(imageView);
                alert.setTitle(bundle.getString("alert.text.about"));
                alert.setHeaderText(bundle.getString("alert.text.header.about"));
                alert.setContentText(bundle.getString("alert.text.body.about"));
                break;
            }

            case ALGO_FAIL: {

                alert = new Alert(WARNING);
                alert.setTitle(bundle.getString("alert.text.prim"));
                alert.setHeaderText(bundle.getString("alert.text.header.prim"));
                alert.setContentText(bundle.getString("alert.text.body.prim"));
                break;
            }

            case SAVE_FAILED: {

                alert = new Alert(ERROR);
                alert.setTitle(bundle.getString("alert.text.saveerr"));
                alert.setHeaderText(bundle.getString("alert.text.header.saveerr"));
                alert.setContentText(bundle.getString("alert.text.body.saveerr"));
                alert.getButtonTypes().setAll(
                        new ButtonType(bundle.getString("alert.OK"), ButtonBar.ButtonData.OK_DONE));
                break;
            }

            case LOAD_FAILED: {

                alert = new Alert(ERROR);
                alert.setTitle(bundle.getString("alert.text.loaderr"));
                alert.setHeaderText(bundle.getString("alert.text.header.loaderr"));
                alert.setContentText(bundle.getString("alert.text.body.loaderr"));
                alert.getButtonTypes().setAll(
                        new ButtonType(bundle.getString("alert.OK"), ButtonBar.ButtonData.OK_DONE));
                break;
            }
            case INVALID_FIELDS: {

                alert = new Alert(ERROR);
                alert.setTitle(bundle.getString("alert.text.invalidfield"));
                alert.setHeaderText(bundle.getString("alert.text.header.invalidfield"));
                alert.setContentText(args[0]);
                alert.getButtonTypes().setAll(
                        new ButtonType(bundle.getString("alert.OK"), ButtonBar.ButtonData.OK_DONE));
                break;
            }

            case ISSUE: {
                alert = new Alert(ERROR);
                alert.setTitle("Ivascape inner program error");
                alert.setHeaderText("A program failure. Ivascape will be closed");
                alert.setContentText(Arrays.toString(args));
            }

            default: {
                alert = new Alert(ERROR);
                alert.setTitle("Unknown error");
                alert.setHeaderText("Oops...");
                alert.setContentText(Arrays.toString(args));
                break;
            }
        }
        if (owner != null)
        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons()
                .add(Loader.getImageRsrc("ico"));
        alert.showAndWait();

        if (type == AlertType.ISSUE){
            Platform.exit();
            System.exit(0);
        }

        return alert;
    }

    public Alert getAlert() {
        return alert;
    }

    public Alert showAndGet(){
        alert.showAndWait();
        return alert;
    }
}