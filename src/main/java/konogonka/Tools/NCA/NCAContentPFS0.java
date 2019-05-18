package konogonka.Tools.NCA;

import konogonka.RainbowHexDump;
import konogonka.Tools.NCA.NCASectionTableBlock.NCASectionBlock;
import konogonka.Tools.PFS0.PFS0Provider;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.RandomAccessFile;
import java.security.Security;
import java.util.LinkedList;

public class NCAContentPFS0 {
    private LinkedList<byte[]> SHA256hashes;
    private PFS0Provider pfs0;

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
                // If encrypted (regular)
                else if (ncaSectionBlock.getCryptoType() == 0x3){
                    long thisMediaLocation = offsetPosition + (ncaHeaderTableEntry.getMediaStartOffset() * 0x200);              // todo: use this location for CTR
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

                    rawData = new byte[0x20];       // 32 bytes - size of SHA256 hash

                    if (raf.read(rawData) != -1) {
                        System.out.println("Encrypted");
                        RainbowHexDump.hexDumpUTF8(rawData);
                    }
                    try {
                        /*
                        System.out.println("Decrypted?");
                        Security.addProvider(new BouncyCastleProvider());       // TODO: DO FUCKING REMEMBER THIS SHIT FOR CTR
                        IvParameterSpec iv = new IvParameterSpec(new byte[10]);
                        SecretKeySpec key = new SecretKeySpec(decryptedKey, "AES");
                        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding", "BC");
                        cipher.init(Cipher.DECRYPT_MODE, key, iv);
                        byte[] decr = cipher.doFinal(rawData);
                        RainbowHexDump.hexDumpUTF8(decr);

                         */
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
}
