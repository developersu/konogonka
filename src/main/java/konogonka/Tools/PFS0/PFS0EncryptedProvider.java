package konogonka.Tools.PFS0;

import konogonka.ctraes.AesCtrDecryptSimple;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static konogonka.LoperConverter.*;

public class PFS0EncryptedProvider implements IPFS0Provider{
    private long rawFileDataStart;      // For this class is pointing to data start position relative to media block start

    private String magic;
    private int filesCount;
    private int stringTableSize;
    private byte[] padding;
    private PFS0subFile[] pfs0subFiles;

    //---------------------------------------

    private long rawBlockDataStart;

    private PFS0DecryptedStreamProvider pfs0DecryptedStreamProvider;

    // Let's do some fuck
    private class PFS0DecryptedStreamProvider{
        private long mediaStartOffset;  // * 0x200
        private long mediaEndOffset;


        private RandomAccessFile raf;
        private PipedOutputStream streamOut;
        private PipedInputStream streamInp;
        private AesCtrDecryptSimple aesCtrDecryptSimple;        // if null, then exception happened.

        public PipedInputStream getPipedInputStream(){ return streamInp; }


        public void getStarted(PFS0subFile subFile) throws Exception{

            System.out.println("rawBlockDataStart (PFS0 Start): "+rawBlockDataStart);
            System.out.println("Skip blocks:       "+rawBlockDataStart/0x200);                                  // aesCtrDecryptSimple.skip(THIS)
            System.out.println("Skip bytes:        "+ (rawBlockDataStart-(rawBlockDataStart/0x200)*0x200));     // write to stream after skiping THIS

            // DBG START
            File contentFile = new File("/tmp/pfs0-NCA0block.pfs0");
            BufferedOutputStream extractedFileOS = new BufferedOutputStream(new FileOutputStream(contentFile));
            // DBG END
            long mediaBlockSize = mediaEndOffset - mediaStartOffset;
            byte[] encryptedBlock;
            byte[] dectyptedBlock;

            // Skip full-size blocks of 512 bytes that we don't need and start decryption from required one
            long startFromBlock = rawBlockDataStart/0x200;
            if (startFromBlock > 0) {
                aesCtrDecryptSimple.skipNext(startFromBlock);
                raf.seek(raf.getFilePointer() + (startFromBlock*0x200));
            }
            // Since our data could be located in position with some offset from the decrypted block, let's skip bytes left
            int skipBytes = (int)(rawBlockDataStart-(rawBlockDataStart/0x200)*0x200);
            if (skipBytes > 0){
                encryptedBlock = new byte[0x200];
                if (raf.read(encryptedBlock) != -1){
                    dectyptedBlock = aesCtrDecryptSimple.dectyptNext(encryptedBlock);
                    try {
                        // DBG START
                        extractedFileOS.write(dectyptedBlock, skipBytes, 0x200-skipBytes);
                        // DBG END
                        //streamOut.write(dectyptedBlock, skipBytes, 0x200);
                    }
                    catch (IOException e){
                        System.out.println("Exception @extract 1st bock");
                    }
                }
                startFromBlock++;
            }

            for (long i = startFromBlock; i < mediaBlockSize; i++){
                encryptedBlock = new byte[0x200];
                if (raf.read(encryptedBlock) != -1){
                    //dectyptedBlock = aesCtr.decrypt(encryptedBlock);
                    dectyptedBlock = aesCtrDecryptSimple.dectyptNext(encryptedBlock);
                    // Writing decrypted data to pipe
                    try {
                        // DBG START
                        extractedFileOS.write(dectyptedBlock);
                        // DBG END
                        //streamOut.write(dectyptedBlock);
                    }
                    catch (IOException e){
                        System.out.println("Exception @extract");
                        break;
                    }
                }
            }

            // DBG START
            extractedFileOS.close();
            // DBG END
        }

        PFS0DecryptedStreamProvider(File file,
                                    long rawBlockDataStart,
                                    long offsetPositionInFile,
                                    byte[] key,
                                    byte[] sectionCTR,
                                    long mediaStartOffset,
                                    long mediaEndOffset
        ){
            this.mediaStartOffset = mediaStartOffset;
            this.mediaEndOffset = mediaEndOffset;

            try {
                this.raf = new RandomAccessFile(file, "r");
                this.raf.seek(offsetPositionInFile + (mediaStartOffset * 0x200));
                this.aesCtrDecryptSimple = new AesCtrDecryptSimple(key, sectionCTR, mediaStartOffset * 0x200);

                this.streamOut = new PipedOutputStream();
                this.streamInp = new PipedInputStream(streamOut);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    // TODO: simplify
    public void setMeta(
            long offsetPositionInFile,
            File fileWithEncPFS0,
            byte[] key,
            byte[] sectionCTR,
            long mediaStartOffset,
            long mediaEndOffset
    ){
        this.pfs0DecryptedStreamProvider = new PFS0DecryptedStreamProvider(
                fileWithEncPFS0,
                rawBlockDataStart,
                offsetPositionInFile,
                key,
                sectionCTR,
                mediaStartOffset,
                mediaEndOffset
        );
        try{
            pfs0DecryptedStreamProvider.getStarted(pfs0subFiles[0]);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    //---------------------------------------
    public PFS0EncryptedProvider(PipedInputStream pipedInputStream,
                                 long pfs0offsetPosition
    ) throws Exception{
        // pfs0offsetPosition is a position relative to Media block. Lets add pfs0 'header's' bytes count and get raw data start position in media block
        rawFileDataStart = -1;      // Set -1 for PFS0EncryptedProvider
        // Detect raw data start position using next var
        rawBlockDataStart = pfs0offsetPosition;

        byte[] fileStartingBytes = new byte[0x10];
        // Read PFS0Provider, files count, header, padding (4 zero bytes)

        for (int i = 0; i < 0x10; i++){
            int currentByte = pipedInputStream.read();
            if (currentByte == -1) {
                throw new Exception("PFS0: Reading stream suddenly ended while trying to read starting 0x10 bytes");
            }
            fileStartingBytes[i] = (byte)currentByte;
        }
        // Update position
        rawBlockDataStart += 0x10;
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
            // Update position
            rawBlockDataStart += 0x18;
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
        // Update position
        rawBlockDataStart += stringTableSize;

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

    }

    @Override
    public boolean isEncrypted() { return true; }
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

    @Override
    public PipedInputStream getProviderSubFilePipedInpStream(String subFileName) {
        //TODO
        return null;
    }

    @Override
    public PipedInputStream getProviderSubFilePipedInpStream(int subFileNumber) {
        //TODO
        return null;
    }
}
