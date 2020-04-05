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

import static konogonka.LoperConverter.byteArrToHexString;
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
    private byte[] contentIndx;
    private byte[] sdkVersion;                  // version ver_revision.ver_micro.vev_minor.ver_major
    private byte cryptoType2;                   // keyblob index. Considering as number within application/ocean/system | AKA KeyGeneration
    private byte Header1SignatureKeyGeneration;
    private byte[] keyGenerationReserved;
    private byte[] rightsId;

    private byte cryptoTypeReal;

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
        titleId = Arrays.copyOfRange(decryptedData, 0x210, 0x218);
        contentIndx = Arrays.copyOfRange(decryptedData, 0x218, 0x21C);
        sdkVersion = Arrays.copyOfRange(decryptedData, 0x21c, 0x220);
        cryptoType2 = decryptedData[0x220];
        Header1SignatureKeyGeneration = decryptedData[0x221];
        keyGenerationReserved = Arrays.copyOfRange(decryptedData, 0x222, 0x230);
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

        // Calculate real Crypto Type
        if (cryptoType1 < cryptoType2)
            cryptoTypeReal = cryptoType2;
        else
            cryptoTypeReal = cryptoType1;

        if (cryptoTypeReal > 0)     // TODO: CLARIFY WHY THE FUCK IS IT FAIR????
            cryptoTypeReal -= 1;

        //todo: if nca3 proceed
        // Decrypt keys if encrypted
        if (Arrays.equals(rightsId, new byte[0x10])) {
            String keyAreaKey;
            switch (keyIndex){
                case 0:
                    keyAreaKey = keys.get(String.format("key_area_key_application_%02d", cryptoTypeReal));
                    break;
                case 1:
                    keyAreaKey = keys.get(String.format("key_area_key_ocean_%02d", cryptoTypeReal));
                    break;
                case 2:
                    keyAreaKey = keys.get(String.format("key_area_key_system_%02d", cryptoTypeReal));
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
            else{
                StringBuilder exceptionStringBuilder = new StringBuilder("key_area_key_");
                switch (keyIndex){
                    case 0:
                        exceptionStringBuilder.append("application_");
                        break;
                    case 1:
                        exceptionStringBuilder.append("ocean_");
                        break;
                    case 2:
                        exceptionStringBuilder.append("system_");
                        break;
                    default:
                        exceptionStringBuilder.append(keyIndex);
                        exceptionStringBuilder.append("[UNKNOWN]_");
                }
                exceptionStringBuilder.append(String.format("%02d", cryptoTypeReal));
                exceptionStringBuilder.append(" requested. Not supported or not found.");

                throw new Exception(exceptionStringBuilder.toString());
            }
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
    public byte[] getContentIndx() { return contentIndx; }
    public byte[] getSdkVersion() { return sdkVersion; }
    public byte getCryptoType2() { return cryptoType2; }
    public byte getHeader1SignatureKeyGeneration() { return Header1SignatureKeyGeneration; }
    public byte[] getKeyGenerationReserved() { return keyGenerationReserved; }
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

    public boolean isKeyAvailable(){                                            // TODO: USE
        if (Arrays.equals(rightsId, new byte[0x10]))
            return true;
        else
            return keys.containsKey(byteArrToHexString(rightsId));
    }
    /**
     * Get content for the selected section
     * @param sectionNumber should be 1-4
     * */
    public NCAContentPFS0 getNCAContentPFS0(int sectionNumber){
        byte[] key;

        // If empty Rights ID
        if (Arrays.equals(rightsId, new byte[0x10])) {
            key = decryptedKey2;                                       // TODO: Just remember this dumb hack
        }
        else {
            try {
                byte[] rightsIDkey = hexStrToByteArray(keys.get(byteArrToHexString(rightsId))); // throws NullPointerException

                SecretKeySpec skSpec = new SecretKeySpec(
                        hexStrToByteArray(keys.get(String.format("titlekek_%02d", cryptoTypeReal))
                        ), "AES");
                Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
                cipher.init(Cipher.DECRYPT_MODE, skSpec);
                key = cipher.doFinal(rightsIDkey);
            }
            catch (Exception e){
                e.printStackTrace();
                System.out.println("No title.keys loaded?");
                return null;
            }
        }
        switch (sectionNumber) {
            case 0:
                return new NCAContentPFS0(file, offset, sectionBlock0, tableEntry0, key);     // TODO: remove decryptedKey2 ?
            case 1:
                return new NCAContentPFS0(file, offset, sectionBlock1, tableEntry1, key);
            case 2:
                return new NCAContentPFS0(file, offset, sectionBlock2, tableEntry2, key);
            case 3:
                return new NCAContentPFS0(file, offset, sectionBlock3, tableEntry3, key);
            default:
                return null;
        }
    }
}