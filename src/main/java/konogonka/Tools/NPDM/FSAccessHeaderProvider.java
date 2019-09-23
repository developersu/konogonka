package konogonka.Tools.NPDM;

import konogonka.LoperConverter;
import konogonka.RainbowHexDump;

import java.util.Arrays;

/**
 * For ACI0 Provider
 * */
public class FSAccessHeaderProvider {

    private byte version;
    private byte[] padding;
    private long permissionsBitmask;
    private int dataSize;
    private int contentOwnIdSectionSize;
    private int dataNownerSizes;
    private int saveDataOwnSectionSize;
    private byte[] unknownData;

    public FSAccessHeaderProvider(byte[] bytes) {
        version = bytes[0];
        padding = Arrays.copyOfRange(bytes, 1, 0x4);
        permissionsBitmask = LoperConverter.getLElong(bytes, 0x4);
        dataSize = LoperConverter.getLEint(bytes, 0xC);
        contentOwnIdSectionSize = LoperConverter.getLEint(bytes, 0x10);
        dataNownerSizes = LoperConverter.getLEint(bytes, 0x14);
        saveDataOwnSectionSize = LoperConverter.getLEint(bytes, 0x18);
        unknownData = Arrays.copyOfRange(bytes, 0x1C, bytes.length);
        //
        System.out.println("version "+version);
        System.out.print("padding ");
        RainbowHexDump.hexDumpUTF8(padding);
        System.out.print("Permissions Bitmask ");
        RainbowHexDump.octDumpLong(permissionsBitmask);
        System.out.println(
                "DataSize "+dataSize+"\n"+
                "Content OwnId Section Size "+contentOwnIdSectionSize+"\n"+
                "Data + owner  "+dataNownerSizes+"\n"+
                "Save Data Own Section Size "+saveDataOwnSectionSize
        );
        RainbowHexDump.hexDumpUTF8(Arrays.copyOfRange(bytes, 0x1C, bytes.length));
        //*/
        //reserved = Arrays.copyOfRange(bytes, 0xC, 0x2C);
    }

    public byte getVersion() { return version; }
    public byte[] getPadding() { return padding; }
    public long getPermissionsBitmask() { return permissionsBitmask; }
    public int getDataSize() { return dataSize; }
    public int getContentOwnIdSectionSize() { return contentOwnIdSectionSize; }
    public int getDataNownerSizes() { return dataNownerSizes; }
    public int getSaveDataOwnSectionSize() { return saveDataOwnSectionSize; }
    public byte[] getUnknownData() { return unknownData; }
}
