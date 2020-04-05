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
package konogonka.Tools.NCA;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class NCAHeaderTableEntry {

    private long mediaStartOffset;
    private long mediaEndOffset;
    private byte[] unknwn1;
    private byte[] unknwn2;

    public NCAHeaderTableEntry(byte[] table) throws Exception{
        if (table.length < 0x10)
            throw new Exception("Section Table size is too small.");

        this.mediaStartOffset = convertUnsignedIntBytesToLong(Arrays.copyOfRange(table, 0x0, 0x4));
        this.mediaEndOffset = convertUnsignedIntBytesToLong(Arrays.copyOfRange(table, 0x4, 0x8));
        this.unknwn1 = Arrays.copyOfRange(table, 0x8, 0xC);
        this.unknwn2 = Arrays.copyOfRange(table, 0xC, 0x10);
    }

    private long convertUnsignedIntBytesToLong(byte[] intBytes){
        if (intBytes.length == 4)
            return ByteBuffer.wrap(Arrays.copyOf(intBytes, 8)).order(ByteOrder.LITTLE_ENDIAN).getLong();
        else
            return -1;
    }

    public long getMediaStartOffset() { return mediaStartOffset; }
    public long getMediaEndOffset() { return mediaEndOffset; }
    public byte[] getUnknwn1() { return unknwn1; }
    public byte[] getUnknwn2() { return unknwn2; }
}