package ivascape.view.serve;

import ivascape.MainApp;
import ivascape.controller.Project;
import ivascape.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;

import java.util.List;

import static ivascape.view.serve.MyAlerts.MyAlertType.*;
import static ivascape.view.serve.MyAlerts.getAlert;

public class LinkEditDialogController {

    private boolean creating = true;
    private boolean okClicked = false;
    private boolean updatePrice = false;
    private Double price = -1.0;
    private Company first = null;
    private Company second = null;
    private Link editLink = null;

    private Project project = Project.getInstance();

    @FXML
    TextField neighbourOneField;

    @FXML
    TextField neighbourTwoField;

    @FXML
    TextField priceField;

    private Stage dialogStage;

    public LinkEditDialogController(){}

    public Link getEditLink() {
        return editLink;
    }

    @FXML
    private void initialize(){

        if (creating)
            neighbourOneField.requestFocus();
        else
            neighbourTwoField.requestFocus();
    }


    public void setDialogStage(Stage dialogStage) {

        this.dialogStage = dialogStage;
    }

    public void setList(List<String> list, String... removed) {

        for (String s: removed
             ) {
            list.remove(s);
        }
        if (creating) TextFields.bindAutoCompletion(neighbourOneField, list);
        TextFields.bindAutoCompletion(neighbourTwoField, list);
    }

    public void setCompanyOne(Company companyOne) {

        first = companyOne;
        neighbourOneField.setText(companyOne.getTitle());
        neighbourOneField.setDisable(true);
        creating = false;
    }

    private void reloadView(){

        if (editLink != null){
            neighbourOneField.setText(editLink.one().getTitle());
            neighbourTwoField.setText(editLink.another().getTitle());
            priceField.setText(editLink.getPrice().toString());

        } else {
            neighbourTwoField.setText("");
        }
    }

    public void setLink(Link link){

        editLink = link;
        second = editLink.one();
        neighbourOneField.setDisable(true);
        neighbourTwoField.setDisable(true);
        priceField.requestFocus();

        reloadView();
    }

    private boolean isInputValid(){

        String errorMessage = "";

        try {
            price = Double.parseDouble(priceField.getText());

            if (price <= 0.0)
                errorMessage += MainApp.bundle.getString("error.negmoney") + "\n";

        } catch (NumberFormatException e){
            errorMessage += MainApp.bundle.getString("error.wrongmoney") + "\n";
        }

        if (!neighbourOneField.isDisabled()) {

            first = project.getCompany(neighbourOneField.getText());

            if (first == null || first.getTitle().equals(neighbourTwoField.getText()))

                errorMessage +=  MainApp.bundle.getString("error.wrongnb") + "\n";
        }

        if (!neighbourTwoField.isDisabled()) {

            second = project.getCompany(neighbourTwoField.getText());

            if (second == null || second.getTitle().equals(neighbourOneField.getText()))

                errorMessage +=  MainApp.bundle.getString("error.wrongnb") + "\n";
        }

        if (errorMessage.length() == 0) {

            return true;

        } else {
            getAlert(INVALID_FIELDS, dialogStage, errorMessage);
            return false;
        }
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    public boolean isUpdatePrice() {
        return updatePrice;
    }

    @FXML
    private void handleOK(){

        if (neighbourOneField.getText().isEmpty() && neighbourTwoField.getText().isEmpty())
            return;

        if (neighbourTwoField.getText().isEmpty()){
            neighbourTwoField.requestFocus();
            return;
        }

        if (priceField.getText().isEmpty()) {
            priceField.requestFocus();
            return;
        }

        if (isInputValid()) {

            if (editLink != null) {
                if (price.equals(editLink.getPrice())){

                    dialogStage.close();

                } else {

                    editLink.setPrice(price);
                    project.modifyLink(editLink.one(),editLink.another(), price);
                    okClicked = true;

                    dialogStage.close();
                }
            } else {

                editLink = project.getLink(second, project.getCompany(neighbourOneField.getText()));

                if (editLink != null) {

                    if (!price.equals(editLink.getPrice())) {

                        if (getAlert(UPDATE_EDGE,dialogStage).getResult().getButtonData() == ButtonBar.ButtonData.OK_DONE) {

                            editLink.setPrice(price);
                            project.getLink(editLink.another(),editLink.one()).setPrice(price);
                            okClicked = true;
                            updatePrice = true;

                            dialogStage.close();

                        } else {
                            editLink = null;
                        }
                    } else {
                        dialogStage.close();
                    }
                } else {

                    editLink = project.addLink(first, second, price);
                    okClicked = true;
                    dialogStage.close();
                }
            }
        }
    }

    @FXML
    private void handleCancel(){

        dialogStage.close();
    }
}
