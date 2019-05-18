package konogonka.Controllers.NSP;

import konogonka.Controllers.IRowModel;

public class Pfs0RowModel implements IRowModel {

    private static int numberCnt = 0;
    public static void resetNumCnt(){ numberCnt = 0; }

    private int number;
    private String fileName;
    private long fileSize;
    private long fileOffset;
    private boolean markForUpload;

    Pfs0RowModel(String fileName, long size, long offset){
        this.markForUpload = false;
        this.fileName = fileName;
        this.fileOffset = offset;
        this.fileSize = size;
        this.number = numberCnt++;
    }
    // Model methods start
    public int getNumber(){
        return number;
    }
    public String getFileName(){
        return fileName;
    }
    public long getFileSize() { return fileSize; }
    public long getFileOffset() {return fileOffset; }
    public boolean isMarkSelected() {
        return markForUpload;
    }
    // Model methods end

    public void setMarkSelected(boolean value){
        markForUpload = value;
    }
}
