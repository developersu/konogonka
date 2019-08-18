package konogonka.Tools;

import java.io.PipedInputStream;

public interface ISuperProvider {
    PipedInputStream getProviderSubFilePipedInpStream(String subFileName);
    PipedInputStream getProviderSubFilePipedInpStream(int subFileNumber);
}
