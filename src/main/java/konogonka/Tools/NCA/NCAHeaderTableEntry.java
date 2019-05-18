package konogonka.Tools.NCA;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class NCAHeaderTableEntry {

    private long mediaStartOffset;
    private long mediaEndOffset;
    private byte[] unknwn1;
    private byte[] unknwn2;

    public NCAHeaderTableEntry(byte[] table) throws Exception{
        if (table.length < 0x10)
            throw new Exception("Section Table size is too small.");

        this.mediaStartOffset = convertUnsignedIntBytesToLong(Arrays.copyOfRange(table, 0x0, 0x4));
        this.mediaEndOffset = convertUnsignedIntBytesToLong(Arrays.copyOfRange(table, 0x4, 0x8));
        this.unknwn1 = Arrays.copyOfRange(table, 0x8, 0xC);
        this.unknwn2 = Arrays.copyOfRange(table, 0xC, 0x10);
    }

    private long convertUnsignedIntBytesToLong(byte[] intBytes){
        if (intBytes.length == 4)
            return ByteBuffer.wrap(Arrays.copyOf(intBytes, 8)).order(ByteOrder.LITTLE_ENDIAN).getLong();
        else
            return -1;
    }

    public long getMediaStartOffset() { return mediaStartOffset; }
    public long getMediaEndOffset() { return mediaEndOffset; }
    public byte[] getUnknwn1() { return unknwn1; }
    public byte[] getUnknwn2() { return unknwn2; }
}