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

import java.io.*;

public class RomFsDecryptedProvider implements IRomFsProvider{

    private long level6Offset;

    private File file;
    private Level6Header header;

    private FileSystemEntry rootEntry;

    public RomFsDecryptedProvider(File decryptedFsImageFile, long level6Offset) throws Exception{
        if (level6Offset < 0)
            throw new Exception("Incorrect Level 6 Offset");

        this.file = decryptedFsImageFile;

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(decryptedFsImageFile));

        this.level6Offset = level6Offset;

        skipBytes(bis, level6Offset);

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
    @Override
    public long getLevel6Offset() { return level6Offset; }
    @Override
    public Level6Header getHeader() { return header; }
    @Override
    public FileSystemEntry getRootEntry() { return rootEntry; }
    @Override
    public PipedInputStream getContent(FileSystemEntry entry) throws Exception{
        if (entry.isDirectory())
            throw new Exception("Request of the binary stream for the folder entry doesn't make sense.");

        PipedOutputStream streamOut = new PipedOutputStream();
        Thread workerThread;

        PipedInputStream streamIn = new PipedInputStream(streamOut);

        workerThread = new Thread(() -> {
            System.out.println("RomFsDecryptedProvider -> getContent(): Executing thread");
            try {
                long subFileRealPosition = level6Offset + header.getFileDataOffset() + entry.getFileOffset();
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                skipBytes(bis, subFileRealPosition);

                int readPice = 8388608; // 8mb NOTE: consider switching to 1mb 1048576

                long readFrom = 0;
                long realFileSize = entry.getFileSize();

                byte[] readBuf;

                while (readFrom < realFileSize) {
                    if (realFileSize - readFrom < readPice)
                        readPice = Math.toIntExact(realFileSize - readFrom);    // it's safe, I guarantee
                    readBuf = new byte[readPice];
                    if (bis.read(readBuf) != readPice) {
                        System.out.println("RomFsDecryptedProvider -> getContent(): Unable to read requested size from file.");
                        return;
                    }
                    streamOut.write(readBuf);
                    readFrom += readPice;
                }
                bis.close();
                streamOut.close();
            } catch (Exception e) {
                System.out.println("RomFsDecryptedProvider -> getContent(): Unable to provide stream");
                e.printStackTrace();
            }
            System.out.println("RomFsDecryptedProvider -> getContent(): Thread is dead");
        });
        workerThread.start();
        return streamIn;
    }
    @Override
    public File getFile() {
        return file;
    }

    private void printDebug(byte[] directoryMetadataTable, byte[] fileMetadataTable){
        new FolderMeta4Debug(header.getDirectoryMetadataTableLength(), directoryMetadataTable);
        new FileMeta4Debug(header.getFileMetadataTableLength(), fileMetadataTable);
        rootEntry.printTreeForDebug();
    }
}
