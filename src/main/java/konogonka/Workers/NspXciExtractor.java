package konogonka.Workers;

import javafx.concurrent.Task;
import konogonka.Controllers.IRowModel;
import konogonka.ModelControllers.EMsgType;
import konogonka.ModelControllers.LogPrinter;
import konogonka.Tools.ISuperProvider;

import java.io.*;
import java.util.List;

public class NspXciExtractor extends Task<Void> {

    private ISuperProvider provider;
    private List<IRowModel> models;
    private LogPrinter logPrinter;

    private String filesDestPath;

    public NspXciExtractor(ISuperProvider provider, List<IRowModel> models, String filesDestPath){
        this.provider = provider;
        this.models = models;
        this.filesDestPath = filesDestPath;
        this.logPrinter = new LogPrinter();
    }

    @Override
    protected Void call() {
        for (IRowModel model : models) {
            logPrinter.print("\tStart extracting: \n"+filesDestPath+model.getFileName(), EMsgType.INFO);
            File contentFile = new File(filesDestPath + model.getFileName());
            try {
                BufferedOutputStream extractedFileBOS = new BufferedOutputStream(new FileOutputStream(contentFile));
                PipedInputStream pis = provider.getProviderSubFilePipedInpStream(model.getNumber());

                byte[] readBuf = new byte[0x800000]; // 8mb NOTE: consider switching to 1mb 1048576
                int readSize;
                //*** PROGRESS BAR VARS START
                long progressHandleFSize = model.getFileSize();
                int progressHandleFRead = 0;
                //*** PROGRESS BAR VARS END
                while ((readSize = pis.read(readBuf)) > -1) {
                    extractedFileBOS.write(readBuf, 0, readSize);
                    readBuf = new byte[0x800000];
                    //*** PROGRESS BAR DECORCATIONS START
                    progressHandleFRead += readSize;
                    try {
                        logPrinter.updateProgress((progressHandleFRead)/(progressHandleFSize/100.0) / 100.0);
                    }catch (InterruptedException ie){
                        getException().printStackTrace();               // TODO: Do something with this
                    }
                    //*** PROGRESS BAR DECORCATIONS END
                }
                try {
                    logPrinter.updateProgress(1.0);
                }catch (InterruptedException ie){
                    getException().printStackTrace();               // TODO: Do something with this
                }
                extractedFileBOS.close();
            } catch (Exception ioe) {
                logPrinter.print("\tExtracting issue\n\t" + ioe.getMessage(), EMsgType.INFO);
                return null;
            } finally {
                logPrinter.print("\tEnd extracting", EMsgType.INFO);
                logPrinter.close();
            }
        }
        return null;
    }
    /*
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

    //                        if (isCancelled())     // Check if user interrupted process.
    //                            return false;

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
    */
}