package konogonka.Tools.XCI;

public class HFS0File {
    private String name;
    private long offset;
    private long size;
    private long hashedRegionSize;
    private boolean padding;
    private byte[] SHA256Hash;
    
    public HFS0File(String name, long offset, long size, long hashedRegionSize, boolean padding, byte[] SHA256Hash){
        this.name = name;
        this.offset = offset;
        this.size = size;
        this.hashedRegionSize = hashedRegionSize;
        this.padding = padding;
        this.SHA256Hash = SHA256Hash;
    }
    
    public String getName() { return name; }
    public long getOffset() { return offset; }
    public long getSize() { return size; }
    public long getHashedRegionSize() { return hashedRegionSize; }
    public boolean isPadding() { return padding; }
    public byte[] getSHA256Hash() { return SHA256Hash; }
    
}
