package ivascape.view.serve;

import ivascape.MainApp;
import ivascape.model.Company;
import ivascape.controller.Project;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;

import static ivascape.view.serve.MyAlerts.getAlert;

public class CompanyEditDialogController {

    private Project project = Project.getInstance();
    private boolean okClicked = false;
    public boolean isOkClicked() {
        return okClicked;
    }


    private Company editCompany = null;

    public void setEditCompany(Company company){

        editCompany = company;
        titleField.setText(editCompany.getTitle());
        capitalField.setText(Double.toString(editCompany.getMoneyCapital()));
        addressField.setText(editCompany.getAddress());
        datePicker.setValue(editCompany.getDate());

        capitalField.requestFocus();
    }

    public Company getEditCompany() {
        return editCompany;
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

    private double editCapital;

    public CompanyEditDialogController(){}

    @FXML
    private void initialize(){

        capitalField.setText("0.0");
        datePicker.setValue(LocalDate.now());
    }

    private boolean isInputValid(){

        String errorMessage = "";

        try{
            editCapital= Double.parseDouble(capitalField.getText());

            if (editCapital < 0.0)
                errorMessage += MainApp.bundle.getString("error.negmoney") + "\n";

        } catch (NumberFormatException e){
            errorMessage += MainApp.bundle.getString("error.wrongmoney") + "\n";
        }

        if (addressField.getText().isEmpty())
             errorMessage +=  MainApp.bundle.getString("error.emptyaddress") + "\n";

        if (titleField.getText().isEmpty())
            errorMessage += MainApp.bundle.getString("error.emptyname") + "\n";

        if (editCompany == null && !titleField.getText().isEmpty()
                && project.getCompany(titleField.getText()) != null)
                errorMessage +=  MainApp.bundle.getString("error.dubc")+ "\n";

        if (errorMessage.length() == 0) {
            return true;

        } else {

            getAlert(MyAlerts.MyAlertType.INVALID_FIELDS,dialogStage,errorMessage);
            return false;
        }
    }

    @FXML
    private void handleOK(){

        if (titleField.getText().isEmpty()){
            return;
        }

        if (isInputValid()) {
            if (editCompany == null) {

                editCompany = new Company(
                        titleField.getText(),
                        addressField.getText(),
                        editCapital,
                        datePicker.getValue()
                        );

                project.getGraph().addVertex(editCompany);
                okClicked = true;
            } else {

                if (!editCompany.getTitle().equals(titleField.getText())
                        || !editCompany.getAddress().equals(addressField.getText())
                        || editCompany.getMoneyCapital() != editCapital
                        || editCompany.getDate() != datePicker.getValue()) {

                    editCompany.setTitle(titleField.getText());
                    editCompany.setAddress(addressField.getText());
                    editCompany.setMoneyCapital(editCapital);
                    editCompany.setDate(datePicker.getValue());

                    project.setSaved(false);

                    okClicked = true;
                }
            }
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel(){

        dialogStage.close();
    }
}
