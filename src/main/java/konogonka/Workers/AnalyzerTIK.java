package konogonka.Workers;

import javafx.concurrent.Task;
import konogonka.ModelControllers.EMsgType;
import konogonka.ModelControllers.LogPrinter;
import konogonka.Tools.TIK.TIKProvider;

import java.io.File;

public class AnalyzerTIK extends Task<TIKProvider> {

    private File file;
    private long offset;
    private LogPrinter logPrinter;

    public AnalyzerTIK(File file){
        this(file, 0);
    }

    public AnalyzerTIK(File file, long offset){
        this.file = file;
        this.offset = offset;
        this.logPrinter = new LogPrinter();
    }

    @Override
    protected TIKProvider call() {
        logPrinter.print("\tStart chain: TIK", EMsgType.INFO);
        try{
            return new TIKProvider(file, offset);
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
        logPrinter.print("\tEnd chain: TIK", EMsgType.INFO);
        logPrinter.close();
    }
}
