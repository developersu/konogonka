package konogonka.Workers;

import javafx.concurrent.Task;
import konogonka.ModelControllers.EMsgType;
import konogonka.ModelControllers.LogPrinter;
import konogonka.Tools.PFS0.PFS0Provider;


import java.io.File;

public class AnalyzerNSP extends Task<PFS0Provider> {

    private File file;
    private LogPrinter logPrinter;

    public AnalyzerNSP(File file){
        this.file = file;
        this.logPrinter = new LogPrinter();
    }

    @Override
    protected PFS0Provider call() {
        logPrinter.print("\tStart chain: NSP", EMsgType.INFO);
        try{
            return new PFS0Provider(file);
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
        logPrinter.print("\tEnd chain: NSP", EMsgType.INFO);
        logPrinter.close();
    }
}
