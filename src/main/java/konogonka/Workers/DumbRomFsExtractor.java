package konogonka.Workers;

import javafx.concurrent.Task;
import konogonka.ModelControllers.EMsgType;
import konogonka.ModelControllers.LogPrinter;
import libKonogonka.fs.RomFs.FileSystemEntry;
import libKonogonka.fs.RomFs.RomFsProvider;

import java.util.List;

public class DumbRomFsExtractor extends Task<Void> {

    private final RomFsProvider provider;
    private FileSystemEntry entry;
    private List<FileSystemEntry> entries;
    private final LogPrinter logPrinter;
    private final String filesDestPath;

    public DumbRomFsExtractor(RomFsProvider provider, List<FileSystemEntry> entries, String filesDestPath){
        this.provider = provider;
        this.entries = entries;
        this.filesDestPath = filesDestPath;
        this.logPrinter = new LogPrinter();
    }

    public DumbRomFsExtractor(RomFsProvider provider, FileSystemEntry entry, String filesDestPath){
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
                provider.exportContent(filesDestPath, entry);
            }
            else {
                logPrinter.print("\tStart dummy extracting from 'RomFs' image: \n"+filesDestPath+"...", EMsgType.INFO);
                for (FileSystemEntry e : entries)
                    provider.exportContent(filesDestPath, e);
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
}