package konogonka.Workers;

import javafx.concurrent.Task;
import konogonka.ModelControllers.EMsgType;
import konogonka.ModelControllers.LogPrinter;
import konogonka.Tools.XCI.XCIProvider;

import java.io.File;

public class AnalyzerXCI extends Task<XCIProvider> {

    private File file;
    private LogPrinter logPrinter;
    private String xciHdrKey;

    public AnalyzerXCI(File file, String xciHdrKey){
        this.file = file;
        this.logPrinter = new LogPrinter();
        this.xciHdrKey = xciHdrKey;
    }

    @Override
    protected XCIProvider call() {
        logPrinter.print("\tStart chain: XCI", EMsgType.INFO);

        XCIProvider xciProvider;

        try {
            xciProvider = new XCIProvider(file, xciHdrKey);
        }catch (Exception e){
            logPrinter.print(e.getMessage(), EMsgType.FAIL);
            xciProvider = null;
        }finally {
            logPrinter.print("\tEnd chain: XCI", EMsgType.INFO);
            logPrinter.close();
        }
        return xciProvider;
    }
}
