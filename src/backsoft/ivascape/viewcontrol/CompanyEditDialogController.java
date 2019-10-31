package backsoft.ivascape.viewcontrol;

import backsoft.ivascape.handler.Preferences;
import backsoft.ivascape.model.Company;
import backsoft.ivascape.model.Project;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;

import static backsoft.ivascape.viewcontrol.MyAlerts.getAlert;

public class CompanyEditDialogController {

    private Project project = Project.get();
    private boolean okClicked = false;
    public boolean isOkClicked() {
        return okClicked;
    }

    public Company getEditCompany() {
        return editCompany;
    }

    private Company editCompany = null;

    public void setEditCompany(Integer comHash){

        if (comHash == 0) return;

        editCompany = project.getCompany(comHash);
        titleField.setText(editCompany.getTitle());
        capitalField.setText(Double.toString(editCompany.getMoneyCapital()));
        addressField.setText(editCompany.getAddress());
        datePicker.setValue(editCompany.getDate());

        capitalField.requestFocus();
    }

    private Stage dialogStage;

    public void setDialogStage(Stage dialogStage) {

        this.dialogStage = dialogStage;
        titleField.requestFocus();
    }

    @FXML
    TextField titleField;

    @FXML
    TextField addressField;

    @FXML
    TextField capitalField;

    @FXML
    private DatePicker datePicker;

    public CompanyEditDialogController(){}

    @FXML
    private void initialize(){

        capitalField.setText("0.0");
        datePicker.setValue(LocalDate.now());
    }

    private boolean isInputValid(){

        String errorMessage = "";
        
        try{
            double editCapital = Double.parseDouble(capitalField.getText());

            if (editCapital < 0.0)
                errorMessage += Preferences.getCurrent().getBundle().getString("error.negmoney") + "\n";

        } catch (NumberFormatException e){
            errorMessage += Preferences.getCurrent().getBundle().getString("error.wrongmoney") + "\n";
        }
        if (addressField.getText().isEmpty())
             errorMessage +=  Preferences.getCurrent().getBundle().getString("error.emptyaddress") + "\n";

        if (titleField.getText().isEmpty())
            errorMessage += Preferences.getCurrent().getBundle().getString("error.emptyname") + "\n";

        if (editCompany == null && !titleField.getText().isEmpty()
                && project.getCompany(titleField.getText()) != null)
                errorMessage +=  Preferences.getCurrent().getBundle().getString("error.dubc")+ "\n";

        if (errorMessage.length() == 0) {
            return true;

        } else {

            getAlert(MyAlerts.AlertType.INVALID_FIELDS, dialogStage, errorMessage);
            return false;
        }
    }

    @FXML
    private void handleOK() {

        if (titleField.getText().isEmpty()) {
            return;
        }

        if (isInputValid()) {

            Company tempCom = new Company(titleField.getText(),
                    addressField.getText(),
                    Double.parseDouble(capitalField.getText()),
                    datePicker.getValue());

            project.add(tempCom);
            okClicked = true;
        }

        dialogStage.close();
    }

    @FXML
    private void handleCancel(){

        dialogStage.close();
    }
}
