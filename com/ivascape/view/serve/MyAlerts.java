package ivascape.view.serve;

import ivascape.MainApp;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class MyAlerts {

    public enum MyAlertType{ON_EXIT, CLOSE_UNSAVED, LOAD_FAILED,
        SAVE_FAILED, DELETE_CONFIRM, INVALID_FIELDS, UPDATE_EDGE,
        ALGORITHM_EXEC, UNKNOWN, ABOUT}

    public static Alert getAlert(MyAlertType alertType, Stage owner, String... args){

        Alert alert;

        switch (alertType){

            case ON_EXIT:{
                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.initOwner(owner);
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image("resources/ico.png"));
                alert.setTitle(MainApp.bundle.getString("alert.text.onexit"));
                alert.setHeaderText(MainApp.bundle.getString("alert.text.conf.onexit"));
                alert.setContentText(args.length > 0 ? MainApp.bundle.getString("alert.text.warn.onexit") : MainApp.bundle.getString("alert.text.warn.onsafeexit"));
                alert.getButtonTypes().setAll(
                        new ButtonType(MainApp.bundle.getString("alert.confirm"), ButtonBar.ButtonData.OK_DONE),
                        new ButtonType(MainApp.bundle.getString("alert.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE));
                break;
            }

            case CLOSE_UNSAVED:{
                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.initOwner(owner);
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image("resources/ico.png"));
                alert.setTitle(MainApp.bundle.getString("alert.text.unsave"));
                alert.setHeaderText(MainApp.bundle.getString("alert.text.conf.unsave"));
                alert.setContentText(MainApp.bundle.getString("alert.text.warn.unsave"));
                alert.getButtonTypes().setAll(
                        new ButtonType(MainApp.bundle.getString("alert.confirm"), ButtonBar.ButtonData.OK_DONE),
                        new ButtonType(MainApp.bundle.getString("alert.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE));
                break;
            }

            case DELETE_CONFIRM:{
                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.initOwner(owner);
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image("resources/ico.png"));
                alert.setTitle(MainApp.bundle.getString("alert.text.delete"));
                alert.setHeaderText(MainApp.bundle.getString("alert.text.conf.delete"));
                alert.setContentText(MainApp.bundle.getString("alert.text.warn.delete"));
                alert.getButtonTypes().setAll(
                        new ButtonType(MainApp.bundle.getString("alert.confirm"), ButtonBar.ButtonData.OK_DONE),
                        new ButtonType(MainApp.bundle.getString("alert.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE));
                break;
            }

            case SAVE_FAILED:{

                alert = new Alert(Alert.AlertType.ERROR);
                alert.initOwner(owner);
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image("resources/ico.png"));
                alert.setTitle(MainApp.bundle.getString("alert.text.saveerr"));
                alert.setHeaderText(MainApp.bundle.getString("alert.text.conf.saveerr"));
                alert.setContentText(MainApp.bundle.getString("alert.text.warn.saveerr"));
                alert.getButtonTypes().setAll(
                        new ButtonType(MainApp.bundle.getString("alert.OK"), ButtonBar.ButtonData.OK_DONE));
                break;
            }

            case LOAD_FAILED:{

                alert = new Alert(Alert.AlertType.ERROR);
                alert.initOwner(owner);
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image("resources/ico.png"));
                alert.setTitle(MainApp.bundle.getString("alert.text.loaderr"));
                alert.setHeaderText(MainApp.bundle.getString("alert.text.conf.loaderr"));
                alert.setContentText(MainApp.bundle.getString("alert.text.warn.loaderr"));
                alert.getButtonTypes().setAll(
                        new ButtonType(MainApp.bundle.getString("alert.OK"), ButtonBar.ButtonData.OK_DONE));
                break;
            }
            case INVALID_FIELDS:{

                alert = new Alert(Alert.AlertType.ERROR);
                alert.initOwner(owner);
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image("resources/ico.png"));
                alert.setTitle(MainApp.bundle.getString("alert.text.invalidfield"));
                alert.setHeaderText(MainApp.bundle.getString("alert.text.conf.invalidfield"));
                alert.setContentText(args[0]);
                alert.getButtonTypes().setAll(
                        new ButtonType(MainApp.bundle.getString("alert.OK"), ButtonBar.ButtonData.OK_DONE));
                break;
            }

            case UPDATE_EDGE:{

                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.initOwner(owner);
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image("resources/ico.png"));
                alert.setTitle(MainApp.bundle.getString("alert.text.linkupd"));
                alert.setHeaderText(MainApp.bundle.getString("alert.text.conf.linkupd"));
                alert.setContentText(MainApp.bundle.getString("alert.text.warn.linkupd"));
                alert.getButtonTypes().setAll(
                        new ButtonType(MainApp.bundle.getString("alert.confirm"), ButtonBar.ButtonData.OK_DONE),
                        new ButtonType(MainApp.bundle.getString("alert.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE));
                break;
            }

            case ALGORITHM_EXEC:{

                alert = new Alert(Alert.AlertType.WARNING);
                alert.initOwner(owner);
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image("resources/ico.png"));
                alert.setTitle(MainApp.bundle.getString("alert.text.prim"));
                alert.setHeaderText(MainApp.bundle.getString("alert.text.conf.prim"));
                alert.setContentText(MainApp.bundle.getString("alert.text.warn.prim"));
                break;
            }

            case ABOUT:{

                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.initOwner(owner);
                ImageView imageView = new ImageView("resources/ico.png");
                imageView.setFitHeight(64);
                imageView.setFitWidth(64);
                alert.setGraphic(imageView);
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image("resources/ico.png"));
                alert.setTitle(MainApp.bundle.getString("alert.text.about"));
                alert.setHeaderText(MainApp.bundle.getString("alert.text.conf.about"));
                alert.setContentText(MainApp.bundle.getString("alert.text.warn.about"));
                break;
            }

            default:{
                alert = new Alert(Alert.AlertType.ERROR);
                alert.initOwner(owner);
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image("resources/ico.png"));
                alert.setTitle("Unknown error");
                alert.setHeaderText("Oops...");
                break;
            }
        }
        alert.showAndWait();
        return alert;
    }
}

