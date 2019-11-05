package backsoft.ivascape.viewcontrol;

import backsoft.ivascape.handler.Loader;
import backsoft.ivascape.handler.Preferences;
import backsoft.ivascape.model.Company;
import backsoft.ivascape.model.Project;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.List;

public class LinksViewItemController {

    public Label titleLabel;
    public Label promptAmountLabel;
    public Label amountLabel;
    public VBox cellsBox;
    public Label promptTitleLabel;

    private Company company;
    private final Project project = Project.get();

    @FXML
    private Button addButton;

    @FXML
    private void initialize(){
        addButton.setDisable(project.companiesAmountProperty().getValue() < 2);
        addButton.setText(Preferences.get().getStringFromBundle("button.addlink"));
        ImageView view = new ImageView(Loader.loadImageResource("add"));
        view.setFitWidth(16);
        view.setFitHeight(16);
        addButton.setGraphic(view);
        promptAmountLabel.setText(Preferences.get().getStringFromBundle("bottombar.lnkamt"));
        promptTitleLabel.setText(Preferences.get().getStringFromBundle("tabletext.title"));
    }
    @FXML
    private void handleAdd(){ Loader.loadDialogEditLink(company.hashCode()); }

    void setItem(Company company, List<VBox> cells){

        this.company = company;
        titleLabel.setText(company.getTitle());
        amountLabel.setText(Integer.toString(cells.size()));
        cellsBox.getChildren().clear();
        cellsBox.getChildren().addAll(cells);
    }
}
