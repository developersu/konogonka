package konogonka.Tools.XCI;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static konogonka.LoperConverter.getLEint;
import static konogonka.LoperConverter.getLElong;
/**
 * Header information
 * */
public class XCIGamecardHeader{
    private byte[] rsa2048PKCS1sig;
    private boolean magicHead;
    private byte[] SecureAreaStartAddr;
    private boolean bkupAreaStartAddr;
    private byte titleKEKIndexBoth;
    private byte titleKEKIndex;
    private byte KEKIndex;
    private byte gcSize;
    private byte gcVersion;
    private byte gcFlags;
    private byte[] pkgID;
    private long valDataEndAddr;
    private byte[] gcInfoIV;
    private long hfs0partOffset;
    private long hfs0headerSize;
    private byte[] hfs0headerSHA256;
    private byte[] hfs0initDataSHA256 ;
    private int secureModeFlag;
    private int titleKeyFlag;
    private int keyFlag;
    private byte[] normAreaEndAddr;

    XCIGamecardHeader(byte[] headerBytes) throws Exception{
        if (headerBytes.length != 400)
            throw new Exception("XCIGamecardHeader Incorrect array size. Expected 400 bytes while received "+headerBytes.length);
        rsa2048PKCS1sig = Arrays.copyOfRange(headerBytes, 0, 256);
        magicHead = Arrays.equals(Arrays.copyOfRange(headerBytes, 256, 260), new byte[]{0x48, 0x45, 0x41, 0x44});
        SecureAreaStartAddr = Arrays.copyOfRange(headerBytes, 260, 264);
        bkupAreaStartAddr = Arrays.equals(Arrays.copyOfRange(headerBytes, 264, 268), new byte[]{(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff});
        titleKEKIndexBoth = headerBytes[268];
            titleKEKIndex = (byte) ((titleKEKIndexBoth >> 4) & (byte) 0x0F);
            KEKIndex = (byte) (titleKEKIndexBoth & 0x0F);
        gcSize = headerBytes[269];
        gcVersion = headerBytes[270];
        gcFlags = headerBytes[271];
        pkgID = Arrays.copyOfRange(headerBytes, 272, 280);
        valDataEndAddr = getLElong(headerBytes, 280);       //TODO: FIX/simplify //
        gcInfoIV = reverseBytes(Arrays.copyOfRange(headerBytes, 288, 304));
        hfs0partOffset = getLElong(headerBytes, 304);
        hfs0headerSize = getLElong(headerBytes, 312);
        hfs0headerSHA256 = Arrays.copyOfRange(headerBytes, 320, 352);
        hfs0initDataSHA256  = Arrays.copyOfRange(headerBytes, 352, 384);
        secureModeFlag = getLEint(headerBytes, 384);
        titleKeyFlag = getLEint(headerBytes, 388);
        keyFlag = getLEint(headerBytes, 392);
        normAreaEndAddr = Arrays.copyOfRange(headerBytes, 396, 400);
    }

    public byte[] getRsa2048PKCS1sig() { return rsa2048PKCS1sig; }
    public boolean isMagicHeadOk() { return magicHead; }
    public byte[] getSecureAreaStartAddr() { return SecureAreaStartAddr; }
    public boolean isBkupAreaStartAddrOk() { return bkupAreaStartAddr; }
    public byte getTitleKEKIndexBoth() { return titleKEKIndexBoth; }
    public byte getTitleKEKIndex() { return titleKEKIndex; }
    public byte getKEKIndex() { return KEKIndex; }

    public byte getGcSize() {
        return gcSize;
    }
    public byte getGcVersion() {
        return gcVersion;
    }
    public byte getGcFlags() {
        return gcFlags;
    }
    public byte[] getPkgID() {
        return pkgID;
    }
    public long getValDataEndAddr() {
        return valDataEndAddr;
    }
    public byte[] getGcInfoIV() {
        return gcInfoIV;
    }
    public long getHfs0partOffset() {
        return hfs0partOffset;
    }
    public long getHfs0headerSize() {
        return hfs0headerSize;
    }
    public byte[] getHfs0headerSHA256() {
        return hfs0headerSHA256;
    }
    public byte[] getHfs0initDataSHA256() {
        return hfs0initDataSHA256;
    }
    public int getSecureModeFlag() {
        return secureModeFlag;
    }
    public boolean isSecureModeFlagOk(){
        return secureModeFlag == 1;
    }
    public int getTitleKeyFlag() {
        return titleKeyFlag;
    }
    public boolean istitleKeyFlagOk(){
        return titleKeyFlag == 2;
    }
    public int getKeyFlag() {
        return keyFlag;
    }
    public boolean iskeyFlagOk(){
        return keyFlag == 0;
    }
    public byte[] getNormAreaEndAddr() {
        return normAreaEndAddr;
    }

    private byte[] reverseBytes(byte[] bArr){
        Byte[] objArr = new Byte[bArr.length];
        for (int i=0;i < bArr.length; i++)
            objArr[i] = bArr[i];
        List<Byte> bytesList = Arrays.asList(objArr);
        Collections.reverse(bytesList);
        for (int i=0;i < bArr.length; i++)
            bArr[i] = objArr[i];
        return bArr;
    }
}