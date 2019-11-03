package backsoft.ivascape.viewcontrol;

import backsoft.ivascape.handler.Loader;
import backsoft.ivascape.logic.Pair;
import backsoft.ivascape.model.Company;
import backsoft.ivascape.model.Link;
import backsoft.ivascape.model.Project;
import javafx.scene.Parent;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LinksViewController{

    void updateView(){

        table.getChildren().clear();
        table.getChildren().addAll(getTableViewItems());
    }

    @FXML
    private VBox table;

    @FXML
    private void initialize(){
    }

    private List<VBox> getTableViewItems(){

        List<VBox> list = new ArrayList<>();

        for (Iterator<Company> itCom = Project.get().getIteratorOfCompanies(); itCom.hasNext(); ) {
            Pair<Parent, LinksViewItemController> fxml = Loader.loadFXML("LinksViewItem");
            LinksViewItemController LVIController = fxml.getTwo();
            Company nextCom = itCom.next();
            LVIController.setCompany(nextCom);
            List<VBox> cells = new ArrayList<>();

            for (Iterator<Link> itLink = Project.get().getIteratorOfLinksOf(nextCom); itLink.hasNext(); ) {

                Pair<Parent, LinksViewCellController> fxmlCell = Loader.loadFXML("LinksViewCell");
                fxmlCell.getTwo().setFieldsFor(itLink.next());
                cells.add((VBox) fxmlCell.getOne());
            }

            LVIController.setCells(cells);
            list.add((VBox) fxml.getOne());
        }

        return list;
    }
}
