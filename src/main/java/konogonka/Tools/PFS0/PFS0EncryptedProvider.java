package konogonka.Tools.PFS0;

import java.io.PipedInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static konogonka.LoperConverter.getLEint;
import static konogonka.LoperConverter.getLElong;

public class PFS0EncryptedProvider implements IPFS0Provider{
    private long rawFileDataStart;          // If -1 then this PFS0 located @ encrypted region

    private String magic;
    private int filesCount;
    private int stringTableSize;
    private byte[] padding;
    private PFS0subFile[] pfs0subFiles;

    //---------------------------------------
    /*
    absOffsetPosOfMediaBlock

    Counter - PFS0 Position
    mediaBlockSize - PFS0 Subsustem Size
    * */
    //---------------------------------------

    public PFS0EncryptedProvider(PipedInputStream pipedInputStream) throws Exception{
        byte[] fileStartingBytes = new byte[0x10];
        // Read PFS0Provider, files count, header, padding (4 zero bytes)

        for (int i = 0; i < 0x10; i++){
            int currentByte = pipedInputStream.read();
            if (currentByte == -1) {
                throw new Exception("PFS0: Reading stream suddenly ended while trying to read starting 0x10 bytes");
            }
            fileStartingBytes[i] = (byte)currentByte;
        }
        // Check PFS0Provider
        magic = new String(fileStartingBytes, 0x0, 0x4, StandardCharsets.US_ASCII);
        if (! magic.equals("PFS0")){
            throw new Exception("PFS0Provider: Bad magic");
        }
        // Get files count
        filesCount = getLEint(fileStartingBytes, 0x4);
        if (filesCount <= 0 ) {
            throw new Exception("PFS0Provider: Files count is too small");
        }
        // Get string table
        stringTableSize = getLEint(fileStartingBytes, 0x8);
        if (stringTableSize <= 0 ){
            throw new Exception("PFS0Provider: String table is too small");
        }
        padding = Arrays.copyOfRange(fileStartingBytes, 0xc, 0x10);
        //---------------------------------------------------------------------------------------------------------
        pfs0subFiles = new PFS0subFile[filesCount];

        long[] offsetsSubFiles = new long[filesCount];
        long[] sizesSubFiles = new long[filesCount];
        int[] strTableOffsets = new int[filesCount];
        byte[][] zeroBytes = new byte[filesCount][];

        byte[] fileEntryTable = new byte[0x18];
        for (int i=0; i < filesCount; i++){
            for (int j = 0; j < 0x18; j++){
                int currentByte = pipedInputStream.read();
                if (currentByte == -1) {
                    throw new Exception("PFS0: Reading stream suddenly ended while trying to read File Entry Table #"+i);
                }
                fileEntryTable[j] = (byte)currentByte;
            }
            offsetsSubFiles[i] = getLElong(fileEntryTable, 0);
            sizesSubFiles[i] = getLElong(fileEntryTable, 0x8);
            strTableOffsets[i] = getLEint(fileEntryTable, 0x10);
            zeroBytes[i] = Arrays.copyOfRange(fileEntryTable, 0x14, 0x18);
        }
        //**********************************************************************************************************
        // In here pointer in front of String table
        String[] subFileNames = new String[filesCount];
        byte[] stringTbl = new byte[stringTableSize];

        for (int i = 0; i < stringTableSize; i++){
            int currentByte = pipedInputStream.read();
            if (currentByte == -1) {
                throw new Exception("PFS0: Reading stream suddenly ended while trying to read string table");
            }
            stringTbl[i] = (byte)currentByte;
        }

        for (int i=0; i < filesCount; i++){
            int j = 0;
            while (stringTbl[strTableOffsets[i]+j] != (byte)0x00)
                j++;
            subFileNames[i] = new String(stringTbl, strTableOffsets[i], j, StandardCharsets.UTF_8);
        }
        for (int i = 0; i < filesCount; i++){
            pfs0subFiles[i] = new PFS0subFile(
                    subFileNames[i],
                    offsetsSubFiles[i],
                    sizesSubFiles[i],
                    zeroBytes[i]
            );
        }
        rawFileDataStart = -1;
    }

    public String getMagic() { return magic; }
    public int getFilesCount() { return filesCount; }
    public int getStringTableSize() { return stringTableSize; }
    public byte[] getPadding() { return padding; }

    public long getRawFileDataStart() { return rawFileDataStart; }
    public PFS0subFile[] getPfs0subFiles() { return pfs0subFiles; }
}
