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
import konogonka.Tools.ISuperProvider;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PipedInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static konogonka.RainbowDump.formatDecHexString;

public class RomFsDecryptedProvider implements ISuperProvider {

    private static final long LEVEL_6_DEFAULT_OFFSET = 0x14000;

    private File decryptedFSImage;
    private Level6Header header;

    private FileSystemEntry rootEntry;

    public RomFsDecryptedProvider(File decryptedFSImage) throws Exception{     // TODO: add default setup AND using meta-data headers from NCA RomFs section (?)
        this.decryptedFSImage = decryptedFSImage;

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(decryptedFSImage));

        skipBytes(bis, LEVEL_6_DEFAULT_OFFSET);

        byte[] rawDataChunk = new byte[0x50];

        if (bis.read(rawDataChunk) != 0x50)
            throw new Exception("Failed to read header (0x50)");

        this.header = new Level6Header(rawDataChunk);
        /*
        // Print Dir Hash table as is:
        long seekTo = header.getDirectoryHashTableOffset() - 0x50;
        rawDataChunk = new byte[(int) header.getDirectoryHashTableLength()];
        skipTo(bis, seekTo);
        if (bis.read(rawDataChunk) != (int) header.getDirectoryHashTableLength())
            throw new Exception("Failed to read Dir hash table");
        RainbowDump.hexDumpUTF8(rawDataChunk);
        // Print Files Hash table as is:
        seekTo = header.getFileHashTableOffset() - header.getDirectoryMetadataTableOffset();
        rawDataChunk = new byte[(int) header.getFileHashTableLength()];
        skipTo(bis, seekTo);
        if (bis.read(rawDataChunk) != (int) header.getFileHashTableLength())
            throw new Exception("Failed to read Files hash table");
        RainbowDump.hexDumpUTF8(rawDataChunk);
        */
        // Read directories metadata
        long locationInFile = header.getDirectoryMetadataTableOffset() - 0x50;

        skipBytes(bis, locationInFile);

        if (header.getDirectoryMetadataTableLength() < 0)
            throw new Exception("Not supported operation.");

        byte[] directoryMetadataTable = new byte[(int) header.getDirectoryMetadataTableLength()];

        if (bis.read(directoryMetadataTable) != (int) header.getDirectoryMetadataTableLength())
            throw new Exception("Failed to read "+header.getDirectoryMetadataTableLength());
        // Read files metadata
        locationInFile = header.getFileMetadataTableOffset() - header.getFileHashTableOffset();      // TODO: replace to 'CurrentPosition'?

        skipBytes(bis, locationInFile);

        if (header.getFileMetadataTableLength() < 0)
            throw new Exception("Not supported operation.");

        byte[] fileMetadataTable = new byte[(int) header.getFileMetadataTableLength()];

        if (bis.read(fileMetadataTable) != (int) header.getFileMetadataTableLength())
            throw new Exception("Failed to read "+header.getFileMetadataTableLength());

        rootEntry = new FileSystemEntry(directoryMetadataTable, fileMetadataTable);
        //printDebug(directoryMetadataTable, fileMetadataTable);
        bis.close();
    }
    private void skipBytes(BufferedInputStream bis, long size) throws Exception{
        long mustSkip = size;
        long skipped = 0;
        while (mustSkip > 0){
            skipped += bis.skip(mustSkip);
            mustSkip = size - skipped;
        }
    }

    public Level6Header getHeader() { return header; }
    public FileSystemEntry getRootEntry() { return rootEntry; }

    @Override
    public PipedInputStream getProviderSubFilePipedInpStream(String subFileName) throws Exception {
        return null;
    }

    @Override
    public PipedInputStream getProviderSubFilePipedInpStream(int subFileNumber) throws Exception {
        throw new Exception("RomFsDecryptedProvider -> getProviderSubFilePipedInpStream(): Get files by number is not supported.");
    }

    @Override
    public File getFile() {
        return decryptedFSImage;
    }

    @Override
    public long getRawFileDataStart() {
        return 0;
    }

    private void printDebug(byte[] directoryMetadataTable, byte[] fileMetadataTable){
        new FolderMeta4Debug(header.getDirectoryMetadataTableLength(), directoryMetadataTable);
        new FileMeta4Debug(header.getFileMetadataTableLength(), fileMetadataTable);
        rootEntry.printTreeForDebug();
    }
}
