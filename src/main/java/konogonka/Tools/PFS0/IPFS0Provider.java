package konogonka.Tools.PFS0;

import konogonka.Tools.ISuperProvider;

public interface IPFS0Provider extends ISuperProvider {
    boolean isEncrypted();
    String getMagic();
    int getFilesCount();
    int getStringTableSize();
    byte[] getPadding();

    PFS0subFile[] getPfs0subFiles();
}
