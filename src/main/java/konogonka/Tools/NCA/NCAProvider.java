package konogonka.Tools.NCA;

import konogonka.Tools.NCA.NCASectionTableBlock.NCASectionBlock;
import konogonka.xtsaes.XTSAESCipher;
import org.bouncycastle.crypto.params.KeyParameter;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

import static konogonka.LoperConverter.getLElong;

// TODO: check file size
public class NCAProvider {
    private File file;                          // File that contains NCA
    private long offset;                        // Offset where NCA actually located
    private HashMap<String, String> keys;       // hashmap with keys using _0x naming (where x number 0-N)
    // Header
    private byte[] rsa2048one;
    private byte[] rsa2048two;
    private String magicnum;
    private byte systemOrGcIndicator;
    private byte contentType;
    private byte cryptoType1;                   // keyblob index. Considering as number within application/ocean/system
    private byte keyIndex;                      // application/ocean/system (kaek index?)
    private long ncaSize;                       // Size of this NCA (bytes)
    private byte[] titleId;
    private byte[] sdkVersion;                  // version ver_revision.ver_micro.vev_minor.ver_major
    private byte cryptoType2;                   // keyblob index. Considering as number within application/ocean/system
    private byte[] rightsId;

    private byte[] sha256hash0;
    private byte[] sha256hash1;
    private byte[] sha256hash2;
    private byte[] sha256hash3;

    private byte[] encryptedKey0;
    private byte[] encryptedKey1;
    private byte[] encryptedKey2;
    private byte[] encryptedKey3;

    private byte[] decryptedKey0;
    private byte[] decryptedKey1;
    private byte[] decryptedKey2;
    private byte[] decryptedKey3;

    private NCAHeaderTableEntry tableEntry0;
    private NCAHeaderTableEntry tableEntry1;
    private NCAHeaderTableEntry tableEntry2;
    private NCAHeaderTableEntry tableEntry3;

    private NCASectionBlock sectionBlock0;
    private NCASectionBlock sectionBlock1;
    private NCASectionBlock sectionBlock2;
    private NCASectionBlock sectionBlock3;

    public NCAProvider(File file, HashMap<String, String> keys) throws Exception{
        this(file, keys, 0);
    }

    public NCAProvider (File file, HashMap<String, String> keys, long offsetPosition) throws Exception{
        this.keys = keys;
        String header_key = keys.get("header_key");
        if (header_key == null )
            throw new Exception("header_key is not found within key set provided.");
        if (header_key.length() != 64)
            throw new Exception("header_key is too small or too big. Must be 64 symbols.");

        this.file = file;
        this.offset = offsetPosition;

        KeyParameter key1 = new KeyParameter(
                hexStrToByteArray(header_key.substring(0, 32))
        );
        KeyParameter key2 = new KeyParameter(
                hexStrToByteArray(header_key.substring(32, 64))
        );

        XTSAESCipher xtsaesCipher = new XTSAESCipher(false);
        xtsaesCipher.init(false, key1, key2);
        //-------------------------------------------------------------------------------------------------------------------------
        byte[] decryptedHeader = new byte[0xC00];

        RandomAccessFile raf = new RandomAccessFile(file, "r");
        byte[] encryptedSequence = new byte[0x200];
        byte[] decryptedSequence;

        raf.seek(offsetPosition);

        for (int i = 0; i < 6; i++){
            if (raf.read(encryptedSequence) != 0x200)
                throw new Exception("Read error "+i);
            decryptedSequence = new byte[0x200];
            xtsaesCipher.processDataUnit(encryptedSequence, 0, 0x200, decryptedSequence, 0, i);
            System.arraycopy(decryptedSequence, 0, decryptedHeader, i * 0x200, 0x200);
        }

        getHeader(decryptedHeader);

        raf.close();

        /*
        //---------------------------------------------------------------------
        FileInputStream fis = new FileInputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("/tmp/decrypted.nca"));
        int i = 0;
        byte[] block = new byte[0x200];
        while (fis.read(block) != -1){
            decryptedSequence = new byte[0x200];
            xtsaesCipher.processDataUnit(block, 0, 0x200, decryptedSequence, 0, i++);
            bos.write(decryptedSequence);
        }
        bos.close();
        //---------------------------------------------------------------------*/
    }

