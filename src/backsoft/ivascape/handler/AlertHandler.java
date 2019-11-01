package backsoft.ivascape.handler;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.util.ResourceBundle;

import static javafx.scene.control.Alert.AlertType.*;

public class AlertHandler {

    public enum AlertType {
        EXIT_CONFIRM, CLOSE_CURR_CONFIRM, LOAD_ISSUE,
        SAVE_ISSUE, DELETE_CONFIRM, FIELDS_ISSUE, UPDATE_EDGE_CONFIRM,
        ALGO_ISSUE, ISSUE, ABOUT
    }
    private Alert alert;
    private AlertType type;
    private String rsrcString;
    private ResourceBundle bundle = Preferences.getCurrent().getBundle();

    private AlertHandler(){}

    public static AlertHandler makeAlert(AlertType type) {
        return (new AlertHandler()).setType(type).fill();
    }

    public AlertHandler setOwner(Stage owner){
        alert.initOwner(owner);
        return this;
    }

    public void show(){
        alert.showAndWait();
    }

    public boolean showAndGetResult(){
        alert.showAndWait();
        return alert.getResult().getButtonData().isDefaultButton();
    }

    public AlertHandler customContent(String content){
        alert.setContentText(alert.getContentText().concat(content));
        return this;
    }

    private AlertHandler customTitle(String title){
        alert.setTitle(title);
        return this;
    }

    private AlertHandler customHeader(String header){
        alert.setHeaderText(header);
        return this;
    }
    private AlertHandler setTitle(){
        return customTitle(bundle.getString("alert.text.title."+ rsrcString));
    }

    private AlertHandler setHeader(){
        return customHeader(bundle.getString("alert.text.header."+ rsrcString));
    }

    private AlertHandler setContent(){
        return customContent(bundle.getString("alert.text.body."+ rsrcString));
    }

    private AlertHandler setConfirmButtons(){
        alert.getButtonTypes().setAll(
                new ButtonType(bundle.getString("alert.confirm"), ButtonBar.ButtonData.OK_DONE),
                new ButtonType(bundle.getString("alert.exit"), ButtonBar.ButtonData.CANCEL_CLOSE));
        return this;
    }

    private AlertHandler setOkButton(){
        alert.getButtonTypes().setAll(
                new ButtonType(bundle.getString("alert.OK"), ButtonBar.ButtonData.OK_DONE));
        return this;
    }

    private AlertHandler fill(){
        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons()
                .add(Loader.loadImageResource("ico"));
        switch (type){
            case EXIT_CONFIRM:
            case CLOSE_CURR_CONFIRM:
            case DELETE_CONFIRM:
            case UPDATE_EDGE_CONFIRM:
                alert = new Alert(CONFIRMATION);
                return setTitle().setHeader().setContent().setConfirmButtons();
            case ALGO_ISSUE:
            case SAVE_ISSUE:
            case LOAD_ISSUE:
            case FIELDS_ISSUE:
            case ISSUE:
                alert = new Alert(ERROR);
                return setTitle().setHeader().setContent().setOkButton();
            case ABOUT:
                alert = new Alert(INFORMATION);
                ImageView imageView = new ImageView(Loader.loadImageResource("ico"));
                imageView.setFitHeight(128);
                imageView.setFitWidth(128);
                alert.setGraphic(imageView);
                return setTitle().setHeader().setContent().setOkButton();
            default:
                alert = new Alert(NONE);
                return this;
        }
    }

    private AlertHandler setResourceString(String str){
        rsrcString = str;
        return this;
    }

    private AlertHandler setType(AlertType type){
        this.type = type;
        switch (type) {
            case EXIT_CONFIRM:
                return setResourceString("onexit");
            case CLOSE_CURR_CONFIRM:
                return setResourceString("unsave");
            case DELETE_CONFIRM:
                return setResourceString("delete");
            case UPDATE_EDGE_CONFIRM:
                return setResourceString("linkupd");
            case ABOUT:
                return setResourceString("about");
            case ALGO_ISSUE:
                return setResourceString("prim");
            case SAVE_ISSUE:
                return setResourceString("saveerr");
            case LOAD_ISSUE:
                return setResourceString("loaderr");
            case FIELDS_ISSUE:
                return setResourceString("invalidfield");
            case ISSUE:
                return setResourceString("issue");
            default:
                return this;
        }
    }
}