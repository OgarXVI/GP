package cz.ogarxvi.view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class FXMLController implements Initializable {
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void CloseFromMenuItem(ActionEvent event) {
        //CLOSE APPLICATION, BUT NOT LIKE THIS!! TODO:
        System.exit(0);
    }
}
