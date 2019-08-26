package konogonka.Tools.NPDM;

import konogonka.RainbowHexDump;
import konogonka.Tools.ASuperInFileProvider;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static konogonka.LoperConverter.*;

public class NPDMProvider extends ASuperInFileProvider {

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
    private int aci0offset;            // originally 4-bytes (u-int)
    private int aci0size;              // originally 4-bytes (u-int)
    private int acidOffset;            // originally 4-bytes (u-int)
    private int acidSize;              // originally 4-bytes (u-int)

    private ACI0Provider aci0;
    private ACIDProvider acid;

    public NPDMProvider(PipedInputStream pis) throws Exception{
        byte[] mainBuf = new byte[0x80];
        if(pis.read(mainBuf) != 0x80)
            throw new Exception("NPDMProvider: Failed to read 'META'");
        aci0offset =  getLEint(mainBuf, 0x70);
        aci0size = getLEint(mainBuf, 0x74);
        acidOffset = getLEint(mainBuf, 0x78);
        acidSize = getLEint(mainBuf, 0x7C);
        byte[] aci0Buf;
        byte[] acidBuf;
        if (aci0offset < acidOffset){
            if (pis.skip(aci0offset - 0x80) != (aci0offset - 0x80))
                throw new Exception("NPDMProvider: Failed to skip bytes till 'ACI0'");
            if ((aci0Buf = readFromStream(pis, aci0size)) == null)
                throw new Exception("NPDMProvider: Failed to read 'ACI0'");
            if (pis.skip(acidOffset - aci0offset - aci0size) != (acidOffset - aci0offset - aci0size))
                throw new Exception("NPDMProvider: Failed to skip bytes till 'ACID'");
            if ((acidBuf = readFromStream(pis, acidSize)) == null)
                throw new Exception("NPDMProvider: Failed to read 'ACID'");
        }
        else {
            if (pis.skip(acidOffset - 0x80) != (acidOffset - 0x80))
                throw new Exception("NPDMProvider: Failed to skip bytes till 'ACID'");
            if ((acidBuf = readFromStream(pis, acidSize)) == null)
                throw new Exception("NPDMProvider: Failed to read 'ACID'");
            if (pis.skip(aci0offset - acidOffset - acidSize) != (aci0offset - acidOffset - acidSize))
                throw new Exception("NPDMProvider: Failed to skip bytes till 'ACI0'");
            if ((aci0Buf = readFromStream(pis, aci0size)) == null)
                throw new Exception("NPDMProvider: Failed to read 'ACI0'");
        }
        magicNum = new String(mainBuf, 0, 4, StandardCharsets.UTF_8);
        reserved1 = Arrays.copyOfRange(mainBuf, 0x4, 0xC);
        MMUFlags = mainBuf[0xC];
        reserved2 = mainBuf[0xD];
        mainThreadPrio = mainBuf[0xE];
        mainThreadCoreNum = mainBuf[0xF];
        reserved3 = Arrays.copyOfRange(mainBuf, 0x10, 0x14);
        personalMmHeapSize = getLEint(mainBuf, 0x14);
        version = getLEint(mainBuf, 0x18);
        mainThreadStackSize = getLElongOfInt(mainBuf, 0x1C);
        titleName = new String(mainBuf, 0x20, 0x10, StandardCharsets.UTF_8);
        productCode = Arrays.copyOfRange(mainBuf, 0x30, 0x40);
        reserved4 = Arrays.copyOfRange(mainBuf, 0x40, 0x70);

        aci0 = new ACI0Provider(aci0Buf);
        acid = new ACIDProvider(acidBuf);
    }

    public NPDMProvider(File file) throws Exception { this(file, 0); }

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
        aci0offset =  getLEint(metaBuf, 0x70);
        aci0size = getLEint(metaBuf, 0x74);
        acidOffset = getLEint(metaBuf, 0x78);
        acidSize = getLEint(metaBuf, 0x7C);
        // Get ACI0
        raf.seek(aci0offset);
        metaBuf = new byte[aci0size];                                           // TODO: NOTE: we read all size but it's memory consuming
        if (raf.read(metaBuf) != aci0size)
            throw new Exception("NPDMProvider: Failed to read 'ACI0'");
        aci0 = new ACI0Provider(metaBuf);
        // Get ACID
        raf.seek(acidOffset);
        metaBuf = new byte[acidSize];                                           // TODO: NOTE: we read all size but it's memory consuming
        if (raf.read(metaBuf) != acidSize)
            throw new Exception("NPDMProvider: Failed to read 'ACID'");
        acid = new ACIDProvider(metaBuf);
        raf.close();
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
    public int getAci0offset() { return aci0offset; }
    public int getAci0size() { return aci0size; }
    public int getAcidOffset() { return acidOffset; }
    public int getAcidSize() { return acidSize; }

    public ACI0Provider getAci0() { return aci0; }
    public ACIDProvider getAcid() { return acid; }
}
