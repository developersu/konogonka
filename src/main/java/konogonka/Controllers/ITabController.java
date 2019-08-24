package konogonka.Controllers;

import javafx.fxml.Initializable;
import konogonka.Tools.ISuperProvider;

import java.io.File;

public interface ITabController extends Initializable {
    void analyze(File file);
    void analyze(File file, long offset);
    void analyze(ISuperProvider parentProvider, int fileNo) throws Exception;
    void resetTab();
}
