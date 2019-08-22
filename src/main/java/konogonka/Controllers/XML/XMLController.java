package konogonka.Controllers.XML;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import konogonka.Controllers.ITabController;
import konogonka.MediatorControl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

public class XMLController implements ITabController {
    @FXML

    private TextArea mainTa;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {    }

    /**
     * Read from start to end
     * */
    @Override
    public void analyze(File file) { analyze(file, 0); }
    /**
     * Read from offset to end
     * */
    @Override
    public void analyze(File file, long offset) {
        try {
            if (file.length() - offset > 10485760)  // 10mB
                throw new Exception("XMLController -> analyze(): File is too big. It must be something wrong with it. Usually they're smaller");
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            if (bis.skip(offset) != offset)
                throw new Exception("XMLController -> analyze(): Unable to skip requested range");
            int readChunk = (int) (file.length() - offset);
            byte[] buffer = new byte[readChunk];    // Let it be 1kB
            if (bis.read(buffer) != readChunk)
                throw new Exception("XMLController -> analyze(): Unable to read requested range");
            mainTa.appendText(new String(buffer, 0, readChunk, StandardCharsets.UTF_8));
            bis.close();
        }
        catch (Exception e){
            MediatorControl.getInstance().getContoller().logArea.appendText("XMLController -> analyze(): \n"+e.getMessage());
        }
    }
    /**
     * Read from offset to length
     * */
    public void analyze(File file, long offset, long fileSize) {
        try {
            if (fileSize > 10485760)  // 10mB
                throw new Exception("XMLController -> analyze(): File is too big. It must be something wrong with it. Usually they're smaller");
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            if (bis.skip(offset) != offset)
                throw new Exception("XMLController -> analyze(): Unable to skip requested range");
            byte[] buffer = new byte[(int)fileSize];    // Let it be 1kB
            if (bis.read(buffer) != fileSize)
                throw new Exception("XMLController -> analyze(): Unable to read requested range");
            mainTa.appendText(new String(buffer, 0, (int) fileSize, StandardCharsets.UTF_8));
            bis.close();
        }
        catch (Exception e){
            MediatorControl.getInstance().getContoller().logArea.appendText("XMLController -> analyze(): \n"+e.getMessage());
        }
    }
    @Override
    public void resetTab() {
        mainTa.setText(null);
    }
}
