package backsoft.ivascape.viewcontrol;

import backsoft.ivascape.handler.AlertHandler;
import backsoft.ivascape.handler.Preferences;
import backsoft.ivascape.model.Company;
import backsoft.ivascape.model.Project;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;

import static backsoft.ivascape.handler.AlertHandler.AlertType.FIELDS_ISSUE;

public class CompanyEditDialogController {
    
    @FXML
    private TextField titleField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField capitalField;
    @FXML
    private DatePicker datePicker;
    
    private Stage dialogStage;

    private Company editCompany = null;
    private final Project project = Project.get();
    private boolean okClicked = false;
    private final Preferences prefs = Preferences.get();
    
    public boolean isOkClicked() {
        return okClicked;
    }

    public Company getEditCompany() {
        return editCompany;
    }

    public void setEditCompany(Integer comHash){

        if (comHash == 0) return;

        editCompany = project.getCompany(comHash);
        titleField.setText(editCompany.getTitle());
        capitalField.setText(Double.toString(editCompany.getMoneyCapital()));
        addressField.setText(editCompany.getAddress());
        datePicker.setValue(editCompany.getDate());

        capitalField.requestFocus();
    }
    
    public void setDialogStage(Stage dialogStage) {

        this.dialogStage = dialogStage;
        titleField.requestFocus();
    }
    
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
                errorMessage += prefs.getValueFromBundle("error.negmoney") + "\n";

        } catch (NumberFormatException e){
            errorMessage += prefs.getValueFromBundle("error.wrongmoney") + "\n";
        }
        if (addressField.getText().isEmpty())
             errorMessage +=  prefs.getValueFromBundle("error.emptyaddress") + "\n";

        if (titleField.getText().isEmpty())
            errorMessage += prefs.getValueFromBundle("error.emptyname") + "\n";

        if (editCompany == null && !titleField.getText().isEmpty()
                && project.getCompany(titleField.getText()) != null)
                errorMessage +=  prefs.getValueFromBundle("error.dubc")+ "\n";

        if (errorMessage.length() == 0) {
            return true;

        } else {
            AlertHandler.makeAlert(FIELDS_ISSUE).setOwner(dialogStage).customContent(errorMessage).show();
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
