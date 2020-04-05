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
package konogonka.Tools.NCA.NCASectionTableBlock;

import java.util.Arrays;

import static konogonka.LoperConverter.getLEint;
import static konogonka.LoperConverter.getLElong;

public class SuperBlockPFS0 {
    private byte[] SHA256hash;
    private int blockSize;
    private int unknownNumberTwo;
    private long hashTableOffset;
    private long hashTableSize;
    private long pfs0offset;
    private long pfs0size;
    private byte[] zeroes;

    SuperBlockPFS0(byte[] sbBytes){
        SHA256hash = Arrays.copyOfRange(sbBytes, 0, 0x20);
        blockSize = getLEint(sbBytes, 0x20);
        unknownNumberTwo = getLEint(sbBytes, 0x24);
        hashTableOffset = getLElong(sbBytes, 0x28);
        hashTableSize = getLElong(sbBytes, 0x30);
        pfs0offset = getLElong(sbBytes, 0x38);
        pfs0size = getLElong(sbBytes, 0x40);
        zeroes = Arrays.copyOfRange(sbBytes, 0x48, 0xf8);
    }

    public byte[] getSHA256hash() { return SHA256hash; }
    public int getBlockSize() { return blockSize; }
    public int getUnknownNumberTwo() { return unknownNumberTwo; }
    public long getHashTableOffset() { return hashTableOffset; }
    public long getHashTableSize() { return hashTableSize; }
    public long getPfs0offset() { return pfs0offset; }
    public long getPfs0size() { return pfs0size; }
    public byte[] getZeroes() { return zeroes; }
}
