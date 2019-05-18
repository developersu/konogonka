package konogonka.Workers;

import javafx.concurrent.Task;
import konogonka.ModelControllers.EMsgType;
import konogonka.ModelControllers.LogPrinter;
import konogonka.Tools.NCA.NCAProvider;

import java.io.File;
import java.util.HashMap;

public class AnalyzerNCA extends Task<NCAProvider> {

    private File file;
    private LogPrinter logPrinter;
    private HashMap<String, String> keysMap;

    public AnalyzerNCA(File file, HashMap<String, String> keysMap){
        this.file = file;
        this.logPrinter = new LogPrinter();
        this.keysMap = keysMap;
    }

    @Override
    protected NCAProvider call() {
        logPrinter.print("\tStart chain: NCA", EMsgType.INFO);

        NCAProvider ncaProvider;

        try {
            ncaProvider = new NCAProvider(file, keysMap);
        }catch (Exception e){
            logPrinter.print(e.getMessage(), EMsgType.FAIL);
            ncaProvider = null;
        }finally {
            logPrinter.print("\tEnd chain: NCA", EMsgType.INFO);
            logPrinter.close();
        }
        return ncaProvider;
    }
}
