package konogonka.Controllers;

import javafx.fxml.Initializable;

import java.io.File;

public interface TabController extends Initializable {
    void analyze(File file);
    void resetTab();
}
