package konogonka.Workers;

import javafx.concurrent.Task;
import konogonka.ModelControllers.EMsgType;
import konogonka.ModelControllers.LogPrinter;
import libKonogonka.Tools.RomFs.FileSystemEntry;
import libKonogonka.Tools.RomFs.IRomFsProvider;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PipedInputStream;
import java.util.List;

public class DumbRomFsExtractor extends Task<Void> {

    private IRomFsProvider provider;
    private FileSystemEntry entry;
    private List<FileSystemEntry> entries;
    private LogPrinter logPrinter;
    private String filesDestPath;

    public DumbRomFsExtractor(IRomFsProvider provider, List<FileSystemEntry> entries, String filesDestPath){
        this.provider = provider;
        this.entries = entries;
        this.filesDestPath = filesDestPath;
        this.logPrinter = new LogPrinter();
    }

    public DumbRomFsExtractor(IRomFsProvider provider, FileSystemEntry entry, String filesDestPath){
        this.provider = provider;
        this.entry = entry;
        this.filesDestPath = filesDestPath;
        this.logPrinter = new LogPrinter();
    }

    @Override
    protected Void call() {
        try {
            if (this.entries == null){
                logPrinter.print("\tStart dummy extracting from 'RomFs' image: \n"+filesDestPath+entry.getName(), EMsgType.INFO);
                if (entry.isFile())
                    exportSingleFile(entry, filesDestPath);
                else
                    exportFolderContent(entry, filesDestPath);
            }
            else {
                logPrinter.print("\tStart dummy extracting from 'RomFs' image: \n"+filesDestPath+"...", EMsgType.INFO);
                for (FileSystemEntry e : entries){
                    if (e.isFile())
                        exportSingleFile(e, filesDestPath);
                    else
                        exportFolderContent(e, filesDestPath);
                }
            }

        } catch (Exception ioe) {
            logPrinter.print("\tDummy extracting from 'RomFs' image issue\n\t" + ioe.getMessage(), EMsgType.INFO);
            return null;
        } finally {
            logPrinter.print("\tEnd dummy extracting from 'RomFs' image extracting", EMsgType.INFO);
            logPrinter.close();
        }
        return null;
    }

    private void exportSingleFile(FileSystemEntry entry, String saveToLocation) throws Exception{
        File contentFile = new File(saveToLocation + entry.getName());

        BufferedOutputStream extractedFileBOS = new BufferedOutputStream(new FileOutputStream(contentFile));
        PipedInputStream pis = provider.getContent(entry);

        byte[] readBuf = new byte[0x200]; // 8mb NOTE: consider switching to 1mb 1048576
        int readSize;
        //*** PROGRESS BAR VARS START
        long progressHandleFSize = entry.getFileSize();
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
    }

    private void exportFolderContent(FileSystemEntry entry, String saveToLocation) throws Exception{
        File contentFile = new File(saveToLocation + entry.getName());
        contentFile.mkdirs();
        String currentDirPath = saveToLocation + entry.getName() + File.separator;
        for (FileSystemEntry fse : entry.getContent()){
            if (fse.isDirectory())
                exportFolderContent(fse, currentDirPath);
            else
                exportSingleFile(fse, currentDirPath);
        }
    }
}