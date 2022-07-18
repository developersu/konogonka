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

import java.util.Arrays;

public class Level6Header {
    private long headerLength;
    private long directoryHashTableOffset;
    private long directoryHashTableLength;
    private long directoryMetadataTableOffset;
    private long directoryMetadataTableLength;
    private long fileHashTableOffset;
    private long fileHashTableLength;
    private long fileMetadataTableOffset;
    private long fileMetadataTableLength;
    private long fileDataOffset;
    
    private byte[] headerBytes;
    private int i;
    
    Level6Header(byte[] headerBytes) throws Exception{
        this.headerBytes = headerBytes;
        if (headerBytes.length < 0x50)
            throw new Exception("Level 6 Header section is too small");
        headerLength = getNext();
        directoryHashTableOffset = getNext();
        directoryHashTableOffset <<= 32;
        directoryHashTableLength = getNext();
        directoryMetadataTableOffset = getNext();
        directoryMetadataTableLength = getNext();
        fileHashTableOffset = getNext();
        fileHashTableLength = getNext();
        fileMetadataTableOffset = getNext();
        fileMetadataTableLength = getNext();
        fileDataOffset = getNext();
        RainbowDump.hexDumpUTF8(Arrays.copyOfRange(headerBytes, 0, 0x50));
    }
    
    private long getNext(){
        final long result = LoperConverter.getLEint(headerBytes, i);
        i += 0x8;
        return result;
    }
    
    public long getHeaderLength() { return headerLength; }
    public long getDirectoryHashTableOffset() { return directoryHashTableOffset; }
    public long getDirectoryHashTableLength() { return directoryHashTableLength; }
    public long getDirectoryMetadataTableOffset() { return directoryMetadataTableOffset; }
    public long getDirectoryMetadataTableLength() { return directoryMetadataTableLength; }
    public long getFileHashTableOffset() { return fileHashTableOffset; }
    public long getFileHashTableLength() { return fileHashTableLength; }
    public long getFileMetadataTableOffset() { return fileMetadataTableOffset; }
    public long getFileMetadataTableLength() { return fileMetadataTableLength; }
    public long getFileDataOffset() { return fileDataOffset; }
    
    public void printDebugInfo(){
        System.out.println("== Level 6 Header ==\n" +
                "Header Length (always 0x50 ?)   "+ RainbowDump.formatDecHexString(headerLength)+"   (size of this structure within first 0x200 block of LEVEL 6 part)\n" +
                "Directory Hash Table Offset     "+ RainbowDump.formatDecHexString(directoryHashTableOffset)+"   (against THIS block where HEADER contains)\n" +
                "Directory Hash Table Length     "+ RainbowDump.formatDecHexString(directoryHashTableLength) + "\n" +
                "Directory Metadata Table Offset "+ RainbowDump.formatDecHexString(directoryMetadataTableOffset) + "\n" +
                "Directory Metadata Table Length "+ RainbowDump.formatDecHexString(directoryMetadataTableLength) + "\n" +
                "File Hash Table Offset          "+ RainbowDump.formatDecHexString(fileHashTableOffset) + "\n" +
                "File Hash Table Length          "+ RainbowDump.formatDecHexString(fileHashTableLength) + "\n" +
                "File Metadata Table Offset      "+ RainbowDump.formatDecHexString(fileMetadataTableOffset) + "\n" +
                "File Metadata Table Length      "+ RainbowDump.formatDecHexString(fileMetadataTableLength) + "\n" +
                "File Data Offset                "+ RainbowDump.formatDecHexString(fileDataOffset) + "\n" +
                "-------------------------------------------------------------"
        );
    }
}
