package konogonka.Tools.PFS0;

public interface IPFS0Provider {
    String getMagic();
    int getFilesCount();
    int getStringTableSize();
    byte[] getPadding();

    long getRawFileDataStart();
    PFS0subFile[] getPfs0subFiles();
}