    private byte[] hexStrToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    private void getHeader(byte[] decryptedData) throws Exception{
        rsa2048one = Arrays.copyOfRange(decryptedData, 0, 0x100);
        rsa2048two = Arrays.copyOfRange(decryptedData, 0x100, 0x200);
        magicnum = new String(decryptedData, 0x200, 0x4, StandardCharsets.US_ASCII);
        systemOrGcIndicator = decryptedData[0x204];
        contentType = decryptedData[0x205];
        cryptoType1 = decryptedData[0x206];
        keyIndex = decryptedData[0x207];
        ncaSize = getLElong(decryptedData, 0x208);
        titleId = Arrays.copyOfRange(decryptedData, 0x210, 0x21C);   // 0x218 ?
        sdkVersion = Arrays.copyOfRange(decryptedData, 0x21c, 0x220);
        cryptoType2 = decryptedData[0x220];
        rightsId = Arrays.copyOfRange(decryptedData, 0x230, 0x240);
        byte[] tableBytes = Arrays.copyOfRange(decryptedData, 0x240, 0x280);
        byte[] sha256tableBytes = Arrays.copyOfRange(decryptedData, 0x280, 0x300);
        sha256hash0 = Arrays.copyOfRange(sha256tableBytes, 0, 0x20);
        sha256hash1 = Arrays.copyOfRange(sha256tableBytes, 0x20, 0x40);
        sha256hash2 = Arrays.copyOfRange(sha256tableBytes, 0x40, 0x60);
        sha256hash3 = Arrays.copyOfRange(sha256tableBytes, 0x60, 0x80);
        byte [] encryptedKeysArea = Arrays.copyOfRange(decryptedData, 0x300, 0x340);

        encryptedKey0 = Arrays.copyOfRange(encryptedKeysArea, 0, 0x10);
        encryptedKey1 = Arrays.copyOfRange(encryptedKeysArea, 0x10, 0x20);
        encryptedKey2 = Arrays.copyOfRange(encryptedKeysArea, 0x20, 0x30);
        encryptedKey3 = Arrays.copyOfRange(encryptedKeysArea, 0x30, 0x40);

        //todo: if nca3 proceed
        // If no rights ID (ticket?) exists
        if (Arrays.equals(rightsId, new byte[0x10])) {
            byte realCryptoType;
            if (cryptoType1 < cryptoType2)
                realCryptoType = cryptoType2;
            else
                realCryptoType = cryptoType1;

            if (realCryptoType > 0)     // TODO: CLARIFY WHY THEH FUCK IS IT FAIR????
                realCryptoType -= 1;


            String keyAreaKey;
            switch (keyIndex){
                case 0:
                    keyAreaKey = keys.get("key_area_key_application_0"+realCryptoType);
                    System.out.println("Using key_area_key_application_0"+realCryptoType);
                    break;
                case 1:
                    keyAreaKey = keys.get("key_area_key_ocean_0"+realCryptoType);
                    System.out.println("Using key_area_key_ocean_0"+realCryptoType);
                    break;
                case 2:
                    keyAreaKey = keys.get("key_area_key_system_0"+realCryptoType);
                    System.out.println("Using key_area_key_system_0"+realCryptoType);
                    break;
                default:
                    keyAreaKey = null;
            }

            if (keyAreaKey != null){
                SecretKeySpec skSpec = new SecretKeySpec(hexStrToByteArray(keyAreaKey), "AES");
                Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
                cipher.init(Cipher.DECRYPT_MODE, skSpec);
                decryptedKey0 = cipher.doFinal(encryptedKey0);
                decryptedKey1 = cipher.doFinal(encryptedKey1);
                decryptedKey2 = cipher.doFinal(encryptedKey2);
                decryptedKey3 = cipher.doFinal(encryptedKey3);
            }
        }
        else {

            // TODO

        }

        tableEntry0 = new NCAHeaderTableEntry(tableBytes);
        tableEntry1 = new NCAHeaderTableEntry(Arrays.copyOfRange(tableBytes, 0x10, 0x20));
        tableEntry2 = new NCAHeaderTableEntry(Arrays.copyOfRange(tableBytes, 0x20, 0x30));
        tableEntry3 = new NCAHeaderTableEntry(Arrays.copyOfRange(tableBytes, 0x30, 0x40));

        sectionBlock0 = new NCASectionBlock(Arrays.copyOfRange(decryptedData, 0x400, 0x600));
        sectionBlock1 = new NCASectionBlock(Arrays.copyOfRange(decryptedData, 0x600, 0x800));
        sectionBlock2 = new NCASectionBlock(Arrays.copyOfRange(decryptedData, 0x800, 0xa00));
        sectionBlock3 = new NCASectionBlock(Arrays.copyOfRange(decryptedData, 0xa00, 0xc00));
    }

