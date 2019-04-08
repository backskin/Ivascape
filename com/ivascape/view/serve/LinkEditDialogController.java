package ivascape.view.serve;

import ivascape.MainApp;
import ivascape.model.Company;
import ivascape.controller.IvascapeProject;
import ivascape.model.Link;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;

import java.util.ArrayList;

import static ivascape.view.serve.MyAlerts.MyAlertType.*;
import static ivascape.view.serve.MyAlerts.getAlert;

public class LinkEditDialogController {

    private boolean creating = true;

    private boolean okClicked = false;

    private boolean updatePrice = false;

    private Double editPrice = -1.0;

    private Company fstCompany = null;

    private Company secCompany = null;

    private Link editLink = null;

    public Link getEditLink() {
        return editLink;
    }

    @FXML
    TextField neighbourOneField;

    @FXML
    TextField neighbourTwoField;

    @FXML
    TextField priceField;

    private Stage dialogStage;


    public LinkEditDialogController(){}

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

    public void setCompanies(ArrayList<String> companies) {

        if (creating)
            TextFields.bindAutoCompletion(neighbourOneField, companies);

        TextFields.bindAutoCompletion(neighbourTwoField, companies);

    }

    public void setCompanyOne(Company companyOne) {

        fstCompany = companyOne;
        neighbourOneField.setText(companyOne.getTitle());
        neighbourOneField.setDisable(true);
        creating = false;
    }

    private void reloadView(){

        if (editLink != null){
            neighbourOneField.setText(editLink.getOne().getTitle());
            neighbourTwoField.setText(editLink.getTwo().getTitle());
            priceField.setText(editLink.getPrice().toString());

        } else {
            neighbourTwoField.setText("");
        }
    }

    public void setLink(Link link){

        editLink = link;
        secCompany = editLink.getOne();
        neighbourOneField.setDisable(true);
        neighbourTwoField.setDisable(true);
        priceField.requestFocus();

        reloadView();
    }

    private boolean isInputValid(){

        String errorMessage = "";

        try{
            editPrice = Double.parseDouble(priceField.getText());

            if (editPrice <= 0.0)
                errorMessage += MainApp.bundle.getString("error.negmoney") + "\n";

        } catch (NumberFormatException e){
            errorMessage += MainApp.bundle.getString("error.wrongmoney") + "\n";
        }

        if (!neighbourOneField.isDisabled()) {

            fstCompany = IvascapeProject.getCompany(neighbourOneField.getText());

            if (fstCompany == null || fstCompany.getTitle().equals(neighbourTwoField.getText()))

                errorMessage +=  MainApp.bundle.getString("error.wrongnb") + "\n";
        }

        if (!neighbourTwoField.isDisabled()) {

            secCompany = IvascapeProject.getCompany(neighbourTwoField.getText());

            if (secCompany == null || secCompany.getTitle().equals(neighbourOneField.getText()))

                errorMessage +=  MainApp.bundle.getString("error.wrongnb") + "\n";
        }

        if (errorMessage.length() == 0) {

            return true;

        } else {
            getAlert(INVALID_FIELDS,dialogStage,errorMessage);
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
                if (editPrice.equals(editLink.getPrice())){

                    dialogStage.close();

                } else {

                    editLink.setPrice(editPrice);
                    IvascapeProject.getLink(editLink.getTwo(),editLink.getOne()).setPrice(editPrice);
                    IvascapeProject.setSaved(false);
                    okClicked = true;
                    dialogStage.close();
                }
            } else {

                editLink = IvascapeProject.getLink(secCompany, IvascapeProject.getCompany(neighbourOneField.getText()));

                if (editLink != null) {

                    if (!editPrice.equals(editLink.getPrice())) {

                        if (getAlert(UPDATE_EDGE,dialogStage).getResult().getButtonData() == ButtonBar.ButtonData.OK_DONE) {

                            editLink.setPrice(editPrice);
                            IvascapeProject.getLink(editLink.getTwo(),editLink.getOne()).setPrice(editPrice);
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

                    editLink = IvascapeProject
                            .addLink(fstCompany,
                                    secCompany,
                                    editPrice);
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
