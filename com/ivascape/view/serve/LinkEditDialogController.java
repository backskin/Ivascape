package ivascape.view.serve;

import ivascape.MainApp;
import ivascape.models.Company;
import ivascape.models.Project;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;

import java.util.List;

import static ivascape.view.serve.MyAlerts.MyAlertType.*;
import static ivascape.view.serve.MyAlerts.getAlert;

public class LinkEditDialogController {

    private double price = .0;
    private boolean confirmed = false;
    private Project project = Project.getInstance();

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


    public void setFields(Company... coms) {

        TextFields.bindAutoCompletion(firstField, project.getCompaniesList());
        List<String> namesList = project.getCompaniesList();

        if (coms.length > 0){

            firstField.setText(coms[0].getTitle());
            namesList.remove(coms[0].getTitle());
            firstField.setDisable(true);
            TextFields.bindAutoCompletion(secondField, namesList);
        }
        if (coms.length > 1) {
            secondField.setText(coms[1].getTitle());
            secondField.setDisable(true);
            priceField.setText(project.getLink(coms[0],coms[1]).getPrice().toString());
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    @FXML
    private void handleOK() {

        String errorMessage = "";

        try {
            try {
                price = Double.parseDouble(priceField.getText());
                if (price <= 0.0) throw new NumberFormatException();

            } catch (NumberFormatException e) {
                errorMessage += MainApp.bundle.getString("error.wrongmoney") + "\n";
            }

            Company one = project.getCompany(firstField.getText());
            Company another = project.getCompany(secondField.getText());

            if (one == null || another == null || firstField.getText().equals(secondField.getText())) {

                errorMessage += MainApp.bundle.getString("error.wrongnb") + "\n";
            }

            if (errorMessage.length() > 0) {
                throw new Exception(errorMessage);
            }

            project.addLink(one, another, price);
            confirmed = true;
            dialogStage.close();

        } catch (Exception e) {
            getAlert(INVALID_FIELDS, dialogStage, e.getMessage());
        }
    }

    @FXML
    private void handleCancel(){ dialogStage.close(); }
}
