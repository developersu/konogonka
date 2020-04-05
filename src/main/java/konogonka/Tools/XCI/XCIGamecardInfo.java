/*
    Copyright 2019-2020 Dmitry Isaenko

    This file is part of Konogonka.

    Konogonka is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Konogonka is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Konogonka.  If not, see <https://www.gnu.org/licenses/>.
*/
package konogonka.Tools.XCI;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

import static konogonka.LoperConverter.getLEint;
import static konogonka.LoperConverter.getLElong;

/**
 * Gamecard Info
 * */
public class XCIGamecardInfo{

    private long fwVersion;
    private byte[] accessCtrlFlags; // 0x00A10011 for 25MHz access or 0x00A10010 for 50MHz access
    private int readWaitTime1;
    private int readWaitTime2;
    private int writeWaitTime1;
    private int writeWaitTime2;
    private byte[] fwMode;
    private byte[] cupVersion;
    private byte[] emptyPadding1;
    private byte[] updPartHash;
    private byte[] cupID;
    private byte[] emptyPadding2;
                                                                                    // todo: Add factory function instead
    XCIGamecardInfo(byte[] infoBytes, byte[] IV, String XCI_HEADER_KEY) throws Exception {
        if (XCI_HEADER_KEY.trim().isEmpty())
            return;
        if (infoBytes.length != 112)
            throw new Exception("XCIGamecardInfo Incorrect array size. Expected 112 bytes while received "+infoBytes.length);

        IvParameterSpec gciIV = new IvParameterSpec(IV);
        SecretKeySpec skSpec = new SecretKeySpec(hexStrToByteArray(XCI_HEADER_KEY), "AES");

        try {
            // NOTE: CBC
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, skSpec, gciIV);

            byte[] decrypted = cipher.doFinal(infoBytes);

            fwVersion = getLElong(decrypted, 0);
            accessCtrlFlags = Arrays.copyOfRange(decrypted, 8, 12);
            readWaitTime1 = getLEint(decrypted, 12);
            readWaitTime2 = getLEint(decrypted, 16);
            writeWaitTime1 = getLEint(decrypted, 20);
            writeWaitTime2 = getLEint(decrypted, 24);
            fwMode = Arrays.copyOfRange(decrypted, 28, 32);
            cupVersion = Arrays.copyOfRange(decrypted, 32, 36);
            emptyPadding1 = Arrays.copyOfRange(decrypted, 36, 40);
            updPartHash = Arrays.copyOfRange(decrypted, 40, 48);
            cupID = Arrays.copyOfRange(decrypted, 48, 56);
            emptyPadding2 = Arrays.copyOfRange(decrypted, 56, 112);
            /*
            System.out.println(fwVersion);
            RainbowHexDump.hexDumpUTF8(accessCtrlFlags);
            System.out.println(readWaitTime1);
            System.out.println(readWaitTime2);
            System.out.println(writeWaitTime1);
            System.out.println(writeWaitTime2);
            RainbowHexDump.hexDumpUTF8(fwMode);
            RainbowHexDump.hexDumpUTF8(cupVersion);
            RainbowHexDump.hexDumpUTF8(emptyPadding1);
            RainbowHexDump.hexDumpUTF8(updPartHash);
            RainbowHexDump.hexDumpUTF8(cupID);
            RainbowHexDump.hexDumpUTF8(emptyPadding2);
            */
        } catch (Exception e) {
            throw new Exception("XCIGamecardInfo Decryption failed: \n  "+e.getMessage());
        }

    }
    private byte[] hexStrToByteArray(String s) {        // thanks stackoverflow
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public long getFwVersion() { return fwVersion; }
    public byte[] getAccessCtrlFlags() { return accessCtrlFlags; }
    public int getReadWaitTime1() { return readWaitTime1; }
    public int getReadWaitTime2() { return readWaitTime2; }
    public int getWriteWaitTime1() { return writeWaitTime1; }
    public int getWriteWaitTime2() { return writeWaitTime2; }

    public byte[] getFwMode() { return fwMode; }
    public byte[] getCupVersion() { return cupVersion; }
    public boolean isEmptyPadding1() { return Arrays.equals(emptyPadding1, new byte[4]); }
    public byte[] getEmptyPadding1() { return emptyPadding1; }
    public byte[] getUpdPartHash() { return updPartHash; }
    public byte[] getCupID() { return cupID; }
    public boolean isEmptyPadding2() { return Arrays.equals(emptyPadding2, new byte[56]); }
    public byte[] getEmptyPadding2() { return emptyPadding2; }
}
