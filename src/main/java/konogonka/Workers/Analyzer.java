package konogonka.Workers;

import javafx.concurrent.Task;
import konogonka.ModelControllers.EMsgType;
import konogonka.ModelControllers.LogPrinter;
import konogonka.Tools.ISuperProvider;
import konogonka.Tools.NCA.NCAProvider;
import konogonka.Tools.NPDM.NPDMProvider;
import konogonka.Tools.PFS0.PFS0Provider;
import konogonka.Tools.TIK.TIKProvider;
import konogonka.Tools.XCI.XCIProvider;

import java.io.File;
import java.util.HashMap;

// TODO: volatile needed?

public class Analyzer {

    public static Task<NCAProvider> analyzeNCA(File file, HashMap<String, String> keysMap, long offset){
        LogPrinter logPrinter = new LogPrinter();

        return new Task<NCAProvider>(){
            @Override
            protected NCAProvider call() {
                logPrinter.print("\tStart chain: NCA", EMsgType.INFO);
                try {
                    return new NCAProvider(file, keysMap, offset);
                }
                catch (Exception e){
                    logPrinter.print(e.getMessage(), EMsgType.FAIL);
                    return null;
                }
                finally {
                    logPrinter.print("\tEnd chain: NCA", EMsgType.INFO);
                    logPrinter.close();
                }
            }
        };
    }

    public static Task<TIKProvider> analyzeTIK(File file, long offset){
        LogPrinter logPrinter = new LogPrinter();
        return new Task<TIKProvider>() {
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
                    logPrinter.print("\tEnd chain: TIK", EMsgType.INFO);
                    logPrinter.close();
                }
            }
        };
    }

    public static Task<NPDMProvider> analyzeNPDM(File file, long offset){
        LogPrinter logPrinter = new LogPrinter();
        return new Task<NPDMProvider>(){
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
                    logPrinter.print("\tEnd chain: NPDM", EMsgType.INFO);
                    logPrinter.close();
                }
            }
        };
    }

    public static Task<NPDMProvider> analyzeNPDM(ISuperProvider parentProvider, int fileNo){
        LogPrinter logPrinter = new LogPrinter();
        return new Task<NPDMProvider>(){
            @Override
            protected NPDMProvider call() {
                logPrinter.print("\tStart chain: NPDM [stream]", EMsgType.INFO);
                try{
                    return new NPDMProvider(parentProvider.getProviderSubFilePipedInpStream(fileNo));
                }
                catch (Exception e){
                    logPrinter.print("\tException: "+e.getMessage(), EMsgType.FAIL);
                    return null;
                }
                finally {
                    logPrinter.print("\tEnd chain: NPDM [stream]", EMsgType.INFO);
                    logPrinter.close();
                }
            }
        };
    }

    public static Task<PFS0Provider> analyzePFS0(File file){
        LogPrinter logPrinter = new LogPrinter();
        return new Task<PFS0Provider>(){
            @Override
            protected PFS0Provider call() {
                logPrinter.print("\tStart chain: NSP (PFS0)", EMsgType.INFO);
                try{
                    return new PFS0Provider(file);
                }
                catch (Exception e){
                    logPrinter.print("\tException: "+e.getMessage(), EMsgType.FAIL);
                    return null;
                }
                finally {
                    logPrinter.print("\tEnd chain: NSP (PFS0)", EMsgType.INFO);
                    logPrinter.close();
                }
            }
        };
    }

    public static Task<XCIProvider> analyzeXCI(File file, String xciHdrKey){
        LogPrinter logPrinter = new LogPrinter();
        return new Task<XCIProvider>() {
            @Override
            protected XCIProvider call() {
                logPrinter.print("\tStart chain: XCI", EMsgType.INFO);
                try {
                    return new XCIProvider(file, xciHdrKey);
                } catch (Exception e) {
                    logPrinter.print(e.getMessage(), EMsgType.FAIL);
                    return null;
                } finally {
                    logPrinter.print("\tEnd chain: XCI", EMsgType.INFO);
                    logPrinter.close();
                }
            }
        };
    }
}
