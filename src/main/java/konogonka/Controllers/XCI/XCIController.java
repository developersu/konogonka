package konogonka.Controllers.XCI;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import konogonka.AppPreferences;
import konogonka.Controllers.ITabController;
import konogonka.Tools.ISuperProvider;
import konogonka.Tools.XCI.XCIProvider;
import konogonka.Workers.AnalyzerXCI;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import static konogonka.LoperConverter.byteArrToHexString;

public class XCIController implements ITabController {

    /* Header */
    @FXML
    private TextField rsa2048pkcs1TF,
            secureAreaStartAddrTF,
            pkgIdTF,
            gcInfoIVTF,
            Hfs0headerSHA256Lbl,
            Hfs0initDataSHA256Lbl,
            normAreaEndAddrTF;
    @FXML
    private Label headLbl,
            bkupAreaStartAddrLbl,
            titleKEKIndexLbl,
            gcSizeLbl,
            gcHdrVersLbl,
            gcFlags,
            vDataEndAddrLbl,
            hfs0partOffLbl,
            hfs0hdrSizeLbl,
            secureModeFlagLbl,
            titleKeyFlagLbl,
            keyFlagLbl;
    /* GC Info */
    @FXML
    private TextField fwModeTF,
            cupVersionTF,
            emptyPadding1TF,
            updPartHashTF,
            cupIDTF,
            emptyPadding2TF;
    @FXML
    private Label fwVersionLbl,
            accessCtrlFlagsLbl,
            readWaitTime1Lbl,
            readWaitTime2Lbl,
            writeWaitTime1,
            writeWaitTime2,
            emptyPadding1Lbl,
            emptyPadding2Lbl;
    /* GC Cert */
    @FXML
    private TextField rsa2048PKCS1sigCertTF,
            magicCertCertTF,
            unknown1CertTF,
            unknown2CertTF,
            deviceIDCertTF,
            unknown3CertTF,
            encryptedDataCertTF;
    @FXML
    private Label kekIndexCertLbl,
            magicCertCertOkLbl;

