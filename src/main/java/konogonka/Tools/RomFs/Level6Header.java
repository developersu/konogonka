/*
 * Copyright 2019-2020 Dmitry Isaenko
 *
 * This file is part of Konogonka.
 *
 * Konogonka is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Konogonka is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Konogonka.  If not, see <https://www.gnu.org/licenses/>.
 */

package konogonka.Tools.RomFs;

import konogonka.LoperConverter;
import konogonka.RainbowDump;

public class Level6Header {
    private long headerHeaderLength;
    private long headerDirectoryHashTableOffset;
    private long headerDirectoryHashTableLength;
    private long headerDirectoryMetadataTableOffset;
    private long headerDirectoryMetadataTableLength;
    private long headerFileHashTableOffset;
    private long headerFileHashTableLength;
    private long headerFileMetadataTableOffset;
    private long headerFileMetadataTableLength;
    private long headerFileDataOffset;
    
    private byte[] headerBytes;
    private int i;
    
    Level6Header(byte[] headerBytes){
        this.headerBytes = headerBytes;
        headerHeaderLength = getNext();
        headerDirectoryHashTableOffset = getNext();
        headerDirectoryHashTableLength = getNext();
        headerDirectoryMetadataTableOffset = getNext();
        headerDirectoryMetadataTableLength = getNext();
        headerFileHashTableOffset = getNext();
        headerFileHashTableLength = getNext();
        headerFileMetadataTableOffset = getNext();
        headerFileMetadataTableLength = getNext();
        headerFileDataOffset = getNext();

        System.out.println("== Level 6 Header ==\n" +
                "Header Length (always 0x50 ?)   "+ RainbowDump.formatDecHexString(headerHeaderLength)+"   (size of this structure within first 0x200 block of LEVEL 6 part)\n" +
                "Directory Hash Table Offset     "+ RainbowDump.formatDecHexString(headerDirectoryHashTableOffset)+"   (against THIS block where HEADER contains)\n" +
                "Directory Hash Table Length     "+ RainbowDump.formatDecHexString(headerDirectoryHashTableLength) + "\n" +
                "Directory Metadata Table Offset "+ RainbowDump.formatDecHexString(headerDirectoryMetadataTableOffset) + "\n" +
                "Directory Metadata Table Length "+ RainbowDump.formatDecHexString(headerDirectoryMetadataTableLength) + "\n" +
                "File Hash Table Offset          "+ RainbowDump.formatDecHexString(headerFileHashTableOffset) + "\n" +
                "File Hash Table Length          "+ RainbowDump.formatDecHexString(headerFileHashTableLength) + "\n" +
                "File Metadata Table Offset      "+ RainbowDump.formatDecHexString(headerFileMetadataTableOffset) + "\n" +
                "File Metadata Table Length      "+ RainbowDump.formatDecHexString(headerFileMetadataTableLength) + "\n" +
                "File Data Offset                "+ RainbowDump.formatDecHexString(headerFileDataOffset) + "\n" +
                "-------------------------------------------------------------"
        );
    }
    
    private long getNext(){
        final long result = LoperConverter.getLEint(headerBytes, i);
        i += 0x8;
        return result;
    }
    
    public long getHeaderHeaderLength() { return headerHeaderLength; }
    public long getHeaderDirectoryHashTableOffset() { return headerDirectoryHashTableOffset; }
    public long getHeaderDirectoryHashTableLength() { return headerDirectoryHashTableLength; }
    public long getHeaderDirectoryMetadataTableOffset() { return headerDirectoryMetadataTableOffset; }
    public long getHeaderDirectoryMetadataTableLength() { return headerDirectoryMetadataTableLength; }
    public long getHeaderFileHashTableOffset() { return headerFileHashTableOffset; }
    public long getHeaderFileHashTableLength() { return headerFileHashTableLength; }
    public long getHeaderFileMetadataTableOffset() { return headerFileMetadataTableOffset; }
    public long getHeaderFileMetadataTableLength() { return headerFileMetadataTableLength; }
    public long getHeaderFileDataOffset() { return headerFileDataOffset; }
}
