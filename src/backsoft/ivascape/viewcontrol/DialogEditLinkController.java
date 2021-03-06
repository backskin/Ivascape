package backsoft.ivascape.viewcontrol;

import backsoft.ivascape.handler.AlertHandler;
import backsoft.ivascape.handler.Loader;
import backsoft.ivascape.handler.Preferences;
import backsoft.ivascape.model.Project;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static backsoft.ivascape.handler.AlertHandler.AlertType.FIELDS_ISSUE;

public class DialogEditLinkController {

    private boolean confirmed = false;
    private final Project project = Project.get();
    private Map<String, String> map;
    @FXML
    TextField firstField;
    @FXML
    TextField secondField;
    @FXML
    TextField priceField;

    private Stage dialogStage;

    public DialogEditLinkController(){}

    @FXML
    private void initialize(){

        if (!firstField.getText().isEmpty())
            firstField.requestFocus();
        else
            secondField.requestFocus();
        map = new HashMap<>();
        project.getIteratorOfCompanies().forEachRemaining(company -> map.put(company.getTitle(),company.getID()));
    }

    public void setStage(Stage stage) {

        this.dialogStage = stage;
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(Loader.getMainStage());
    }

    public void setFields(String... companies) {

        dialogStage.setTitle(Preferences.get().getStringFromBundle(companies == null ?
                "edittabs.header.newlink" : "edittabs.header.editlink"));

        if (companies == null || companies.length < 2){

            List<String> namesList = new ArrayList<>();
            project.getIteratorOfCompanies().forEachRemaining(company -> namesList.add(company.getTitle()));
            if (companies != null && companies.length > 0){
                String firstCompanyName = project.findCompanyByID(companies[0]).getTitle();
                namesList.remove(firstCompanyName);
                firstField.setText(firstCompanyName);
                firstField.setDisable(true);
            } else {
                TextFields.bindAutoCompletion(firstField, namesList);
            }
            TextFields.bindAutoCompletion(secondField, namesList);
        } else {
            firstField.setText(project.findCompanyByID(companies[0]).getTitle());
            firstField.setDisable(true);
            secondField.setText(project.findCompanyByID(companies[1]).getTitle());
            secondField.setDisable(true);
            priceField.setText(project.getLink(project.findCompanyByID(companies[0]), project.findCompanyByID(companies[1])).getPrice().toString());
        }
    }

    public boolean isConfirmed() { return confirmed; }

    @FXML
    private void handleOK() {

        try {
            double price = Double.parseDouble(priceField.getText());
            if (price <= 0.0)
                throw new NumberFormatException("\n"+Preferences.get()
                        .getStringFromBundle("error.wrongmoney"));
            if (firstField.getText().equals(secondField.getText()))
                throw new Exception("\n"+Preferences.get().getStringFromBundle("error.reflex"));
            if (null == project.findCompanyByTitle(firstField.getText())
                    || null == project.findCompanyByTitle(secondField.getText()))
                throw new Exception("\n"+Preferences.get().getStringFromBundle("error.wrongcomname"));

            ViewUpdater.current().getGVController().normalScale();
            project.add(map.get(firstField.getText()), map.get(secondField.getText()), price);
            ViewUpdater.current().getGVController().restoreScale();
            confirmed = true;
            dialogStage.close();

        } catch (Exception e) {
            AlertHandler.makeAlert(FIELDS_ISSUE).setOwner(dialogStage).customContent(e.toString()).show();
        }
    }

    @FXML
    private void handleCancel(){ dialogStage.close(); }
}