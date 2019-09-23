package konogonka.Tools.NPDM.ACID;

import konogonka.LoperConverter;

import java.util.Arrays;

/**
 * For ACID Provider
 * */
public class FSAccessControlProvider {

    private byte version;
    private byte[] padding;
    private long permissionsBitmask;
    private byte[] reserved;

    public FSAccessControlProvider(byte[] bytes) {
        version = bytes[0];
        padding = Arrays.copyOfRange(bytes, 1, 0x4);
        permissionsBitmask =  LoperConverter.getLElong(bytes, 0x4);
        reserved = Arrays.copyOfRange(bytes, 0xC, 0x2C);
    }

    public byte getVersion() { return version; }
    public byte[] getPadding() { return padding; }
    public long getPermissionsBitmask() { return permissionsBitmask; }
    public byte[] getReserved() { return reserved; }
}
