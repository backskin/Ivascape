package ivascape.view.main;

import ivascape.MainApp;
import ivascape.controller.IvascapeProject;
import ivascape.model.Link;
import ivascape.view.serve.LinkEditDialogController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

import static ivascape.view.serve.MyAlerts.*;

public class LinksViewCellController {

    private MainWindowController MWController;

    private GraphViewController GVController;

    public void setGVController(GraphViewController GVController) {
        this.GVController = GVController;
    }

    public void setMWController(MainWindowController MWController) {

        this.MWController = MWController;
    }

    private Link link;

    @FXML
    private Label name;
    @FXML
    private Label price;

    public LinksViewCellController(){

    }

    public void setLink(Link link){

        this.link = link;

        reloadView();
    }

    private void reloadView(){

        if (link!=null){

            name.setText(link.getTwo().getTitle());
            price.setText(Double.toString(Math.round(link.getPrice()*100)/100.0));

        } else {

            name.setText("");
            price.setText("");
        }
    }

    @FXML
    private void initialize(){

        reloadView();
    }
    @FXML
    private void handleEdit(){

        editLink(link);
        reloadView();
    }

    @FXML
    private void handleDelete(){

        if (getAlert(MyAlertType.DELETE_CONFIRM, MWController.getMainStage())
                .getResult().getButtonData() == ButtonBar.ButtonData.OK_DONE){

            GVController.delEdge(link);
            IvascapeProject.delLink(link);

            MWController.reloadTV();
           //GVController.reloadView();
        }
    }

    private void editLink(Link editingLink){

        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/serve/LinkEditDialog.fxml"), MainApp.bundle);

            VBox editDialog = loader.load();

            LinkEditDialogController LEDController = loader.getController();
            Stage dialogStage = new Stage();
            dialogStage.setTitle(MainApp.bundle.getString("edittabs.header.editlink"));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setResizable(false);
            dialogStage.initOwner(MWController.getMainStage());
            dialogStage.getIcons().add(new Image("resources/ico.png"));
            Scene scene = new Scene(editDialog);
            dialogStage.setScene(scene);

            LEDController.setDialogStage(dialogStage);

            LEDController.setLink(editingLink);

            dialogStage.setResizable(false);

            dialogStage.showAndWait();

            if (LEDController.isOkClicked())

                GVController.editEdge(LEDController.getEditLink());

            MWController.reloadTV();
            GVController.reloadView();

        } catch (IOException e){

            getAlert(MyAlertType.UNKNOWN, MWController.getMainStage());
            e.printStackTrace();
        }
    }
}
