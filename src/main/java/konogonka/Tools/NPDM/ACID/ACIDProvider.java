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
package konogonka.Tools.NPDM.ACID;

import konogonka.Tools.NPDM.KernelAccessControlProvider;
import konogonka.Tools.NPDM.ServiceAccessControlProvider;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static konogonka.LoperConverter.*;

public class ACIDProvider {

    private byte[] rsa2048signature;
    private byte[] rsa2048publicKey;
    private String magicNum;
    private int dataSize;
    private byte[] reserved1;
    private byte flag1;
    private byte flag2;
    private byte flag3;
    private byte flag4;
    private long titleRangeMin;
    private long titleRangeMax;
    private int fsAccessControlOffset;
    private int fsAccessControlSize;
    private int serviceAccessControlOffset;
    private int serviceAccessControlSize;
    private int kernelAccessControlOffset;
    private int kernelAccessControlSize;
    private byte[] reserved2;

    private FSAccessControlProvider fsAccessControlProvider;
    private ServiceAccessControlProvider serviceAccessControlProvider;
    private KernelAccessControlProvider kernelAccessControlProvider;

    public ACIDProvider(byte[] acidBytes) throws Exception{
        if (acidBytes.length < 0x240)
            throw new Exception("ACIDProvider -> ACI0 size is too short");
        rsa2048signature = Arrays.copyOfRange(acidBytes, 0, 0x100);
        rsa2048publicKey = Arrays.copyOfRange(acidBytes, 0x100, 0x200);
        magicNum = new String(acidBytes, 0x200, 0x4, StandardCharsets.UTF_8);
        dataSize = getLEint(acidBytes, 0x204);
        reserved1 = Arrays.copyOfRange(acidBytes, 0x208, 0x20C);
        flag1 = acidBytes[0x20C];
        flag2 = acidBytes[0x20D];
        flag3 = acidBytes[0x20E];
        flag4 = acidBytes[0x20F];
        titleRangeMin = getLElong(acidBytes, 0x210);
        titleRangeMax = getLElong(acidBytes, 0x218);
        fsAccessControlOffset = getLEint(acidBytes, 0x220);
        fsAccessControlSize = getLEint(acidBytes, 0x224);
        serviceAccessControlOffset = getLEint(acidBytes, 0x228);
        serviceAccessControlSize = getLEint(acidBytes, 0x22C);
        kernelAccessControlOffset = getLEint(acidBytes, 0x230);
        kernelAccessControlSize = getLEint(acidBytes, 0x234);
        reserved2 = Arrays.copyOfRange(acidBytes, 0x238, 0x240);
        if (fsAccessControlOffset > serviceAccessControlOffset || serviceAccessControlOffset > kernelAccessControlOffset )
            throw new Exception("ACIDProvider -> blocks inside the ACID are not sorted in ascending order. Only ascending order supported.");
        fsAccessControlProvider = new FSAccessControlProvider(Arrays.copyOfRange(acidBytes, fsAccessControlOffset, fsAccessControlOffset+fsAccessControlSize));
        serviceAccessControlProvider = new ServiceAccessControlProvider(Arrays.copyOfRange(acidBytes, serviceAccessControlOffset, serviceAccessControlOffset+serviceAccessControlSize));
        kernelAccessControlProvider = new KernelAccessControlProvider(Arrays.copyOfRange(acidBytes, kernelAccessControlOffset, kernelAccessControlOffset+kernelAccessControlSize));
    }

    public byte[] getRsa2048signature()  { return rsa2048signature; }
    public byte[] getRsa2048publicKey()  { return rsa2048publicKey; }
    public String getMagicNum()  { return magicNum; }
    public int getDataSize()  { return dataSize; }
    public byte[] getReserved1()  { return reserved1; }
    public byte getFlag1()  { return flag1; }
    public byte getFlag2()  { return flag2; }
    public byte getFlag3()  { return flag3; }
    public byte getFlag4()  { return flag4; }
    public long getTitleRangeMin()  { return titleRangeMin; }
    public long getTitleRangeMax()  { return titleRangeMax; }
    public int getFsAccessControlOffset()  { return fsAccessControlOffset; }
    public int getFsAccessControlSize()  { return fsAccessControlSize; }
    public int getServiceAccessControlOffset()  { return serviceAccessControlOffset; }
    public int getServiceAccessControlSize()  { return serviceAccessControlSize; }
    public int getKernelAccessControlOffset()  { return kernelAccessControlOffset; }
    public int getKernelAccessControlSize()  { return kernelAccessControlSize; }
    public byte[] getReserved2()  { return reserved2; }

    public FSAccessControlProvider getFsAccessControlProvider() { return fsAccessControlProvider; }
    public ServiceAccessControlProvider getServiceAccessControlProvider() { return serviceAccessControlProvider; }
    public KernelAccessControlProvider getKernelAccessControlProvider() { return kernelAccessControlProvider; }
}