    @FXML
    private HFSBlockController
            HFSBlockMainController,
            HFSBlockUpdateController,
            HFSBlockNormalController,
            HFSBlockSecureController,
            HFSBlockLogoController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        HFSBlockMainController.setTitlePaneTypeName("main", "HFS0 (Primary): 0xF000");
        HFSBlockUpdateController.setTitlePaneTypeName("update", "HFS0 update");
        HFSBlockNormalController.setTitlePaneTypeName("normal", "HFS0 normal");
        HFSBlockSecureController.setTitlePaneTypeName("secure", "HFS0 secure");
        HFSBlockLogoController.setTitlePaneTypeName("logo", "HFS0 logo");
    }

    /**
     * Start analyze XCI
     * */
    @Override
    public void analyze(File selectedFile, long offset){
        // TODO: IMPLEMENT
        return;
    }
    @Override
    public void analyze(File selectedFile){
        HFSBlockController.setSelectedFile(selectedFile);

        AnalyzerXCI analyzerXCI = new AnalyzerXCI(selectedFile, AppPreferences.getInstance().getXciHeaderKey());
        analyzerXCI.setOnSucceeded(e->{
            populateFields(analyzerXCI.getValue());
        });
        Thread workThread = new Thread(analyzerXCI);
        workThread.setDaemon(true);
        workThread.start();
    }
    @Override
    public void analyze(ISuperProvider parentProvider, int fileNo) throws Exception {
        throw new Exception("Not supported for XCI");
    }
    @Override
    public void resetTab(){
        HFSBlockController.setSelectedFile(null);
        /* Header */
        rsa2048pkcs1TF.setText("-");
        headLbl.setText("-");
        secureAreaStartAddrTF.setText("-");
        bkupAreaStartAddrLbl.setText("-");
        titleKEKIndexLbl.setText("-");
        gcSizeLbl.setText("-");
        gcHdrVersLbl.setText("-");
        gcFlags.setText("-");
        pkgIdTF.setText("-");
        vDataEndAddrLbl.setText("-");
        gcInfoIVTF.setText("-");
        hfs0partOffLbl.setText("-");
        hfs0hdrSizeLbl.setText("-");
        Hfs0headerSHA256Lbl.setText("-");
        Hfs0initDataSHA256Lbl.setText("-");
        secureModeFlagLbl.setText("-");
        titleKeyFlagLbl.setText("-");
        keyFlagLbl.setText("-");
        normAreaEndAddrTF.setText("-");
        /* GC Info */
        fwVersionLbl.setText("-");
        accessCtrlFlagsLbl.setText("-");
        readWaitTime1Lbl.setText("-");
        readWaitTime2Lbl.setText("-");
        writeWaitTime1.setText("-");
        writeWaitTime2.setText("-");
        fwModeTF.setText("-");
        cupVersionTF.setText("-");
        emptyPadding1TF.setText("-");
        updPartHashTF.setText("-");
        cupIDTF.setText("-");
        emptyPadding2TF.setText("-");
        emptyPadding1Lbl.setText("-");
        emptyPadding2Lbl.setText("-");

        /* Certificate  */
        rsa2048PKCS1sigCertTF.setText("-");
        magicCertCertTF.setText("-");
        magicCertCertOkLbl.setText("-");
        unknown1CertTF.setText("-");
        kekIndexCertLbl.setText("-");
        unknown2CertTF.setText("-");
        deviceIDCertTF.setText("-");
        unknown3CertTF.setText("-");
        encryptedDataCertTF.setText("-");

        /* HFS0 */
        HFSBlockMainController.resetTab();
        HFSBlockUpdateController.resetTab();
        HFSBlockNormalController.resetTab();
        HFSBlockSecureController.resetTab();
        HFSBlockLogoController.resetTab();
    }
    private void populateFields(XCIProvider xci){
        if (xci != null){
            /* Header */
            rsa2048pkcs1TF.setText(byteArrToHexString(xci.getGCHeader().getRsa2048PKCS1sig()));
            //RainbowHexDump.hexDumpUTF8(xci.getGCHeader().getRsa2048PKCS1sig());
            headLbl.setText(Boolean.toString(xci.getGCHeader().isMagicHeadOk()));
            //System.out.println(xci.getGCHeader().isMagicHeadOk());
            secureAreaStartAddrTF.setText(byteArrToHexString(xci.getGCHeader().getSecureAreaStartAddr()));
            //System.out.println(xci.getGCHeader().isSecureAreaStartAddrOk());
            bkupAreaStartAddrLbl.setText(Boolean.toString(xci.getGCHeader().isBkupAreaStartAddrOk()));
            //System.out.println(xci.getGCHeader().isBkupAreaStartAddrOk());
            titleKEKIndexLbl.setText(String.format("%02x", xci.getGCHeader().getTitleKEKIndexBoth())+": "
                    +String.format("%02x", xci.getGCHeader().getTitleKEKIndex())+" "
                    +String.format("%02x", xci.getGCHeader().getKEKIndex())
            );
            //System.out.print(String.format("%02x", xci.getGCHeader().getTitleKEKIndexBoth())+" ");
            //System.out.print(String.format("%02x", xci.getGCHeader().getTitleKEKIndex())+" ");
            //System.out.println(String.format("%02x", xci.getGCHeader().getKEKIndex()));
            switch (xci.getGCHeader().getGcSize()){
                case (byte) 0xFA:   // 1 GB
                    gcSizeLbl.setText("1 GB");
                    break;
                case (byte) 0xF8:   // 2 GB
                    gcSizeLbl.setText("2 GB");
                    break;
                case (byte) 0xF0:   // 4 GB
                    gcSizeLbl.setText("4 GB");
                    break;
                case (byte) 0xE0:   // 8 GB
                    gcSizeLbl.setText("8 GB");
                    break;
                case (byte) 0xE1:   // 16 GB
                    gcSizeLbl.setText("16 GB");
                    break;
                case (byte) 0xE2:   // 32 GB
                    gcSizeLbl.setText("32 GB");
                    break;
                default:
                    gcSizeLbl.setText("? "+String.format("%02x", xci.getGCHeader().getGcSize()));
            }
            //System.out.println(String.format("%02x", xci.getGCHeader().getGcSize()));
            gcHdrVersLbl.setText(String.format("%02x", xci.getGCHeader().getGcVersion()));
            //System.out.println(String.format("%02x", xci.getGCHeader().getGcVersion()));
            gcFlags.setText(String.format("%02x", xci.getGCHeader().getGcFlags()));
            //System.out.println(String.format("%02x", xci.getGCHeader().getGcFlags()));
            pkgIdTF.setText(byteArrToHexString(xci.getGCHeader().getPkgID()));
            //RainbowHexDump.hexDumpUTF8(xci.getGCHeader().getPkgID());
            vDataEndAddrLbl.setText(Long.toString(xci.getGCHeader().getValDataEndAddr()));
            //System.out.println(xci.getGCHeader().getValDataEndAddr());
            gcInfoIVTF.setText(byteArrToHexString(xci.getGCHeader().getGcInfoIV()));
            //RainbowHexDump.hexDumpUTF8(xci.getGCHeader().getGcInfoIV());
            hfs0partOffLbl.setText(Long.toString(xci.getGCHeader().getHfs0partOffset()));
            //System.out.println(xci.getGCHeader().getHfs0partOffset());
            hfs0hdrSizeLbl.setText(Long.toString(xci.getGCHeader().getHfs0headerSize()));
            //System.out.println(xci.getGCHeader().getHfs0headerSize());
            Hfs0headerSHA256Lbl.setText(byteArrToHexString(xci.getGCHeader().getHfs0headerSHA256()));
            //RainbowHexDump.hexDumpUTF8(xci.getGCHeader().getHfs0headerSHA256());
            Hfs0initDataSHA256Lbl.setText(byteArrToHexString(xci.getGCHeader().getHfs0initDataSHA256()));
            //RainbowHexDump.hexDumpUTF8(xci.getGCHeader().getHfs0initDataSHA256() );
            secureModeFlagLbl.setText(xci.getGCHeader().isSecureModeFlagOk()+" ("+xci.getGCHeader().getSecureModeFlag()+")");
            //System.out.print(xci.getGCHeader().getSecureModeFlag());
            //System.out.println(xci.getGCHeader().isSecureModeFlagOk());
            titleKeyFlagLbl.setText(xci.getGCHeader().istitleKeyFlagOk()+" ("+xci.getGCHeader().getTitleKeyFlag()+")");
            //System.out.print(xci.getGCHeader().getTitleKeyFlag());
            //System.out.println(xci.getGCHeader().istitleKeyFlagOk());
            keyFlagLbl.setText(xci.getGCHeader().iskeyFlagOk()+" ("+xci.getGCHeader().getKeyFlag()+")");
            //System.out.print(xci.getGCHeader().getKeyFlag());
            //System.out.println(xci.getGCHeader().iskeyFlagOk());
            normAreaEndAddrTF.setText(byteArrToHexString(xci.getGCHeader().getNormAreaEndAddr()));
            //System.out.println(xci.getGCHeader().isNormAreaEndAddrOk());

            /* GC Info */
            fwVersionLbl.setText(Long.toString(xci.getGCInfo().getFwVersion()));
            String tempACF = byteArrToHexString(xci.getGCInfo().getAccessCtrlFlags());
            if (tempACF.equals("1100a100"))
                accessCtrlFlagsLbl.setText("25Mhz [0x1100a100]");
            else if(tempACF.equals("1000a100"))
                accessCtrlFlagsLbl.setText("50Mhz [0x1000a100]");
            else
                accessCtrlFlagsLbl.setText("??? ["+tempACF+"]");
            readWaitTime1Lbl.setText(Long.toString(xci.getGCInfo().getReadWaitTime1()));
            readWaitTime2Lbl.setText(Long.toString(xci.getGCInfo().getReadWaitTime2()));
            writeWaitTime1.setText(Long.toString(xci.getGCInfo().getWriteWaitTime1()));
            writeWaitTime2.setText(Long.toString(xci.getGCInfo().getWriteWaitTime2()));
            fwModeTF.setText(byteArrToHexString(xci.getGCInfo().getFwMode()));
            cupVersionTF.setText(byteArrToHexString(xci.getGCInfo().getCupVersion()));
            emptyPadding1Lbl.setText(Boolean.toString(xci.getGCInfo().isEmptyPadding1()));
            emptyPadding1TF.setText(byteArrToHexString(xci.getGCInfo().getEmptyPadding1()));
            updPartHashTF.setText(byteArrToHexString(xci.getGCInfo().getUpdPartHash()));
            cupIDTF.setText(byteArrToHexString(xci.getGCInfo().getCupID()));
            emptyPadding2Lbl.setText(Boolean.toString(xci.getGCInfo().isEmptyPadding2()));
            emptyPadding2TF.setText(byteArrToHexString(xci.getGCInfo().getEmptyPadding2()));

            /* Certificate  */
            rsa2048PKCS1sigCertTF.setText(byteArrToHexString(xci.getGCCert().getRsa2048PKCS1sig()));
            magicCertCertTF.setText(byteArrToHexString(xci.getGCCert().getMagicCert()));
            magicCertCertOkLbl.setText(Boolean.toString(xci.getGCCert().isMagicCertOk()));
            unknown1CertTF.setText(byteArrToHexString(xci.getGCCert().getUnknown1()));
            kekIndexCertLbl.setText(String.format("%02x", xci.getGCCert().getKekIndex()));
            unknown2CertTF.setText(byteArrToHexString(xci.getGCCert().getUnknown2()));
            deviceIDCertTF.setText(byteArrToHexString(xci.getGCCert().getDeviceID()));
            unknown3CertTF.setText(byteArrToHexString(xci.getGCCert().getUnknown3()));
            encryptedDataCertTF.setText(byteArrToHexString(xci.getGCCert().getEncryptedData()));

            /* HFS0 */
            HFSBlockMainController.populateTab(xci.getHfs0ProviderMain());
            HFSBlockUpdateController.populateTab(xci.getHfs0ProviderUpdate());
            HFSBlockNormalController.populateTab(xci.getHfs0ProviderNormal());
            HFSBlockSecureController.populateTab(xci.getHfs0ProviderSecure());
            HFSBlockLogoController.populateTab(xci.getHfs0ProviderLogo());
        }
    }
}