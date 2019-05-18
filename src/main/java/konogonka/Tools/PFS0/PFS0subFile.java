package konogonka.Tools.PFS0;

public class PFS0subFile {
    private String name;
    private long offset;     // REAL in file (including offset in NCA/NSP file)
    private long size;
    private byte[] zeroes;

    public PFS0subFile(String name, long offset, long size, byte[] zeroesInTable){
        this.name = name;
        this.offset = offset;
        this.size = size;
        this.zeroes = zeroesInTable;
    }

    public String getName() { return name; }
    public long getOffset() { return offset; }
    public long getSize() { return size; }
    public byte[] getZeroes() { return zeroes; }
}
