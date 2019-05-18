package konogonka.Tools.NCA.NCASectionTableBlock;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static konogonka.LoperConverter.getLEint;
import static konogonka.LoperConverter.getLElong;

public class NCASectionBlock {
    private byte[] version;
    private byte fsType;
    private byte hashType;
    private byte cryptoType;
    private byte[] padding;
    private SuperBlockIVFC superBlockIVFC;
    private SuperBlockPFS0 superBlockPFS0;
    private byte[] BKTRfullHeader;
    // BKTR extended
    private long BKTRoffsetSection1;
    private long BKTRsizeSection1;
    private String BKTRmagicSection1;
    private int BKTRu32Section1;
    private int BKTRs32Section1;
    private byte[] BKTRunknownSection1;
    
    private long BKTRoffsetSection2;
    private long BKTRsizeSection2;
    private String BKTRmagicSection2;
    private int BKTRu32Section2;
    private int BKTRs32Section2;
    private byte[] BKTRunknownSection2;

    private byte[] sectionCTRlow;
    private byte[] sectionCTRhigh;
    private byte[] unknownEndPadding;

    public NCASectionBlock(byte[] tableBlockBytes) throws Exception{
        if (tableBlockBytes.length != 0x200)
            throw new Exception("Table Block Section size is incorrect.");
        version = Arrays.copyOfRange(tableBlockBytes, 0, 0x2);
        fsType = tableBlockBytes[0x2];
        hashType = tableBlockBytes[0x3];
        cryptoType = tableBlockBytes[0x4];
        padding = Arrays.copyOfRange(tableBlockBytes, 0x5, 0x8);
        byte[] superBlockBytes = Arrays.copyOfRange(tableBlockBytes, 0x8, 0xf8);

        if ((fsType == 0) && (hashType == 0x3))
            superBlockIVFC = new SuperBlockIVFC(superBlockBytes);
        else if ((fsType == 0x1) && (hashType == 0x2))
            superBlockPFS0 = new SuperBlockPFS0(superBlockBytes);

        BKTRfullHeader = Arrays.copyOfRange(tableBlockBytes, 0x100, 0x140);

        BKTRoffsetSection1 = getLElong(BKTRfullHeader, 0);
        BKTRsizeSection1 = getLElong(BKTRfullHeader, 0x8);
        BKTRmagicSection1 = new String(Arrays.copyOfRange(BKTRfullHeader, 0x10, 0x14), StandardCharsets.US_ASCII);
        BKTRu32Section1 = getLEint(BKTRfullHeader, 0x14);
        BKTRs32Section1 = getLEint(BKTRfullHeader, 0x18);
        BKTRunknownSection1 = Arrays.copyOfRange(tableBlockBytes, 0x1c, 0x20);

        BKTRoffsetSection2 = getLElong(BKTRfullHeader, 0x20);
        BKTRsizeSection2 = getLElong(BKTRfullHeader, 0x28);
        BKTRmagicSection2 = new String(Arrays.copyOfRange(BKTRfullHeader, 0x30, 0x34), StandardCharsets.US_ASCII);
        BKTRu32Section2 = getLEint(BKTRfullHeader, 0x34);
        BKTRs32Section2 = getLEint(BKTRfullHeader, 0x38);
        BKTRunknownSection2 = Arrays.copyOfRange(BKTRfullHeader, 0x3c, 0x40);

        sectionCTRlow = Arrays.copyOfRange(tableBlockBytes, 0x140, 0x144);
        sectionCTRhigh = Arrays.copyOfRange(tableBlockBytes, 0x144, 0x148);
        unknownEndPadding = Arrays.copyOfRange(tableBlockBytes, 0x148, 0x200);
    }

    public byte[] getVersion() { return version; }
    public byte getFsType() { return fsType; }
    public byte getHashType() { return hashType; }
    public byte getCryptoType() { return cryptoType; }
    public byte[] getPadding() { return padding; }
    public SuperBlockIVFC getSuperBlockIVFC() { return superBlockIVFC; }
    public SuperBlockPFS0 getSuperBlockPFS0() { return superBlockPFS0; }
    public byte[] getBKTRfullHeader() { return BKTRfullHeader; }

    public long getBKTRoffsetSection1() { return BKTRoffsetSection1; }
    public long getBKTRsizeSection1() { return BKTRsizeSection1; }
    public String getBKTRmagicSection1() { return BKTRmagicSection1; }
    public int getBKTRu32Section1() { return BKTRu32Section1; }
    public int getBKTRs32Section1() { return BKTRs32Section1; }
    public byte[] getBKTRunknownSection1() { return BKTRunknownSection1; }
    public long getBKTRoffsetSection2() { return BKTRoffsetSection2; }
    public long getBKTRsizeSection2() { return BKTRsizeSection2; }
    public String getBKTRmagicSection2() { return BKTRmagicSection2; }
    public int getBKTRu32Section2() { return BKTRu32Section2; }
    public int getBKTRs32Section2() { return BKTRs32Section2; }
    public byte[] getBKTRunknownSection2() { return BKTRunknownSection2; }
    public byte[] getSectionCTRlow() { return sectionCTRlow; }
    public byte[] getSectionCTRhigh() { return sectionCTRhigh; }
    public byte[] getUnknownEndPadding() { return unknownEndPadding; }
}

