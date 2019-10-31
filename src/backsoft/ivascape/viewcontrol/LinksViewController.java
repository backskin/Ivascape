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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LinksViewController {

    private Project project = Project.get();

    @FXML
    private Accordion table;

    private List<TitledPane> getTableViewItems(){

        List<TitledPane> list = new ArrayList<>();

        for (Iterator<Company> itCom = project.getIteratorOfComs(); itCom.hasNext(); ) {

            Pair<Parent, LinksViewItemController> fxml = Loader.loadFXML("LinksViewItem");
            LinksViewItemController LVIController = fxml.getTwo();
            Company nextCom = itCom.next();
            LVIController.setCompany(nextCom);
            List<VBox> cells = new ArrayList<>();

            for (Iterator<Link> itLink = project.getIteratorOfLinks(nextCom); itLink.hasNext(); ) {

                Pair<Parent, LinksViewCellController> fxml2 = Loader.loadFXML("LinksViewCell");
                LinksViewCellController TVCController = fxml2.getTwo();
                Link nextLink = itLink.next();
                TVCController.put(nextLink);

                cells.add((VBox) fxml.getOne());
            }

            LVIController.setCells(cells);
            list.add((TitledPane) fxml.getOne());
        }

        return list;
    }

    void updateView(){

        table.getPanes().clear();
        table.getPanes().addAll(getTableViewItems());
    }
}
