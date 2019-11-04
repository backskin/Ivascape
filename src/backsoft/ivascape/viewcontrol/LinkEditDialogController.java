package backsoft.ivascape.viewcontrol;

import backsoft.ivascape.handler.AlertHandler;
import backsoft.ivascape.handler.Preferences;
import backsoft.ivascape.model.Company;
import backsoft.ivascape.model.Project;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;

import java.util.List;

import static backsoft.ivascape.handler.AlertHandler.AlertType.FIELDS_ISSUE;

public class LinkEditDialogController {

    private double price = .0;
    private boolean confirmed = false;
    private final Project project = Project.get();

    @FXML
    TextField firstField;
    @FXML
    TextField secondField;
    @FXML
    TextField priceField;

    private Stage dialogStage;

    public LinkEditDialogController(){}

    @FXML
    private void initialize(){

        if (!firstField.getText().isEmpty())
            firstField.requestFocus();
        else
            secondField.requestFocus();
    }

    public void setDialogStage(Stage dialogStage) {

        this.dialogStage = dialogStage;
    }

    public void setFields(Integer... hashes) {

        Company com1, com2;

        if (hashes == null || hashes.length < 2){

            List<String> namesList = project.getCompaniesTitlesList();
            if (hashes != null && hashes.length > 0){
                com1 = project.getCompany(hashes[0]);
                namesList.remove(com1.getTitle());
                firstField.setText(com1.getTitle());
                firstField.setDisable(true);
            } else {
                TextFields.bindAutoCompletion(firstField, namesList);
            }
            TextFields.bindAutoCompletion(secondField, namesList);
        } else {
            com1 = project.getCompany(hashes[0]);
            com2 = project.getCompany(hashes[1]);
            firstField.setText(com1.getTitle());
            firstField.setDisable(true);
            secondField.setText(com2.getTitle());
            secondField.setDisable(true);
            priceField.setText(project.getLink(com1, com2).getPrice().toString());
        }
    }

    public boolean isConfirmed() { return confirmed; }

    @FXML
    private void handleOK() {

        String errorMessage = "";
        try {
            try {
                price = Double.parseDouble(priceField.getText());
                if (price <= 0.0)
                    throw new NumberFormatException();

            } catch (NumberFormatException e) {
                errorMessage +="\n"+Preferences.get().getStringFromBundle("error.wrongmoney");
            }
            Company one = project.getCompany(firstField.getText());
            Company another = project.getCompany(secondField.getText());

            if (one == null || another == null)
                errorMessage += "\n"+Preferences.get().getStringFromBundle("error.wrongcomname");
            if (firstField.getText().equals(secondField.getText()))
                errorMessage += "\n"+Preferences.get().getStringFromBundle("error.reflex");

            if (errorMessage.length() > 0) {
                throw new Exception(errorMessage);
            }

            project.add(one, another, price);
            confirmed = true;
            dialogStage.close();

        } catch (Exception e) {
            AlertHandler.makeAlert(FIELDS_ISSUE).setOwner(dialogStage).customContent(e.getMessage()).show();
        }
    }

    @FXML
    private void handleCancel(){ dialogStage.close(); }
}