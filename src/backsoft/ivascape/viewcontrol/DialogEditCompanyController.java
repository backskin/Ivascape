package backsoft.ivascape.viewcontrol;

import backsoft.ivascape.handler.AlertHandler;
import backsoft.ivascape.handler.Loader;
import backsoft.ivascape.handler.Preferences;
import backsoft.ivascape.model.Company;
import backsoft.ivascape.model.Project;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;

import static backsoft.ivascape.handler.AlertHandler.AlertType.FIELDS_ISSUE;

public class DialogEditCompanyController {
    
    @FXML
    private TextField titleField;
    @FXML
    private TextArea addressArea;
    @FXML
    private TextField capitalField;
    @FXML
    private DatePicker datePicker;
    
    private Stage dialogStage;

    private Company editCompany = null;
    private final Project project = Project.get();
    private boolean confirmed = false;
    private final Preferences prefs = Preferences.get();
    
    public boolean isConfirmed() {
        return confirmed;
    }

    public Company getEditCompany() {
        return editCompany;
    }

    public void setFields(Object company){

        dialogStage.setTitle(prefs.getStringFromBundle(company == null ?
                "edittabs.header.newcmp" : "edittabs.header.editcmp"));

        if (company == null) return;

        editCompany = (Company) company;
        titleField.setText(editCompany.getTitle());
        capitalField.setText(Double.toString(editCompany.getMoney()));
        addressArea.setText(editCompany.getAddress());
        datePicker.setValue(editCompany.getDate());
        capitalField.requestFocus();
    }
    
    public void setStage(Stage stage) {

        this.dialogStage = stage;
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(Loader.getMainStage());
        titleField.requestFocus();
    }
    
    public DialogEditCompanyController(){}

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
                errorMessage += "\n" + prefs.getStringFromBundle("error.negmoney");

        } catch (NumberFormatException e){
            errorMessage += "\n" + prefs.getStringFromBundle("error.wrongmoney");
        }
        if (addressArea.getText().isEmpty()) addressArea.setText(
                prefs.getStringFromBundle("error.emptyaddress"));

        if (titleField.getText().isEmpty())
            errorMessage += "\n" + prefs.getStringFromBundle("error.emptyname");

        if (editCompany == null && !titleField.getText().isEmpty()
                && project.getCompany(titleField.getText()) != null)
                errorMessage +=  "\n" + prefs.getStringFromBundle("error.dubc");

        if (errorMessage.length() == 0) {
            return true;
        } else {
            AlertHandler.makeAlert(FIELDS_ISSUE).setOwner(dialogStage).customContent(errorMessage).show();
            return false;
        }
    }

    @FXML
    private void handleOK() {
        if (isInputValid()) {

            if (editCompany == null) {
                editCompany = Company.createCompany().setTitle(titleField.getText()).setAddress(
                        addressArea.getText()).setMoney(
                        Double.parseDouble(capitalField.getText())).setDate(datePicker.getValue());
                project.add(editCompany);
            } else {
                editCompany
                        .setDate(datePicker.getValue())
                        .setTitle(titleField.getText())
                        .setAddress(addressArea.getText())
                        .setMoney(Double.parseDouble(capitalField.getText()));
            }

            confirmed = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel(){

        dialogStage.close();
    }
}
