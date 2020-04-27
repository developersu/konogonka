package konogonka.Workers;

import javafx.concurrent.Task;
import konogonka.ModelControllers.EMsgType;
import konogonka.ModelControllers.LogPrinter;
import konogonka.Tools.NCA.NCAContent;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PipedInputStream;

public class DumbNCA3ContentExtractor extends Task<Void> {

    private NCAContent ncaContent;
    private int ncaNumberInFile;
    private LogPrinter logPrinter;
    private String filesDestPath;

    public DumbNCA3ContentExtractor(NCAContent ncaContent, int ncaNumberInFile, String filesDestPath){
        this.ncaContent = ncaContent;
        this.ncaNumberInFile = ncaNumberInFile;
        this.filesDestPath = filesDestPath;
        this.logPrinter = new LogPrinter();
    }

    @Override
    protected Void call() {
        logPrinter.print("\tStart dummy extracting: \n"+filesDestPath+"NCAContent_"+ncaNumberInFile+".bin", EMsgType.INFO);
        File contentFile = new File(filesDestPath + "NCAContent_"+ncaNumberInFile+".bin");
        try {
            BufferedOutputStream extractedFileBOS = new BufferedOutputStream(new FileOutputStream(contentFile));
            PipedInputStream pis = ncaContent.getRawDataContentPipedInpStream();

            byte[] readBuf = new byte[0x200]; // 8mb NOTE: consider switching to 1mb 1048576
            int readSize;
            //*** PROGRESS BAR VARS START
            long progressHandleFSize = ncaContent.getRawDataContentSize();
            int progressHandleFRead = 0;
            //*** PROGRESS BAR VARS END
            while ((readSize = pis.read(readBuf)) > -1) {
                extractedFileBOS.write(readBuf, 0, readSize);
                readBuf = new byte[0x200];
                //*** PROGRESS BAR DECORCATIONS START
                progressHandleFRead += readSize;
                //System.out.println(readSize);
                try {
                    logPrinter.updateProgress((progressHandleFRead)/(progressHandleFSize/100.0) / 100.0);
                }catch (InterruptedException ignore){}
                //*** PROGRESS BAR DECORCATIONS END
            }
            try {
                logPrinter.updateProgress(1.0);
            }
            catch (InterruptedException ignored){}
            extractedFileBOS.close();

        } catch (Exception ioe) {
            logPrinter.print("\tExtracting dummy issue\n\t" + ioe.getMessage(), EMsgType.INFO);
            return null;
        } finally {
            logPrinter.print("\tEnd dummy extracting", EMsgType.INFO);
            logPrinter.close();
        }
        return null;
    }
}