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
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            // If it's PFS0Provider
            if (ncaSectionBlock.getSuperBlockPFS0() != null){
                // IF NO ENCRYPTION
                if (ncaSectionBlock.getCryptoType() == 0x1) {
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
                // If encrypted (regular) todo: check keys provided
                else if (ncaSectionBlock.getCryptoType() == 0x3){           // d0c1...
                    if (decryptedKey == null)
                        return; // TODO: FIX

                    //--------------------------------------------------------------------------------------------------
                    System.out.println("Media start location: " + ncaHeaderTableEntry.getMediaStartOffset());
                    System.out.println("Media end location:   " + ncaHeaderTableEntry.getMediaEndOffset());
                    System.out.println("Media size          : " + (ncaHeaderTableEntry.getMediaEndOffset()-ncaHeaderTableEntry.getMediaStartOffset()));
                    System.out.println("Media act. location:  " + (offsetPosition + (ncaHeaderTableEntry.getMediaStartOffset() * 0x200)));
                    System.out.println("SHA256 hash tbl size: " + ncaSectionBlock.getSuperBlockPFS0().getHashTableSize());
                    System.out.println("SHA256 hash tbl offs: " + ncaSectionBlock.getSuperBlockPFS0().getHashTableOffset());
                    System.out.println("SHA256 records:       " + (ncaSectionBlock.getSuperBlockPFS0().getHashTableSize() / 0x20));
                    System.out.println("KEY:                  " + LoperConverter.byteArrToHexString(decryptedKey));
                    System.out.println("CTR:                  " + LoperConverter.byteArrToHexString(ncaSectionBlock.getSectionCTR()));
                    System.out.println("PFS0 Offs:            "+ncaSectionBlock.getSuperBlockPFS0().getPfs0offset());
                    System.out.println();
                    //--------------------------------------------------------------------------------------------------
                    long thisMediaLocation = offsetPosition + (ncaHeaderTableEntry.getMediaStartOffset() * 0x200);          // According to real file
                    long hashTableLocation = thisMediaLocation + ncaSectionBlock.getSuperBlockPFS0().getHashTableOffset();  // According to real file

                    raf.seek(thisMediaLocation);

                    try {
                        // IV for CTR == 32 bytes
                        byte[] IVarray = new byte[0x10];
                        // Populate first 8 bytes taken from Header's section Block CTR
                        System.arraycopy(LoperConverter.flip(ncaSectionBlock.getSectionCTR()), 0x0, IVarray,0x0, 0x8);
                        // Populate last 8 bytes calculated. Thanks hactool project!
                        // TODO: here is too much magic. It MUST be clarified and simplified
                        long mediaStrtOffReal = ncaHeaderTableEntry.getMediaStartOffset() * 0x200;                          // NOTE: long actually should be unsigned.. for calculation it's not critical, but for representation it is
                        mediaStrtOffReal >>= 4;
                        for (int i = 0; i < 0x8; i++){
                            IVarray[0x10-i-1] = (byte)(mediaStrtOffReal & 0xff);                                            // Note: issues could be here
                            mediaStrtOffReal >>= 8;
                        }

                        AesCtr aesCtr = new AesCtr(decryptedKey, IVarray);

                        byte[] encryptedBlock;
                        byte[] dectyptedBlock;
                        long mediaBlockSize = ncaHeaderTableEntry.getMediaEndOffset() - ncaHeaderTableEntry.getMediaStartOffset();
                        // Prepare thread to parse encrypted data
                        PipedOutputStream streamOut = new PipedOutputStream();
                        PipedInputStream streamInp = new PipedInputStream(streamOut);

                        new Thread(new ParseEncrypted(
                                SHA256hashes,
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
                                dectyptedBlock = aesCtr.decrypt(encryptedBlock);
                                // Writing decrypted data to pipe
                                streamOut.write(dectyptedBlock);
                            }
                        }
                        streamOut.flush();

                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                    raf.close();
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


    private class ParseEncrypted implements Runnable{

        LinkedList<byte[]> SHA256hashes;
        PipedInputStream pipedInputStream;

        long hashTableOffset;
        long hashTableSize;
        long hashTableRecordsCount;
        long pfs0offset;
        long pfs0size;

        ParseEncrypted(LinkedList<byte[]> SHA256hashes, PipedInputStream pipedInputStream, long pfs0offset, long pfs0size, long hashTableOffset, long hashTableSize){
            this.SHA256hashes = SHA256hashes;
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
                    while (counter < hashTableOffset) {
                        pipedInputStream.read();
                        counter++;
                    }
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
                        SHA256hashes.add(sectionHash);
                    }
                    // Skip padding and go to PFS0 location
                    if (counter < pfs0offset){
                        while (counter < pfs0offset){
                            pipedInputStream.read();
                            counter++;
                        }
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
                    break;
                }
            }
            catch (IOException ioe){
                System.out.println("'ParseEncrypted' thread exception");
                ioe.printStackTrace();
            }
            finally {
                System.out.println("Thread died.");
            }
        }
    }
}