    public byte[] getRsa2048one() { return rsa2048one; }
    public byte[] getRsa2048two() { return rsa2048two; }
    public String getMagicnum() { return magicnum; }
    public byte getSystemOrGcIndicator() { return systemOrGcIndicator; }
    public byte getContentType() { return contentType; }
    public byte getCryptoType1() { return cryptoType1; }
    public byte getKeyIndex() { return keyIndex; }
    public long getNcaSize() { return ncaSize; }
    public byte[] getTitleId() { return titleId; }
    public byte[] getSdkVersion() { return sdkVersion; }
    public byte getCryptoType2() { return cryptoType2; }
    public byte[] getRightsId() { return rightsId; }

    public byte[] getSha256hash0() { return sha256hash0; }
    public byte[] getSha256hash1() { return sha256hash1; }
    public byte[] getSha256hash2() { return sha256hash2; }
    public byte[] getSha256hash3() { return sha256hash3; }

    public byte[] getEncryptedKey0() { return encryptedKey0; }
    public byte[] getEncryptedKey1() { return encryptedKey1; }
    public byte[] getEncryptedKey2() { return encryptedKey2; }
    public byte[] getEncryptedKey3() { return encryptedKey3; }

    public byte[] getDecryptedKey0() { return decryptedKey0; }
    public byte[] getDecryptedKey1() { return decryptedKey1; }
    public byte[] getDecryptedKey2() { return decryptedKey2; }
    public byte[] getDecryptedKey3() { return decryptedKey3; }

    public NCAHeaderTableEntry getTableEntry0() { return tableEntry0; }
    public NCAHeaderTableEntry getTableEntry1() { return tableEntry1; }
    public NCAHeaderTableEntry getTableEntry2() { return tableEntry2; }
    public NCAHeaderTableEntry getTableEntry3() { return tableEntry3; }

    public NCASectionBlock getSectionBlock0() { return sectionBlock0; }
    public NCASectionBlock getSectionBlock1() { return sectionBlock1; }
    public NCASectionBlock getSectionBlock2() { return sectionBlock2; }
    public NCASectionBlock getSectionBlock3() { return sectionBlock3; }

    /**
     * Get content for the selected section
     * @param sectionNumber should be 1-4
     * */
    public NCAContentPFS0 getNCAContentPFS0(int sectionNumber){
        switch (sectionNumber){
            case 0:
                return new NCAContentPFS0(file, offset, sectionBlock0, tableEntry0, decryptedKey2);     // TODO: remove decryptedKey2
            case 1:
                return new NCAContentPFS0(file, offset, sectionBlock1, tableEntry1, decryptedKey2);
            case 2:
                return new NCAContentPFS0(file, offset, sectionBlock2, tableEntry2, decryptedKey2);
            case 3:
                return new NCAContentPFS0(file, offset, sectionBlock3, tableEntry3, decryptedKey2);
            default:
                return null;
        }
    }
}
// 0 OR 2  crypto type
// 0,1,2 kaek index
//settings.keyset.key_area_keys[ctx->crypto_type][ctx->header.kaek_ind]
            /*
            0x207 =
            0: key_area_key_application_  0x206 range:[0-6]; usually used 0 or 2
            1: key_area_key_ocean [0-6]
            2: key_area_key_system [0-6]

            if(ncahdr_x206 < ncahdr_x220){ret = ncahdr_x220; } else { ret = ncahdr_x206; } return ret;

            ret > 0? ret--

             */