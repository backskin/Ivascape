package backsoft.ivascape.viewcontrol;

import backsoft.ivascape.handler.Loader;
import backsoft.ivascape.model.Link;
import backsoft.ivascape.model.Project;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import static backsoft.ivascape.viewcontrol.MyAlerts.AlertType.DELETE_CONFIRM;
import static backsoft.ivascape.viewcontrol.MyAlerts.getAlert;
import static javafx.scene.control.ButtonBar.ButtonData.OK_DONE;

public class LinksViewCellController {

    @FXML
    private Label name;
    @FXML
    private Label price;

    private Link link;

    void put(Link link) {

        this.link = link;
        name.setText(link.two().getTitle());
        price.setText(Double.toString(Math.round(link.getPrice() * 100) / 100.0));
    }

    @FXML
    private void handleEdit(){

        Loader.loadDialogEditLink(link.one().hashCode(), link.two().hashCode());
    }

    @FXML
    private void handleDelete(){

        if (getAlert(DELETE_CONFIRM, Loader.getMainStage())
                .getResult().getButtonData() == OK_DONE){

            Project.get().remove(link);
            ViewUpdater.current().updateLinksView().updateMapView();
        }
    }
}
