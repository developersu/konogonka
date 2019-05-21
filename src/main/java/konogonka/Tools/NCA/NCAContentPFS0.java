package konogonka.Tools.NCA;

import konogonka.LoperConverter;
import konogonka.RainbowHexDump;
import konogonka.Tools.NCA.NCASectionTableBlock.NCASectionBlock;
import konogonka.Tools.PFS0.PFS0Provider;
import konogonka.ctraes.AesCtr;

import java.io.*;
import java.util.LinkedList;

public class NCAContentPFS0 {
    private LinkedList<byte[]> SHA256hashes;
    private PFS0Provider pfs0;

    // TODO: if decryptedKey is empty, thorow exception ??
    public NCAContentPFS0(File file, long offsetPosition, NCASectionBlock ncaSectionBlock, NCAHeaderTableEntry ncaHeaderTableEntry, byte[] decryptedKey){
        SHA256hashes = new LinkedList<>();
        try {
            // If it's PFS0Provider
            if (ncaSectionBlock.getSuperBlockPFS0() != null){
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
                        else
                            return;                      // TODO: fix
                    }
                    raf.close();
                    // Get pfs0
                    pfs0 = new PFS0Provider(file, pfs0Location);
                }
                // If encrypted (regular)
                else if (ncaSectionBlock.getCryptoType() == 0x3){           // d0c1...
                    new CryptoSection03(file,
                            offsetPosition,
                            decryptedKey,
                            ncaSectionBlock,
                            ncaHeaderTableEntry.getMediaStartOffset(),
                            ncaHeaderTableEntry.getMediaEndOffset());
                }
            }
            else if (ncaSectionBlock.getSuperBlockIVFC() != null){

            }
            else {
                return;         // TODO: FIX THIS STUFF
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public LinkedList<byte[]> getSHA256hashes() { return SHA256hashes; }
    public PFS0Provider getPfs0() { return pfs0; }

    private class CryptoSection03{
        
        CryptoSection03(File file, long offsetPosition, byte[] decryptedKey, NCASectionBlock ncaSectionBlock, long mediaStartOffset, long mediaEndOffset) throws Exception{
            //--------------------------------------------------------------------------------------------------
            System.out.println("Media start location: " + mediaStartOffset);
            System.out.println("Media end location:   " + mediaEndOffset);
            System.out.println("Media size          : " + (mediaEndOffset-mediaStartOffset));
            System.out.println("Media act. location:  " + (offsetPosition + (mediaStartOffset * 0x200)));
            System.out.println("SHA256 hash tbl size: " + ncaSectionBlock.getSuperBlockPFS0().getHashTableSize());
            System.out.println("SHA256 hash tbl offs: " + ncaSectionBlock.getSuperBlockPFS0().getHashTableOffset());
            System.out.println("PFS0 Offs:            " + ncaSectionBlock.getSuperBlockPFS0().getPfs0offset());
            System.out.println("SHA256 records:       " + (ncaSectionBlock.getSuperBlockPFS0().getHashTableSize() / 0x20));
            System.out.println("KEY:                  " + LoperConverter.byteArrToHexString(decryptedKey));
            System.out.println("CTR:                  " + LoperConverter.byteArrToHexString(ncaSectionBlock.getSectionCTR()));
            System.out.println();
            //--------------------------------------------------------------------------------------------------

            if (decryptedKey == null)
                throw new Exception("CryptoSection03: unable to proceed. No decrypted key provided.");

            RandomAccessFile raf = new RandomAccessFile(file, "r");
            raf.seek(offsetPosition + (mediaStartOffset * 0x200));

            AesCtrDecryptor decryptor = new AesCtrDecryptor(decryptedKey, ncaSectionBlock.getSectionCTR(), mediaStartOffset * 0x200);

            byte[] encryptedBlock;
            byte[] dectyptedBlock;
            long mediaBlockSize = mediaEndOffset - mediaStartOffset;
            // Prepare thread to parse encrypted data
            PipedOutputStream streamOut = new PipedOutputStream();
            PipedInputStream streamInp = new PipedInputStream(streamOut);

            new Thread(new ParseThread(
                    streamInp,
                    ncaSectionBlock.getSuperBlockPFS0().getPfs0offset(),
                    ncaSectionBlock.getSuperBlockPFS0().getPfs0size(),
                    ncaSectionBlock.getSuperBlockPFS0().getHashTableOffset(),
                    ncaSectionBlock.getSuperBlockPFS0().getHashTableSize()
            )).start();
            // Decrypt data
            for (int i = 0; i < mediaBlockSize; i++){
                encryptedBlock = new byte[0x200];
                if (raf.read(encryptedBlock) != -1){
                    //dectyptedBlock = aesCtr.decrypt(encryptedBlock);
                    dectyptedBlock = decryptor.dectyptNext(encryptedBlock);
                    // Writing decrypted data to pipe
                    streamOut.write(dectyptedBlock);
                }
            }
            streamOut.flush();
            streamOut.close();

            raf.close();
        }
        /*
        * Simplify decryption of the CTR
        * */
        private class AesCtrDecryptor{

            private long realMediaOffset;
            byte[] IVarray;
            private AesCtr aesCtr;

            AesCtrDecryptor(byte[] decryptedKey, byte[] sectionCTR, long realMediaOffset) throws Exception{
                this.realMediaOffset = realMediaOffset;
                aesCtr = new AesCtr(decryptedKey);
                // IV for CTR == 16 bytes
                IVarray = new byte[0x10];
                // Populate first 8 bytes taken from Header's section Block CTR
                System.arraycopy(LoperConverter.flip(sectionCTR), 0x0, IVarray,0x0, 0x8);
            }

            public byte[] dectyptNext(byte[] enctyptedBlock) throws Exception{
                updateIV(realMediaOffset);
                byte[] decryptedBlock = aesCtr.decrypt(enctyptedBlock, IVarray);
                realMediaOffset += 0x200;
                return decryptedBlock;
            }
            // Populate last 8 bytes calculated. Thanks hactool project!
            private void updateIV(long offset){
                offset >>= 4;
                for (int i = 0; i < 0x8; i++){
                    IVarray[0x10-i-1] = (byte)(offset & 0xff);                                            // Note: issues could be here
                    offset >>= 8;
                }
            }
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
            long pfs0size;

            ParseThread(PipedInputStream pipedInputStream, long pfs0offset, long pfs0size, long hashTableOffset, long hashTableSize){
                this.pipedInputStream = pipedInputStream;
                this.hashTableOffset = hashTableOffset;
                this.hashTableSize = hashTableSize;
                this.hashTableRecordsCount = hashTableSize / 0x20;
                this.pfs0offset = pfs0offset;
                this.pfs0size = pfs0size;
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
                    // Main loop
                    while (true){
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
                            if ((pfs0offset-counter) != pipedInputStream.skip(pfs0offset-counter))
                                return;                                                     // TODO: fix?
                            counter += pfs0offset-counter;
                        }
                        //---------------------------------------------------------
                        byte[] magic = new byte[0x4];
                        for (int i = 0; i < 4; i++){
                            int currentByte = pipedInputStream.read();
                            if (currentByte == -1)
                                break;
                            magic[i] = (byte)currentByte;
                        }
                        RainbowHexDump.hexDumpUTF8(magic);
                        while (pipedInputStream.read() != -1)
                            ;
                        break;
                    }
                }
                catch (IOException ioe){
                    System.out.println("'ParseThread' thread exception");
                    ioe.printStackTrace();
                }
                finally {
                    System.out.println("ParseThread thread died.");
                }
            }
        }
    }
}