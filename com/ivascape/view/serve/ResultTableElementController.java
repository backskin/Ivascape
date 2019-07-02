package ivascape.view.serve;

import ivascape.model.Link;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ResultTableElementController {

    @FXML
    private Label companyOne;

    @FXML
    private Label companyTwo;

    @FXML
    private Label linkPrice;


    void setLink(Link link){

        this.companyOne.setText(link.one().getTitle());
        this.companyTwo.setText(link.another().getTitle());
        this.linkPrice.setText(link.getPrice().toString());
    }

    @FXML
    private void initialize(){

    }
}