package konogonka.Tools.XCI;

import konogonka.RainbowHexDump;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;

import static konogonka.LoperConverter.*;

/**
 * HFS0
 * */
public class HFS0Provider {

    private boolean magicHFS0;
    private int filesCnt;
    private boolean paddingHfs0;
    private int stringTableSize;
    private long rawFileDataStart;

    private HFS0File[] hfs0Files;

    HFS0Provider(long hfsOffsetPosition, RandomAccessFile raf) throws Exception{
        byte[] hfs0bytes = new byte[16];
        try{
            raf.seek(hfsOffsetPosition);
            if (raf.read(hfs0bytes) != 16){
                throw new Exception("Read HFS0 structure failure. Can't read first 16 bytes on requested offset.");
            }
        }
        catch (IOException ioe){
            throw new Exception("Read HFS0 structure failure. Can't read first 16 bytes on requested offset: "+ioe.getMessage());
        }
        magicHFS0 = Arrays.equals(Arrays.copyOfRange(hfs0bytes, 0, 4),new byte[]{0x48, 0x46, 0x53, 0x30});
        filesCnt = getLEint(hfs0bytes, 0x4);
        stringTableSize = getLEint(hfs0bytes, 8);
        paddingHfs0 = Arrays.equals(Arrays.copyOfRange(hfs0bytes, 12, 16),new byte[4]);

        hfs0Files = new HFS0File[filesCnt];

        // TODO: IF NOT EMPTY TABLE:

        long[] offsetHfs0files = new long[filesCnt];
        long[] sizeHfs0files = new long[filesCnt];
        int[] hashedRegionSizeHfs0Files = new int[filesCnt];
        boolean[] paddingHfs0Files = new boolean[filesCnt];
        byte[][] SHA256HashHfs0Files = new byte[filesCnt][];
        int[] strTableOffsets = new int[filesCnt];

        try {
            // Populate meta information regarding each file inside (?) HFS0
            byte[] metaInfoBytes = new byte[64];
            for (int i=0; i < filesCnt; i++){
                if (raf.read(metaInfoBytes) != 64) {
                    throw new Exception("Read HFS0 File Entry Table failure for file # "+i);
                }
                offsetHfs0files[i] = getLElong(metaInfoBytes, 0);
                sizeHfs0files[i] = getLElong(metaInfoBytes, 8);
                hashedRegionSizeHfs0Files[i] = getLEint(metaInfoBytes, 20);
                paddingHfs0Files[i] = Arrays.equals(Arrays.copyOfRange(metaInfoBytes, 24, 32), new byte[8]);
                SHA256HashHfs0Files[i] = Arrays.copyOfRange(metaInfoBytes, 32, 64);

                strTableOffsets[i] = getLEint(metaInfoBytes, 16);
            }
            // Define location of actual data for this HFS0
            rawFileDataStart = raf.getFilePointer()+stringTableSize;
            if (stringTableSize <= 0)
                throw new Exception("String table size of HFS0 less or equal to zero");
            byte[] stringTbl = new byte[stringTableSize];
            if (raf.read(stringTbl) != stringTableSize){
                throw new Exception("Read HFS0 String table failure. Can't read requested string table size ("+stringTableSize+")");
            }
            String[] namesHfs0files = new String[filesCnt];
            // Parse string table
            for (int i=0; i < filesCnt; i++){
                int j = 0;
                while (stringTbl[strTableOffsets[i]+j] != (byte)0x00)
                    j++;
                namesHfs0files[i] = new String(stringTbl, strTableOffsets[i], j, StandardCharsets.UTF_8);
            }
            //----------------------------------------------------------------------------------------------------------
            // Set files
            for (int i=0; i < filesCnt; i++){
                hfs0Files[i] = new HFS0File(
                        namesHfs0files[i],
                        offsetHfs0files[i],
                        sizeHfs0files[i],
                        hashedRegionSizeHfs0Files[i],
                        paddingHfs0Files[i],
                        SHA256HashHfs0Files[i]
                );
            }
        }
        catch (IOException ioe){
            throw new Exception("Read HFS0 structure failure: "+ioe.getMessage());
        }
    }

    public boolean isMagicHFS0() { return magicHFS0; }
    public int getFilesCnt() { return filesCnt; }
    public boolean isPaddingHfs0() { return paddingHfs0; }
    public int getStringTableSize() { return stringTableSize; }

    public long getRawFileDataStart() { return rawFileDataStart; }
    public HFS0File[] getHfs0Files() { return hfs0Files; }
}