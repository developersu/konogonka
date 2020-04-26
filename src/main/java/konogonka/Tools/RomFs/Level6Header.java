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
    
    Level6Header(byte[] headerBytes){
        this.headerBytes = headerBytes;
        headerLength = getNext();
        directoryHashTableOffset = getNext();
        directoryHashTableLength = getNext();
        directoryMetadataTableOffset = getNext();
        directoryMetadataTableLength = getNext();
        fileHashTableOffset = getNext();
        fileHashTableLength = getNext();
        fileMetadataTableOffset = getNext();
        fileMetadataTableLength = getNext();
        fileDataOffset = getNext();
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
}
