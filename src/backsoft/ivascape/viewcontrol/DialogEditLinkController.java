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

import java.util.List;

import static backsoft.ivascape.handler.AlertHandler.AlertType.FIELDS_ISSUE;

public class DialogEditLinkController {

    private boolean confirmed = false;
    private final Project project = Project.get();

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
    }

    public void setStage(Stage stage) {

        this.dialogStage = stage;
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(Loader.getMainStage());
    }

    public void setFields(int... input) {
        dialogStage.setTitle(Preferences.get().getStringFromBundle(input == null ?
                "edittabs.header.newlink" : "edittabs.header.editlink"));

        if (input == null || input.length < 2){

            List<String> namesList = project.getCompaniesTitlesList();
            if (input != null && input.length > 0){
                String firstCompanyName = project.getCompany(input[0]).getTitle();
                namesList.remove(firstCompanyName);
                firstField.setText(firstCompanyName);
                firstField.setDisable(true);
            } else {
                TextFields.bindAutoCompletion(firstField, namesList);
            }
            TextFields.bindAutoCompletion(secondField, namesList);
        } else {
            firstField.setText(project.getCompany(input[0]).getTitle());
            firstField.setDisable(true);
            secondField.setText(project.getCompany(input[1]).getTitle());
            secondField.setDisable(true);
            priceField.setText(project.getLink(project.getCompany(input[0]), project.getCompany(input[1])).getPrice().toString());
        }
    }

    public boolean isConfirmed() { return confirmed; }

    @FXML
    private void handleOK() {

        try {
            double price = Double.parseDouble(priceField.getText());
            if (price <= 0.0)
                throw new NumberFormatException("\n"+Preferences.get().getStringFromBundle("error.wrongmoney"));
            if (firstField.getText().equals(secondField.getText()))
                throw new Exception("\n"+Preferences.get().getStringFromBundle("error.reflex"));
            if (null == project.getCompany(firstField.getText()) || null == project.getCompany(secondField.getText()))
                throw new Exception("\n"+Preferences.get().getStringFromBundle("error.wrongcomname"));

            project.add(firstField.getText(), secondField.getText(), price);
            confirmed = true;
            dialogStage.close();

        } catch (Exception e) {
            AlertHandler.makeAlert(FIELDS_ISSUE).setOwner(dialogStage).customContent(e.getMessage()).show();
        }
    }

    @FXML
    private void handleCancel(){ dialogStage.close(); }
}