package ivascape.view.main;

import ivascape.MainApp;
import ivascape.controller.Project;
import ivascape.model.Company;
import ivascape.view.serve.MyAlerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static ivascape.view.serve.MyAlerts.getAlert;

public class LinksViewController {

    private MainWindowController MWController;
    private GraphViewController GVController;
    private Project project = Project.getInstance();

    void setGVController(GraphViewController GVController) {
        this.GVController = GVController;
    }

    void setMWController(MainWindowController MWController) {

        this.MWController = MWController;
    }

    private String expPaneMemory;

    @FXML
    Accordion table;

    public LinksViewController(){
    }

    private List<TitledPane> getTableViewItems(){

        List<TitledPane> list = new ArrayList<>();

        try {

            Iterator<Company> iterator = project.getGraph().getVertexIterator();

            while (iterator.hasNext()){

                FXMLLoader loader = new FXMLLoader(
                        MainApp.class.getResource("view/main/LinksViewItem.fxml"),
                        MainApp.bundle);

                TitledPane titledPane = loader.load();

                LinksViewItemController TVIController = loader.getController();

                Company iCompany = iterator.next();

                TVIController.setCompany(iCompany);

                List<VBox> cells = new ArrayList<>();

                TVIController.setMWController(MWController);

                TVIController.setGVController(GVController);

                Iterator<Company> jIterator = project.getGraph().getVertexIterator();

                while (jIterator.hasNext()){

                    Company jCompany = jIterator.next();

                    if (project.getGraph().getEdge(iCompany,jCompany) != null){

                        FXMLLoader anotherLoader = new FXMLLoader(
                                MainApp.class.getResource("view/main/LinksViewCell.fxml"),
                                MainApp.bundle);

                        VBox cell = anotherLoader.load();
                        LinksViewCellController TVCController = anotherLoader.getController();

                        TVCController.setLink(project.getGraph().getEdge(iCompany,jCompany));

                        TVCController.setMWController(MWController);

                        TVCController.setGVController(GVController);

                        cells.add(cell);
                    }
                }
                TVIController.setCells(cells);
                list.add(titledPane);
            }

        } catch (IOException e){

            getAlert(MyAlerts.MyAlertType.UNKNOWN, MWController.getMainStage());
            e.printStackTrace();
        }

        return list;
    }

    public void reloadView(){

        List<TitledPane> items =  getTableViewItems();

        if (items!=null) {

            for (TitledPane pane:table.getPanes()
                    ) {

                if (pane.isExpanded())

                    expPaneMemory = pane.getText();
            }

            table.getPanes().clear();
            table.getPanes().addAll(items);

            for (TitledPane pane:table.getPanes()
                    ) {

                if (pane.getText().equals(expPaneMemory))
                    pane.setExpanded(true);
            }
        }
    }
}
