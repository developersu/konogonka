package konogonka.Controllers;

public interface IRowModel {
    int getNumber();
    String getFileName();
    long getFileSize();
    long getFileOffset();
    boolean isMarkSelected();
    void setMarkSelected(boolean value);
}
