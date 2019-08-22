package konogonka.Tools.NPDM;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static konogonka.LoperConverter.*;

public class NPDMProvider {

    private String magicNum;
    private byte[] reserved1;
    private byte MMUFlags;
    private byte reserved2;
    private byte mainThreadPrio;
    private byte mainThreadCoreNum;
    private byte[] reserved3;
    private int personalMmHeapSize;     // safe-to-store
    private int version;                // safe?
    private long mainThreadStackSize;    // TODO: check if safe
    private String titleName;
    private byte[] productCode;
    private byte[] reserved4;
    private long aci0offset;            // originally 4-bytes (u-int)
    private long aci0size;              // originally 4-bytes (u-int)
    private long acidOffset;            // originally 4-bytes (u-int)
    private long acidSize;              // originally 4-bytes (u-int)

    public NPDMProvider(File file) throws Exception  { this(file, 0); }

    public NPDMProvider(File file, long offset) throws Exception {
        if (file.length() - offset < 0x80)  // Header's size
            throw new Exception("NPDMProvider: File is too small.");
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        raf.seek(offset);
        // Get META
        byte[] metaBuf = new byte[0x80];
        if (raf.read(metaBuf) != 0x80)
            throw new Exception("NPDMProvider: Failed to read 'META'");
        magicNum = new String(metaBuf, 0, 4, StandardCharsets.UTF_8);
        reserved1 = Arrays.copyOfRange(metaBuf, 0x4, 0xC);
        MMUFlags = metaBuf[0xC];
        reserved2 = metaBuf[0xD];
        mainThreadPrio = metaBuf[0xE];
        mainThreadCoreNum = metaBuf[0xF];
        reserved3 = Arrays.copyOfRange(metaBuf, 0x10, 0x14);
        personalMmHeapSize = getLEint(metaBuf, 0x14);
        version = getLEint(metaBuf, 0x18);
        mainThreadStackSize = getLElongOfInt(metaBuf, 0x1C);
        titleName = new String(metaBuf, 0x20, 0x10, StandardCharsets.UTF_8);
        productCode = Arrays.copyOfRange(metaBuf, 0x30, 0x40);
        reserved4 = Arrays.copyOfRange(metaBuf, 0x40, 0x70);
        aci0offset =  getLElongOfInt(metaBuf, 0x70);
        aci0size = getLElongOfInt(metaBuf, 0x74);
        acidOffset = getLElongOfInt(metaBuf, 0x78);
        acidSize = getLElongOfInt(metaBuf, 0x7C);
    }

    public String getMagicNum() { return magicNum; }
    public byte[] getReserved1() { return reserved1; }
    public byte getMMUFlags() { return MMUFlags; }
    public byte getReserved2() { return reserved2; }
    public byte getMainThreadPrio() { return mainThreadPrio; }
    public byte getMainThreadCoreNum() { return mainThreadCoreNum; }
    public byte[] getReserved3() { return reserved3; }
    public int getPersonalMmHeapSize() { return personalMmHeapSize; }
    public int getVersion() { return version; }
    public long getMainThreadStackSize() { return mainThreadStackSize; }
    public String getTitleName() { return titleName; }
    public byte[] getProductCode() { return productCode; }
    public byte[] getReserved4() { return reserved4; }
    public long getAci0offset() { return aci0offset; }
    public long getAci0size() { return aci0size; }
    public long getAcidOffset() { return acidOffset; }
    public long getAcidSize() { return acidSize; }
}
