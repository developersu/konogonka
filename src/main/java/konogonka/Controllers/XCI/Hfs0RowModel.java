package konogonka.Controllers.XCI;

import konogonka.Controllers.IRowModel;

public class Hfs0RowModel implements IRowModel {

    private static int numberCnt = 0;
    public static void resetNumCnt(){ numberCnt = 0; }

    private int number;
    private String fileName;
    private long fileSize;
    private long fileOffset;
    private long hashedRegionSize;
    private boolean padding;
    private String SHA256Hash;
    private boolean markForUpload;

    Hfs0RowModel(String fileName, long size, long offset, long hashedRegionSize, boolean padding, byte[] SHA256Hash){
        this.markForUpload = false;
        this.fileName = fileName;
        this.fileOffset = offset;
        this.fileSize = size;
        this.hashedRegionSize = hashedRegionSize;
        this.padding = padding;
        StringBuilder sb = new StringBuilder();
        for (byte b: SHA256Hash)
            sb.append(String.format("%02x", b));
        this.SHA256Hash = sb.toString();
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

    public long getHashedRegionSize() { return hashedRegionSize; }
    public boolean isPadding() { return padding; }
    public String getSHA256Hash() { return SHA256Hash; }

    public boolean isMarkSelected() {
        return markForUpload;
    }
    // Model methods end
    public void setMarkSelected(boolean value){
        markForUpload = value;
    }
}
