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

import konogonka.ctraes.AesCtrDecryptSimple;

import java.io.*;
import java.util.Arrays;

public class RomFsEncryptedProvider implements IRomFsProvider{

    private long level6Offset;

    private File file;
    private Level6Header header;

    private FileSystemEntry rootEntry;

    //--------------------------------

    private long romFSoffsetPosition;
    private byte[] key;
    private byte[] sectionCTR;
    private long mediaStartOffset;
    private long mediaEndOffset;

    public RomFsEncryptedProvider(long romFSoffsetPosition,
                                  long level6Offset,
                                  File fileWithEncPFS0,
                                  byte[] key,
                                  byte[] sectionCTR,
                                  long mediaStartOffset,
                                  long mediaEndOffset
    ) throws Exception{
        this.file = fileWithEncPFS0;
        this.level6Offset = level6Offset;
        this.romFSoffsetPosition = romFSoffsetPosition;
        this.key = key;
        this.sectionCTR = sectionCTR;
        this.mediaStartOffset = mediaStartOffset;
        this.mediaEndOffset = mediaEndOffset;

        RandomAccessFile raf = new RandomAccessFile(file, "r");
        long abosluteOffsetPosition = romFSoffsetPosition + (mediaStartOffset * 0x200);
        raf.seek(abosluteOffsetPosition + level6Offset);

        AesCtrDecryptSimple decryptor = new AesCtrDecryptSimple(key, sectionCTR, mediaStartOffset * 0x200);
        //Go to Level 6 header
        decryptor.skipNext(level6Offset / 0x200);

        // Decrypt data
        byte[] encryptedBlock = new byte[0x200];
        byte[] dectyptedBlock;
        if (raf.read(encryptedBlock) == 0x200)
            dectyptedBlock = decryptor.dectyptNext(encryptedBlock);
        else
            throw new Exception("Failed to read header header (0x200 - block)");

        this.header = new Level6Header(dectyptedBlock);

        header.printDebugInfo();

        if (header.getDirectoryMetadataTableLength() < 0)
            throw new Exception("Not supported: DirectoryMetadataTableLength < 0");

        if (header.getFileMetadataTableLength() < 0)
            throw new Exception("Not supported: FileMetadataTableLength < 0");

        /*---------------------------------*/

        // Read directories metadata
        byte[] directoryMetadataTable = readMetaTable(abosluteOffsetPosition,
                header.getDirectoryMetadataTableOffset(),
                header.getDirectoryMetadataTableLength(),
                raf);

        // Read files metadata
        byte[] fileMetadataTable = readMetaTable(abosluteOffsetPosition,
                header.getFileMetadataTableOffset(),
                header.getFileMetadataTableLength(),
                raf);

        rootEntry = new FileSystemEntry(directoryMetadataTable, fileMetadataTable);

        raf.close();
    }
    
