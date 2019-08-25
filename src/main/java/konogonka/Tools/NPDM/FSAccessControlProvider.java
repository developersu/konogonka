package konogonka.Tools.NPDM;

import java.util.Arrays;

/**
 * For ACID Provider
 * */
public class FSAccessControlProvider {

    private byte version;
    private byte[] padding;
    private byte[] permissionsBitmask;  // ??? Where each byte representing 255-long bit-bask of the thing it representing. Like 0x01 bit-mask for ApplicationInfo
    private byte[] reserved;

    public FSAccessControlProvider(byte[] bytes) {
        version = bytes[0];
        padding = Arrays.copyOfRange(bytes, 1, 0x4);
        permissionsBitmask = Arrays.copyOfRange(bytes, 0x4, 0xC);
        reserved = Arrays.copyOfRange(bytes, 0xC, 0x2C);
    }

    public byte getVersion() { return version; }
    public byte[] getPadding() { return padding; }
    public byte[] getPermissionsBitmask() { return permissionsBitmask; }
    public byte[] getReserved() { return reserved; }
}
