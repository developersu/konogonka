package konogonka.Tools.PFS0;

import konogonka.RainbowHexDump;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static konogonka.LoperConverter.*;

public class PFS0Provider implements IPFS0Provider{
    private long rawFileDataStart;          // If -1 then this PFS0 located @ encrypted region

    private String magic;
    private int filesCount;
    private int stringTableSize;
    private byte[] padding;
    private PFS0subFile[] pfs0subFiles;

    public PFS0Provider(File fileWithPfs0) throws Exception{ this(fileWithPfs0, 0); }

    public PFS0Provider(File fileWithPfs0, long pfs0offsetPosition) throws Exception{

        RandomAccessFile raf = new RandomAccessFile(fileWithPfs0, "r");         // TODO: replace to bufferedInputStream

        raf.seek(pfs0offsetPosition);
        byte[] fileStartingBytes = new byte[0x10];
        // Read PFS0Provider, files count, header, padding (4 zero bytes)
        if (raf.read(fileStartingBytes) != 0x10){
            raf.close();
            throw new Exception("PFS0Provider: Unable to read starting bytes");
        }
        // Check PFS0Provider
        magic = new String(fileStartingBytes, 0x0, 0x4, StandardCharsets.US_ASCII);
        if (! magic.equals("PFS0")){
            raf.close();
            throw new Exception("PFS0Provider: Bad magic");
        }
        // Get files count
        filesCount = getLEint(fileStartingBytes, 0x4);
        if (filesCount <= 0 ) {
            raf.close();
            throw new Exception("PFS0Provider: Files count is too small");
        }
        // Get string table
        stringTableSize = getLEint(fileStartingBytes, 0x8);
        if (stringTableSize <= 0 ){
            raf.close();
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
        for (int i=0; i<filesCount; i++){
            if (raf.read(fileEntryTable) != 0x18)
                throw new Exception("PFS0Provider: String table is too small");
            offsetsSubFiles[i] = getLElong(fileEntryTable, 0);
            sizesSubFiles[i] = getLElong(fileEntryTable, 0x8);
            strTableOffsets[i] = getLEint(fileEntryTable, 0x10);
            zeroBytes[i] = Arrays.copyOfRange(fileEntryTable, 0x14, 0x18);
        }
        //**********************************************************************************************************
        // In here pointer in front of String table
        String[] subFileNames = new String[filesCount];
        byte[] stringTbl = new byte[stringTableSize];
        if (raf.read(stringTbl) != stringTableSize){
            throw new Exception("Read PFS0Provider String table failure. Can't read requested string table size ("+stringTableSize+")");
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
        rawFileDataStart = raf.getFilePointer();
        raf.close();
    }

    @Override
    public boolean isEncrypted() { return false; }
    @Override
    public String getMagic() { return magic; }
    @Override
    public int getFilesCount() { return filesCount; }
    @Override
    public int getStringTableSize() { return stringTableSize; }
    @Override
    public byte[] getPadding() { return padding; }
    @Override
    public long getRawFileDataStart() { return rawFileDataStart; }
    @Override
    public PFS0subFile[] getPfs0subFiles() { return pfs0subFiles; }
}
