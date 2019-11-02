package backsoft.ivascape.viewcontrol;

import backsoft.ivascape.handler.AlertHandler;
import backsoft.ivascape.handler.Loader;
import backsoft.ivascape.model.Link;
import backsoft.ivascape.model.Project;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import static backsoft.ivascape.handler.AlertHandler.AlertType.DELETE_CONFIRM;
public class LinksViewCellController {

    @FXML
    private Label name;
    @FXML
    private Label price;

    private Link link;

    void setFieldsFor(Link link) {

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

        if (AlertHandler.makeAlert(DELETE_CONFIRM).setOwner(Loader.getMainStage()).showAndGetResult()){
            Project.get().remove(link);
            ViewUpdater.current().updateLinksView().updateGraphView();
        }
    }
}
