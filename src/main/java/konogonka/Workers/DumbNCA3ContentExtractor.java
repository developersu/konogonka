package konogonka.Workers;

import javafx.concurrent.Task;
import konogonka.ModelControllers.EMsgType;
import konogonka.ModelControllers.LogPrinter;
import libKonogonka.fs.NCA.NCAContent;

public class DumbNCA3ContentExtractor extends Task<Void> {

    private final NCAContent ncaContent;
    private final int ncaNumberInFile;
    private final LogPrinter logPrinter;
    private final String filesDestinationPath;

    public DumbNCA3ContentExtractor(NCAContent ncaContent, int ncaNumberInFile, String filesDestinationPath){
        this.ncaContent = ncaContent;
        this.ncaNumberInFile = ncaNumberInFile;
        this.filesDestinationPath = filesDestinationPath;
        this.logPrinter = new LogPrinter();
    }

    @Override
    protected Void call() {
        String lv6mark = "";
        if (ncaContent.getRomfs() != null){
            lv6mark = " [lv6 "+ncaContent.getRomfs().getLevel6Offset()+"]";
        }
        logPrinter.print("\tStart dummy extracting: \n"+ filesDestinationPath +"NCAContent_"+ncaNumberInFile+lv6mark+".bin", EMsgType.INFO);
        ncaContent.exportMediaBlock(filesDestinationPath + "NCAContent_"+ncaNumberInFile+lv6mark+".bin");
        logPrinter.print("\tEnd dummy extracting", EMsgType.INFO);
        logPrinter.close();
        return null;
    }
}