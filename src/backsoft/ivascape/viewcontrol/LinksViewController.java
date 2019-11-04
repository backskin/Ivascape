package backsoft.ivascape.viewcontrol;

import backsoft.ivascape.handler.Loader;
import backsoft.ivascape.logic.Pair;
import backsoft.ivascape.model.Company;
import backsoft.ivascape.model.Link;
import backsoft.ivascape.model.Project;
import javafx.scene.Parent;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LinksViewController{

    private String tempString = "Empty project. Nothing to see here. " +
            "Load file or Go to 'Companies Tab' and add some.";
    private StackPane tempStackPane = new StackPane(new Text(tempString));
    private Project project = Project.get();

    @FXML
    private VBox table;
    @FXML
    private void initialize(){
        if (project.isEmpty())
            table.getChildren().add(tempStackPane);
        else updateView();
        project.companiesAmountProperty().addListener(observable -> {
            if (project.isEmpty()) table.getChildren().add(tempStackPane);
        });
    }

    void updateView(){

        table.getChildren().clear();
        table.getChildren().addAll(getTableViewItems());
    }

    private List<VBox> getTableViewItems() {

        return new ArrayList<>() {{
            for (Iterator<Company> itCom = project.getIteratorOfCompanies(); itCom.hasNext(); ) {

                Pair<Parent, LinksViewItemController> fxml = Loader.loadFXML("LinksViewItem");
                Company nextCom = itCom.next();
                List<VBox> cells = new ArrayList<>();

                for (Iterator<Link> itLink = project.getIteratorOfLinksOf(nextCom); itLink.hasNext(); ) {

                    Pair<Parent, LinksViewCellController> fxmlCell = Loader.loadFXML("LinksViewCell");
                    fxmlCell.getTwo().setFieldsFor(itLink.next());
                    cells.add((VBox) fxmlCell.getOne());
                }

                fxml.getTwo().setItem(nextCom, cells);
                add((VBox) fxml.getOne());
            }
        }};
    }
}
