package konogonka.Workers;

import javafx.concurrent.Task;
import konogonka.Controllers.IRowModel;
import konogonka.ModelControllers.EMsgType;
import konogonka.ModelControllers.LogPrinter;

import java.io.*;
import java.util.List;

public class NspXciExtractor extends Task<Void> {

    private long rawDataStartPos;
    private List<IRowModel> models;
    private String filesDestPath;
    private LogPrinter logPrinter;
    private File NspXciFile;

    public NspXciExtractor(long rawDataStartPos, List<IRowModel> models, String filesDestPath, File NspXciFile){
        this.rawDataStartPos = rawDataStartPos;
        this.models = models;
        this.filesDestPath = filesDestPath;
        this.NspXciFile = NspXciFile;
        this.logPrinter = new LogPrinter();
    }

    @Override
    protected Void call() {
        logPrinter.print("\tStart extracting", EMsgType.INFO);
        for (IRowModel model: models){
            logPrinter.print(filesDestPath+model.getFileName(), EMsgType.INFO);

            File contentFile = new File(filesDestPath+model.getFileName());

            long realFileOffset = rawDataStartPos + model.getFileOffset();
            long realFileSize = model.getFileSize();

            long readFrom = 0;

            int readPice = 8388608; // 8mb NOTE: consider switching to 1mb 1048576
            byte[] readBuf;

                try{
                    BufferedOutputStream extractedFileOS = new BufferedOutputStream(new FileOutputStream(contentFile));

                    BufferedInputStream bufferedInStream = new BufferedInputStream(new FileInputStream(NspXciFile));      // TODO: refactor?
                    if (bufferedInStream.skip(realFileOffset) != realFileOffset) {
                        logPrinter.print("File length is less than offset noted", EMsgType.FAIL);
                        return null;
                    }

                    while (readFrom < realFileSize){
/*
                        if (isCancelled())     // Check if user interrupted process.
                            return false;
*/
                        if (realFileSize - readFrom < readPice)
                            readPice = Math.toIntExact(realFileSize - readFrom);    // it's safe, I guarantee
                        readBuf = new byte[readPice];
                        if (bufferedInStream.read(readBuf) != readPice) {
                            logPrinter.print("Can't read required chunk from file", EMsgType.FAIL);
                            return null;
                        }

                        extractedFileOS.write(readBuf, 0, readPice);
                        //-----------------------------------------/
                        try {
                            logPrinter.updateProgress((readFrom+readPice)/(realFileSize/100.0) / 100.0);
                        }catch (InterruptedException ie){
                            getException().printStackTrace();               // TODO: Do something with this
                        }
                        //-----------------------------------------/
                        readFrom += readPice;
                    }
                    bufferedInStream.close();
                    extractedFileOS.close();
                    //-----------------------------------------/
                    try{
                        logPrinter.updateProgress(1.0);
                    }
                    catch (InterruptedException ie){
                        getException().printStackTrace();               // TODO: Do something with this
                    }
                    //-----------------------------------------/
                }
                catch (IOException ioe){
                    logPrinter.print("\tRead/Write error\n\t"+ioe.getMessage(), EMsgType.INFO);
                    return null;
                }
        }
        close();
        return null;
    }

    private void close(){
        logPrinter.print("\tEnd extracting", EMsgType.INFO);
        logPrinter.close();
    }
}