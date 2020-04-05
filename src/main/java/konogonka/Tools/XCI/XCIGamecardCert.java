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

import java.util.Arrays;

/**
 * Gamecard Cert
 * */
public class XCIGamecardCert {
    private byte[] rsa2048PKCS1sig;
    private byte[] magicCert;
    private byte[] unknown1;
    private byte kekIndex;
    private byte[] unknown2;
    private byte[] deviceID;
    private byte[] unknown3;
    private byte[] encryptedData;

    XCIGamecardCert(byte[] certBytes) throws Exception{
        if (certBytes.length != 512)
            throw new Exception("XCIGamecardCert Incorrect array size. Expected 512 bytes while received "+certBytes.length);
        rsa2048PKCS1sig = Arrays.copyOfRange(certBytes, 0, 256);
        magicCert = Arrays.copyOfRange(certBytes, 256, 260);
        unknown1 = Arrays.copyOfRange(certBytes, 260, 264);
        kekIndex = certBytes[264];
        unknown2 = Arrays.copyOfRange(certBytes, 265, 272);
        deviceID = Arrays.copyOfRange(certBytes, 272, 288);
        unknown3 = Arrays.copyOfRange(certBytes, 288, 298);
        encryptedData = Arrays.copyOfRange(certBytes, 298, 512);
        /*
        RainbowHexDump.hexDumpUTF8(rsa2048PKCS1sig);
        RainbowHexDump.hexDumpUTF8(magicCert);
        RainbowHexDump.hexDumpUTF8(unknown1);
        System.out.println(kekIndex);
        RainbowHexDump.hexDumpUTF8(unknown2);
        RainbowHexDump.hexDumpUTF8(deviceID);
        RainbowHexDump.hexDumpUTF8(unknown3);
        RainbowHexDump.hexDumpUTF8(encryptedData);
        */
    }
    public byte[] getRsa2048PKCS1sig() { return rsa2048PKCS1sig; }
    public byte[] getMagicCert() { return magicCert; }
    public boolean isMagicCertOk(){ return Arrays.equals(magicCert, new byte[]{0x48, 0x45, 0x41, 0x44}); }
    public byte[] getUnknown1() { return unknown1; }
    public byte getKekIndex() { return kekIndex; }
    public byte[] getUnknown2() { return unknown2; }
    public byte[] getDeviceID() { return deviceID; }
    public byte[] getUnknown3() { return unknown3; }
    public byte[] getEncryptedData() { return encryptedData; }
}
