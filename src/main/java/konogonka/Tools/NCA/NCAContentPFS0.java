package konogonka.Tools.NCA;

import konogonka.LoperConverter;
import konogonka.RainbowHexDump;
import konogonka.Tools.NCA.NCASectionTableBlock.NCASectionBlock;
import konogonka.Tools.PFS0.PFS0Provider;
import konogonka.ctraes.AesCtr;

import java.io.File;
import java.io.RandomAccessFile;
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
                            break;                      // TODO: fix
                    }
                    // Get pfs0
                    raf.seek(pfs0Location);
                    pfs0 = new PFS0Provider(file, pfs0Location);

                    raf.close();
                }
                // If encrypted (regular) todo: check keys provided
                else if (ncaSectionBlock.getCryptoType() == 0x3){           // d0c1...
                    //--------------------------------------------------------------------------------------------------
                    System.out.println("Media start location: " + ncaHeaderTableEntry.getMediaStartOffset());
                    System.out.println("Media end location:   " + ncaHeaderTableEntry.getMediaEndOffset());
                    System.out.println("Media size = sha h.tbl.size: " + (ncaHeaderTableEntry.getMediaEndOffset()-ncaHeaderTableEntry.getMediaStartOffset()));
                    System.out.println("Media act. location:  " + (offsetPosition + (ncaHeaderTableEntry.getMediaStartOffset() * 0x200)));
                    System.out.println("SHA256 hash tbl size: " + ncaSectionBlock.getSuperBlockPFS0().getHashTableSize());
                    System.out.println("SHA256 records:       " + (ncaSectionBlock.getSuperBlockPFS0().getHashTableSize() / 0x20));
                    System.out.println("KEY:                  " + LoperConverter.byteArrToHexString(decryptedKey));
                    System.out.println();
                    //--------------------------------------------------------------------------------------------------
                    long thisMediaLocation = offsetPosition + (ncaHeaderTableEntry.getMediaStartOffset() * 0x200);          // According to real file
                    long hashTableLocation = thisMediaLocation + ncaSectionBlock.getSuperBlockPFS0().getHashTableOffset();  // According to real file

                    raf.seek(thisMediaLocation);

                    try {
                        // IV for CTR == 32 bytes
                        byte[] IVarray = new byte[0x10];
                        // Populate first 8 bytes taken from Header's section Block CTR
                        System.arraycopy(LoperConverter.flip(ncaSectionBlock.getSectionCTR()), 0, IVarray,0, 8);
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
                        long mediaBlockSize = ncaHeaderTableEntry.getMediaEndOffset()-ncaHeaderTableEntry.getMediaStartOffset();

                        for (int i = 0; i < mediaBlockSize; i++){
                            encryptedBlock = new byte[0x200];
                            if (raf.read(encryptedBlock) != -1){
                                dectyptedBlock = aesCtr.decrypt(encryptedBlock);





                            }
                        }

                        /*
                        System.arraycopy(dectyptedBlock, 0, decryptedHeader, i * 0x200, 0x200);
                        */


                        //RainbowHexDump.hexDumpUTF8(dectyptedBlock);

                        /*
                        // Calculate hashes count
                        long sha256recordsNumber = ncaSectionBlock.getSuperBlockPFS0().getHashTableSize() / 0x20;

                        long currentHashStart = ncaSectionBlock.getSuperBlockPFS0().getHashTableOffset();
                        for (int i = 0; i < sha256recordsNumber; i++){
                            currentHashStart += i * 0x20;
                            encryptedBlock = Arrays.copyOfRange(dectyptedBlock, currentHashStart, currentHashStart + 0x20); //[0x20];       // 32 bytes - size of SHA256 hash
                            SHA256hashes.add(encryptedBlock);
                        }
                        */
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }


                    /*
                    byte[] rawData;
                    long sha256recordsNumber = ncaSectionBlock.getSuperBlockPFS0().getHashTableSize() / 0x20;
                    // Collect hashes
                    for (int i = 0; i < sha256recordsNumber; i++){
                        rawData = new byte[0x20];       // 32 bytes - size of SHA256 hash
                        if (raf.read(rawData) != -1)
                            SHA256hashes.add(rawData);
                        else
                            break;                      // TODO: fix
                    }
                    // Get pfs0
                    raf.seek(pfs0Location);

                    rawData = new byte[0x20];       // 32 bytes - size of SHA256 hash
                    */



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
}