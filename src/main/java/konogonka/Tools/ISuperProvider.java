package konogonka.Tools;

import java.io.File;
import java.io.PipedInputStream;
/**
 * Any class of this type must provide streams
 * */
public interface ISuperProvider {
    PipedInputStream getProviderSubFilePipedInpStream(String subFileName) throws Exception;
    PipedInputStream getProviderSubFilePipedInpStream(int subFileNumber) throws Exception;

    File getFile();
    long getRawFileDataStart();
}
