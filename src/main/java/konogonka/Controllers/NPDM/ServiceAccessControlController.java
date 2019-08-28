package konogonka.Controllers.NPDM;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
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

    public void populateFields(LinkedHashMap<String, Byte> collection){
        resetTab();

        for (Map.Entry entry : collection.entrySet()) {
            Label control = new Label(String.format("0x%02x", (Byte) entry.getValue()));
            Label serviceName = new Label((String) entry.getKey());

            control.setPadding(new Insets(5.0, 5.0, 5.0, 5.0));
            serviceName.setPadding(new Insets(5.0, 5.0, 5.0, 5.0));

            SACPane.getChildren().add(new HBox(control, serviceName));
        }
    }
}