    private byte[] readMetaTable(long abosluteOffsetPosition,
                                 long metaOffset,
                                 long metaSize,
                                 RandomAccessFile raf) throws Exception{
        byte[] encryptedBlock;
        byte[] dectyptedBlock;
        byte[] metadataTable = new byte[(int) metaSize];
        //0
        AesCtrDecryptSimple decryptor = new AesCtrDecryptSimple(key, sectionCTR, mediaStartOffset * 0x200);

        long startBlock = metaOffset / 0x200;

        decryptor.skipNext(level6Offset / 0x200 + startBlock);

        raf.seek(abosluteOffsetPosition + level6Offset + startBlock * 0x200);

        //1
        long ignoreBytes = metaOffset - startBlock * 0x200;
        long currentPosition = 0;

        if (ignoreBytes > 0) {
            encryptedBlock = new byte[0x200];
            if (raf.read(encryptedBlock) == 0x200) {
                dectyptedBlock = decryptor.dectyptNext(encryptedBlock);
                // If we have extra-small file that is less then a block and even more
                if ((0x200 - ignoreBytes) > metaSize){
                    metadataTable = Arrays.copyOfRange(dectyptedBlock, (int)ignoreBytes, 0x200);
                    return metadataTable;
                }
                else {
                    System.arraycopy(dectyptedBlock, (int) ignoreBytes, metadataTable, 0, 0x200 - (int) ignoreBytes);
                    currentPosition = 0x200 - ignoreBytes;
                }
            }
            else {
                throw new Exception("RomFsEncryptedProvider(): Unable to get 512 bytes from 1st bock for Directory Metadata Table");
            }
            startBlock++;
        }
        long endBlock = (metaSize + ignoreBytes) / 0x200 + startBlock;  // <- pointing to place where any data related to this media-block ends

        //2
        int extraData = (int) ((endBlock - startBlock)*0x200 - (metaSize + ignoreBytes));
        
        if (extraData < 0)
            endBlock--;
        //3
        while ( startBlock < endBlock ) {
            encryptedBlock = new byte[0x200];
            if (raf.read(encryptedBlock) == 0x200) {
                dectyptedBlock = decryptor.dectyptNext(encryptedBlock);
                System.arraycopy(dectyptedBlock, 0, metadataTable, (int) currentPosition, 0x200);
            }
            else
                throw new Exception("RomFsEncryptedProvider(): Unable to get 512 bytes from block for Directory Metadata Table");

            startBlock++;
            currentPosition += 0x200;
        }

        //4
        if (extraData != 0){                 // In case we didn't get what we want
            encryptedBlock = new byte[0x200];
            if (raf.read(encryptedBlock) == 0x200) {
                dectyptedBlock = decryptor.dectyptNext(encryptedBlock);
                System.arraycopy(dectyptedBlock, 0, metadataTable, (int) currentPosition, Math.abs(extraData));
            }
            else
                throw new Exception("RomFsEncryptedProvider(): Unable to get 512 bytes from block for Directory Metadata Table");
        }

        return metadataTable;
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

                byte[] encryptedBlock;
                byte[] dectyptedBlock;

                RandomAccessFile raf = new RandomAccessFile(file, "r");

                //0
                AesCtrDecryptSimple decryptor = new AesCtrDecryptSimple(key, sectionCTR, mediaStartOffset * 0x200);

                long startBlock = (entry.getFileOffset() + header.getFileDataOffset()) / 0x200;

                decryptor.skipNext(level6Offset / 0x200 + startBlock);

                long abosluteOffsetPosition = romFSoffsetPosition + (mediaStartOffset * 0x200);

                raf.seek(abosluteOffsetPosition + level6Offset + startBlock * 0x200);

                //1
                long ignoreBytes = (entry.getFileOffset() + header.getFileDataOffset()) - startBlock * 0x200;

                if (ignoreBytes > 0) {
                    encryptedBlock = new byte[0x200];
                    if (raf.read(encryptedBlock) == 0x200) {
                        dectyptedBlock = decryptor.dectyptNext(encryptedBlock);
                        // If we have extra-small file that is less then a block and even more
                        if ((0x200 - ignoreBytes) > entry.getFileSize()){
                            streamOut.write(dectyptedBlock, (int)ignoreBytes, (int) entry.getFileSize());    // safe cast
                            raf.close();
                            streamOut.close();
                            return;
                        }
                        else {
                            streamOut.write(dectyptedBlock, (int) ignoreBytes, 0x200 - (int) ignoreBytes);
                        }
                    }
                    else {
                        throw new Exception("RomFsEncryptedProvider(): Unable to get 512 bytes from 1st bock for Directory Metadata Table");
                    }
                    startBlock++;
                }
                long endBlock = (entry.getFileSize() + ignoreBytes) / 0x200 + startBlock;  // <- pointing to place where any data related to this media-block ends

                //2
                int extraData = (int) ((endBlock - startBlock)*0x200 - (entry.getFileSize() + ignoreBytes));

                if (extraData < 0)
                    endBlock--;
                //3
                while ( startBlock < endBlock ) {
                    encryptedBlock = new byte[0x200];
                    if (raf.read(encryptedBlock) == 0x200) {
                        dectyptedBlock = decryptor.dectyptNext(encryptedBlock);
                        streamOut.write(dectyptedBlock);
                    }
                    else
                        throw new Exception("RomFsEncryptedProvider(): Unable to get 512 bytes from block for Directory Metadata Table");

                    startBlock++;
                }

                //4
                if (extraData != 0){                 // In case we didn't get what we want
                    encryptedBlock = new byte[0x200];
                    if (raf.read(encryptedBlock) == 0x200) {
                        dectyptedBlock = decryptor.dectyptNext(encryptedBlock);
                        streamOut.write(dectyptedBlock, 0, Math.abs(extraData));
                    }
                    else
                        throw new Exception("RomFsEncryptedProvider(): Unable to get 512 bytes from block for Directory Metadata Table");
                }
                raf.close();
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
