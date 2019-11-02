package backsoft.ivascape.viewcontrol;

import backsoft.ivascape.handler.Loader;
import backsoft.ivascape.logic.Pair;
import backsoft.ivascape.model.Company;
import backsoft.ivascape.model.Link;
import backsoft.ivascape.model.Project;
import javafx.scene.Parent;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LinksViewController implements ViewController{

    public void updateView(){

        table.getPanes().clear();
        table.getPanes().addAll(getTableViewItems());
    }

    @FXML
    private Accordion table;

    private List<TitledPane> getTableViewItems(){

        List<TitledPane> list = new ArrayList<>();

        for (Iterator<Company> itCom = Project.get().getIteratorOfCompanies(); itCom.hasNext(); ) {

            Pair<Parent, LinksViewItemController> fxml = Loader.loadFXML("LinksViewItem");
            LinksViewItemController LVIController = fxml.getTwo();
            Company nextCom = itCom.next();
            LVIController.setCompany(nextCom);
            List<VBox> cells = new ArrayList<>();

            for (Iterator<Link> itLink = Project.get().getIteratorOfLinks(nextCom); itLink.hasNext(); ) {

                Pair<Parent, LinksViewCellController> fxmlCell = Loader.loadFXML("LinksViewCell");
                fxmlCell.getTwo().setFieldsFor(itLink.next());
                cells.add((VBox) fxmlCell.getOne());
            }

            LVIController.setCells(cells);
            list.add((TitledPane) fxml.getOne());
        }

        return list;
    }
}
