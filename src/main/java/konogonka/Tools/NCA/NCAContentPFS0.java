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

import konogonka.Tools.NCA.NCASectionTableBlock.NCASectionBlock;
import konogonka.Tools.PFS0.IPFS0Provider;
import konogonka.Tools.PFS0.PFS0EncryptedProvider;
import konogonka.Tools.PFS0.PFS0Provider;
import konogonka.ctraes.AesCtrDecryptSimple;

import java.io.*;
import java.util.LinkedList;

public class NCAContentPFS0 {
    private LinkedList<byte[]> SHA256hashes;
    private IPFS0Provider pfs0;

    // TODO: if decryptedKey is empty, thorow exception ??
    public NCAContentPFS0(File file, long offsetPosition, NCASectionBlock ncaSectionBlock, NCAHeaderTableEntry ncaHeaderTableEntry, byte[] decryptedKey){
        SHA256hashes = new LinkedList<>();
            // If it's PFS0Provider
            if (ncaSectionBlock.getSuperBlockPFS0() != null){
                try {
                    // IF NO ENCRYPTION
                    if (ncaSectionBlock.getCryptoType() == 0x1) {
                        RandomAccessFile raf = new RandomAccessFile(file, "r");
                        long thisMediaLocation = offsetPosition + (ncaHeaderTableEntry.getMediaStartOffset() * 0x200);
                        long hashTableLocation = thisMediaLocation + ncaSectionBlock.getSuperBlockPFS0().getHashTableOffset();
                        long pfs0Location = thisMediaLocation + ncaSectionBlock.getSuperBlockPFS0().getPfs0offset();

                        raf.seek(hashTableLocation);

                        byte[] rawData;
                        long sha256recordsNumber = ncaSectionBlock.getSuperBlockPFS0().getHashTableSize() / 0x20;
                        // Collect hashes
                        for (int i = 0; i < sha256recordsNumber; i++){
                            rawData = new byte[0x20];       // 32 bytes - size of SHA256 hash
                            if (raf.read(rawData) != -1)
                                SHA256hashes.add(rawData);
                            else {
                                raf.close();
                                return;                      // TODO: fix
                            }
                        }
                        raf.close();
                        // Get pfs0
                        pfs0 = new PFS0Provider(file, pfs0Location);
                    }
                    // If encrypted regular [ 0x03 ]
                    else if (ncaSectionBlock.getCryptoType() == 0x03){
                        new CryptoSection03(file,
                                offsetPosition,
                                decryptedKey,
                                ncaSectionBlock,
                                ncaHeaderTableEntry.getMediaStartOffset(),
                                ncaHeaderTableEntry.getMediaEndOffset());
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else if (ncaSectionBlock.getSuperBlockIVFC() != null){
                    // TODO
            }
            else {
                return;         // TODO: FIX THIS STUFF
            }
    }

    public LinkedList<byte[]> getSHA256hashes() { return SHA256hashes; }
    public IPFS0Provider getPfs0() { return pfs0; }

    private class CryptoSection03{
        
        CryptoSection03(File file, long offsetPosition, byte[] decryptedKey, NCASectionBlock ncaSectionBlock, long mediaStartBlocksOffset, long mediaEndBlocksOffset) throws Exception{
            /*//--------------------------------------------------------------------------------------------------
            System.out.println("Media start location: " + mediaStartBlocksOffset);
            System.out.println("Media end location:   " + mediaEndBlocksOffset);
            System.out.println("Media size          : " + (mediaEndBlocksOffset-mediaStartBlocksOffset));
            System.out.println("Media act. location:  " + (offsetPosition + (mediaStartBlocksOffset * 0x200)));
            System.out.println("SHA256 hash tbl size: " + ncaSectionBlock.getSuperBlockPFS0().getHashTableSize());
            System.out.println("SHA256 hash tbl offs: " + ncaSectionBlock.getSuperBlockPFS0().getHashTableOffset());
            System.out.println("PFS0 Offs:            " + ncaSectionBlock.getSuperBlockPFS0().getPfs0offset());
            System.out.println("SHA256 records:       " + (ncaSectionBlock.getSuperBlockPFS0().getHashTableSize() / 0x20));
            System.out.println("KEY:                  " + LoperConverter.byteArrToHexString(decryptedKey));
            System.out.println("CTR:                  " + LoperConverter.byteArrToHexString(ncaSectionBlock.getSectionCTR()));
            System.out.println();
            //--------------------------------------------------------------------------------------------------*/
            if (decryptedKey == null)
                throw new Exception("CryptoSection03: unable to proceed. No decrypted key provided.");

            RandomAccessFile raf = new RandomAccessFile(file, "r");
            long abosluteOffsetPosition = offsetPosition + (mediaStartBlocksOffset * 0x200);
            raf.seek(abosluteOffsetPosition);

            AesCtrDecryptSimple decryptor = new AesCtrDecryptSimple(decryptedKey, ncaSectionBlock.getSectionCTR(), mediaStartBlocksOffset * 0x200);

            byte[] encryptedBlock;
            byte[] dectyptedBlock;
            long mediaBlocksSize = mediaEndBlocksOffset - mediaStartBlocksOffset;
            // Prepare thread to parse encrypted data
            PipedOutputStream streamOut = new PipedOutputStream();
            PipedInputStream streamInp = new PipedInputStream(streamOut);

            Thread pThread = new Thread(new ParseThread(
                    streamInp,
                    ncaSectionBlock.getSuperBlockPFS0().getPfs0offset(),
                    ncaSectionBlock.getSuperBlockPFS0().getHashTableOffset(),
                    ncaSectionBlock.getSuperBlockPFS0().getHashTableSize(),
                    offsetPosition,
                    file,
                    decryptedKey,
                    ncaSectionBlock.getSectionCTR(),
                    mediaStartBlocksOffset,
                    mediaEndBlocksOffset
            ));
            pThread.start();
            // Decrypt data
            for (int i = 0; i < mediaBlocksSize; i++){
                encryptedBlock = new byte[0x200];
                if (raf.read(encryptedBlock) != -1){
                    //dectyptedBlock = aesCtr.decrypt(encryptedBlock);
                    dectyptedBlock = decryptor.dectyptNext(encryptedBlock);
                    // Writing decrypted data to pipe
                    try {
                        streamOut.write(dectyptedBlock);
                    }
                    catch (IOException e){
                        break;
                    }
                }
            }
            pThread.join();
            streamOut.close();
            raf.close();
            //****************************************___DEBUG___*******************************************************
            /*
            File contentFile = new File("/tmp/decryptedNCA0block_"+offsetPosition+".pfs0");
            BufferedOutputStream extractedFileOS = new BufferedOutputStream(new FileOutputStream(contentFile));

            raf = new RandomAccessFile(file, "r");
            raf.seek(abosluteOffsetPosition);
            decryptor = new AesCtrDecryptSimple(decryptedKey, ncaSectionBlock.getSectionCTR(), mediaStartBlocksOffset * 0x200);

            for (int i = 0; i < mediaBlocksSize; i++){
                encryptedBlock = new byte[0x200];
                if (raf.read(encryptedBlock) != -1){
                    //dectyptedBlock = aesCtr.decrypt(encryptedBlock);
                    dectyptedBlock = decryptor.dectyptNext(encryptedBlock);
                    // Writing decrypted data to pipe
                    try {
                        extractedFileOS.write(dectyptedBlock);
                    }
                    catch (IOException e){
                        System.out.println("Exception @extract");
                        break;
                    }
                }
            }
            extractedFileOS.close();
            raf.close();
            System.out.println("@extract done");
            //*//******************************************************************************************************/
        }
        /*
        * Since we representing decrypted data as stream (it's easier to look on it this way),
        * this thread will be parsing it.
        * */
        private class ParseThread implements Runnable{

            PipedInputStream pipedInputStream;

            long hashTableOffset;
            long hashTableSize;
            long hashTableRecordsCount;
            long pfs0offset;

            private long MetaOffsetPositionInFile;
            private File MetaFileWithEncPFS0;
            private byte[] MetaKey;
            private byte[] MetaSectionCTR;
            private long MetaMediaStartOffset;
            private long MetaMediaEndOffset;


            ParseThread(PipedInputStream pipedInputStream,
                        long pfs0offset,
                        long hashTableOffset,
                        long hashTableSize,

                        long MetaOffsetPositionInFile,
                        File MetaFileWithEncPFS0,
                        byte[] MetaKey,
                        byte[] MetaSectionCTR,
                        long MetaMediaStartOffset,
                        long MetaMediaEndOffset
            ){
                this.pipedInputStream = pipedInputStream;
                this.hashTableOffset = hashTableOffset;
                this.hashTableSize = hashTableSize;
                this.hashTableRecordsCount = hashTableSize / 0x20;
                this.pfs0offset = pfs0offset;

                this.MetaOffsetPositionInFile = MetaOffsetPositionInFile;
                this.MetaFileWithEncPFS0 = MetaFileWithEncPFS0;
                this.MetaKey = MetaKey;
                this.MetaSectionCTR = MetaSectionCTR;
                this.MetaMediaStartOffset = MetaMediaStartOffset;
                this.MetaMediaEndOffset = MetaMediaEndOffset;

            }

            @Override
            public void run() {
                long counter = 0;       // How many bytes already read

                try{
                    if (hashTableOffset > 0){
                        if (hashTableOffset != pipedInputStream.skip(hashTableOffset))
                            return;                                                     // TODO: fix?
                        counter = hashTableOffset;
                    }
                    // Loop for collecting all recrods from sha256 hash table
                    while ((counter - hashTableOffset) < hashTableSize){
                        int hashCounter = 0;
                        byte[] sectionHash = new byte[0x20];
                        // Loop for collecting bytes for every SINGLE records, where record size == 0x20
                        while (hashCounter < 0x20){
                            int currentByte = pipedInputStream.read();
                            if (currentByte == -1)
                                break;
                            sectionHash[hashCounter] = (byte)currentByte;
                            hashCounter++;
                            counter++;
                        }
                        // Write after collecting
                        SHA256hashes.add(sectionHash);  // From the NCAContentProvider obviously
                    }
                    // Skip padding and go to PFS0 location
                    if (counter < pfs0offset){
                        long toSkip = pfs0offset-counter;
                        if (toSkip != pipedInputStream.skip(toSkip))
                            return;                                                     // TODO: fix?
                        counter += toSkip;
                    }
                    //---------------------------------------------------------
                    pfs0 = new PFS0EncryptedProvider(pipedInputStream, counter,
                            MetaOffsetPositionInFile,
                            MetaFileWithEncPFS0,
                            MetaKey,
                            MetaSectionCTR,
                            MetaMediaStartOffset,
                            MetaMediaEndOffset);
                    pipedInputStream.close();
                }
                catch (Exception e){
                    System.out.println("'ParseThread' thread exception");
                    e.printStackTrace();
                }
                finally {
                    System.out.println("Thread dies");
                }
            }
        }
    }
}