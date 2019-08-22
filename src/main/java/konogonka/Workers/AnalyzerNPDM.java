package konogonka.Workers;

import javafx.concurrent.Task;
import konogonka.ModelControllers.EMsgType;
import konogonka.ModelControllers.LogPrinter;
import konogonka.Tools.NPDM.NPDMProvider;

import java.io.File;

public class AnalyzerNPDM extends Task<NPDMProvider> {

    private File file;
    private long offset;
    private LogPrinter logPrinter;

    public AnalyzerNPDM(File file){
        this(file, 0);
    }

    public AnalyzerNPDM(File file, long offset){
        this.file = file;
        this.offset = offset;
        this.logPrinter = new LogPrinter();
    }

    @Override
    protected NPDMProvider call() {
        logPrinter.print("\tStart chain: NPDM", EMsgType.INFO);
        try{
            return new NPDMProvider(file, offset);
        }
        catch (Exception e){
            logPrinter.print("\tException: "+e.getMessage(), EMsgType.FAIL);
            return null;
        }
        finally {
            close();
        }
    }

    private void close(){
        logPrinter.print("\tEnd chain: NPDM", EMsgType.INFO);
        logPrinter.close();
    }
}

