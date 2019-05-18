package konogonka.Tools.XCI;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class XCIProvider{
    // TODO: Since LOGO partition added, we have to handle it properly. Is it works??

    //private BufferedInputStream xciBIS;
    private XCIGamecardHeader xciGamecardHeader;
    private XCIGamecardInfo xciGamecardInfo;
    private XCIGamecardCert xciGamecardCert;
    private HFS0Provider hfs0ProviderMain,
            hfs0ProviderUpdate,
            hfs0ProviderNormal,
            hfs0ProviderSecure,
            hfs0ProviderLogo;

    public XCIProvider(File file, String XCI_HEADER_KEY) throws Exception{                                                   // TODO: ADD FILE SIZE CHECK !!! Check xciHdrKey
        RandomAccessFile raf;

        try {
            //xciBIS = new BufferedInputStream(new FileInputStream(file));
            raf = new RandomAccessFile(file, "r");
        }
        catch (FileNotFoundException fnfe){
            throw new Exception("XCI File not found: \n  "+fnfe.getMessage());
        }

        if (file.length() < 0xf010)
            throw new Exception("XCI File is too small.");

        try{
            byte[] gamecardHeaderBytes = new byte[400];
            byte[] gamecardInfoBytes = new byte[112];
            byte[] gamecardCertBytes = new byte[512];

            // Creating GC Header class
            if (raf.read(gamecardHeaderBytes) != 400) {
                raf.close();
                throw new Exception("XCI Can't read Gamecard Header bytes.");
            }
            xciGamecardHeader = new XCIGamecardHeader(gamecardHeaderBytes);     // throws exception
            // Creating GC Info class
            if (raf.read(gamecardInfoBytes) != 112) {
                raf.close();
                throw new Exception("XCI Can't read Gamecard Header bytes.");
            }
            xciGamecardInfo = new XCIGamecardInfo(gamecardInfoBytes, xciGamecardHeader.getGcInfoIV(), XCI_HEADER_KEY);
            // Creating GC Cerfificate class
            raf.seek(0x7000);
            if (raf.read(gamecardCertBytes) != 512) {
                raf.close();
                throw new Exception("XCI Can't read Gamecard certificate bytes.");
            }
            xciGamecardCert = new XCIGamecardCert(gamecardCertBytes);

            hfs0ProviderMain = new HFS0Provider(0xf000, raf);
            if (hfs0ProviderMain.getFilesCnt() < 3){
                raf.close();
                throw new Exception("XCI Can't read Gamecard certificate bytes.");
            }
            // Get all partitions from the main HFS0 file
            String partition;
            for (HFS0File hfs0File: hfs0ProviderMain.getHfs0Files()){
                partition = hfs0File.getName();
                if (partition.equals("update")) {
                    hfs0ProviderUpdate = new HFS0Provider(hfs0ProviderMain.getRawFileDataStart() + hfs0File.getOffset(), raf);
                    continue;
                }
                if (partition.equals("normal")) {
                    hfs0ProviderNormal = new HFS0Provider(hfs0ProviderMain.getRawFileDataStart() + hfs0File.getOffset(), raf);
                    continue;
                }
                if (partition.equals("secure")) {
                    hfs0ProviderSecure = new HFS0Provider(hfs0ProviderMain.getRawFileDataStart() + hfs0File.getOffset(), raf);
                    continue;
                }
                if (partition.equals("logo")) {
                    hfs0ProviderLogo = new HFS0Provider(hfs0ProviderMain.getRawFileDataStart() + hfs0File.getOffset(), raf);
                }
            }
            raf.close();
        }
        catch (IOException ioe){
            throw new Exception("XCI Failed file analyze for ["+file.getName()+"]\n  "+ioe.getMessage());
        }
    }
    /**
     * Getters
     * */
    public XCIGamecardHeader getGCHeader(){ return this.xciGamecardHeader; }
    public XCIGamecardInfo getGCInfo(){ return this.xciGamecardInfo; }
    public XCIGamecardCert getGCCert(){ return this.xciGamecardCert; }
    public HFS0Provider getHfs0ProviderMain() { return this.hfs0ProviderMain; }
    public HFS0Provider getHfs0ProviderUpdate() { return this.hfs0ProviderUpdate; }
    public HFS0Provider getHfs0ProviderNormal() { return this.hfs0ProviderNormal; }
    public HFS0Provider getHfs0ProviderSecure() { return this.hfs0ProviderSecure; }
    public HFS0Provider getHfs0ProviderLogo() { return this.hfs0ProviderLogo; }
}