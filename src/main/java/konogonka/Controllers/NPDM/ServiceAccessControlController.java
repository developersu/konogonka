package konogonka.Controllers.NPDM;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import konogonka.Tools.NPDM.LCollectionEntry;

import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class ServiceAccessControlController implements Initializable {

    @FXML
    private VBox SACPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    public void resetTab(){
        SACPane.getChildren().clear();
    }

    public void populateFields(LinkedList<LCollectionEntry> collection){
        resetTab();

        for (LCollectionEntry entry : collection) {

            Label control = new Label(String.format("0x%02x", entry.getKey()));
            Label serviceName = new Label(entry.getValue());

            control.setPadding(new Insets(5.0, 5.0, 5.0, 5.0));
            serviceName.setPadding(new Insets(5.0, 5.0, 5.0, 5.0));

            SACPane.getChildren().add(new HBox(control, serviceName));
        }
    }
}
