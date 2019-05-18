package konogonka.Controllers.NCA;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import konogonka.AppPreferences;
import konogonka.Controllers.TabController;
import konogonka.Tools.NCA.NCAContentPFS0;
import konogonka.Tools.NCA.NCAProvider;
import konogonka.Workers.AnalyzerNCA;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import static konogonka.LoperConverter.byteArrToHexString;

public class NCAController implements TabController {

    private File selectedFile;
    @FXML
    private NCASectionHeaderBlockController
            NCASectionHeaderFirstController,
            NCASectionHeaderSecondController,
            NCASectionHeaderThirdController,
            NCASectionHeaderFourthController;
    @FXML
    private NCASectionContentController
            NCASectionContentFirstController,
            NCASectionContentSecondController,
            NCASectionContentThirdController,
            NCASectionContentFourthController;
    @FXML
    private NCATableController
            NCATable1Controller,
            NCATable2Controller,
            NCATable3Controller,
            NCATable4Controller;
    @FXML
    private Label
            magicnumLbl,
            systemOrGcIndLbl,
            contentTypeLbl,
            cryptoType1Lbl,
            keyIndexLbl,
            ncaSizeLbl,
            titleIdLbl,
            sdkVersionLbl,
            cryptoType2Lbl,
            ticketLbl;
    @FXML
    private TextField
            rsa2048oneTF,
            rsa2048twoTF,
            sha256section1TF,
            sha256section2TF,
            sha256section3TF,
            sha256section4TF,
            keyAreaEnKey0TF,
            keyAreaEnKey1TF,
            keyAreaEnKey2TF,
            keyAreaEnKey3TF,
            keyAreaDecKey0TF,
            keyAreaDecKey1TF,
            keyAreaDecKey2TF,
            keyAreaDecKey3TF;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @Override
    public void analyze(File file) {
        this.selectedFile = file;
        HashMap<String, String> keysMap = new HashMap<>();
        keysMap.put("header_key", AppPreferences.getInstance().getHeaderKey());
        for (int i = 0; i < 6; i++){
            keysMap.put("key_area_key_application_0"+i, AppPreferences.getInstance().getApplicationKey(i));
            keysMap.put("key_area_key_ocean_0"+i, AppPreferences.getInstance().getOceanKey(i));
            keysMap.put("key_area_key_system_0"+i, AppPreferences.getInstance().getSystemKey(i));
        }
        
        AnalyzerNCA analyzerNCA = new AnalyzerNCA(file, keysMap);
        analyzerNCA.setOnSucceeded(e->{
            populateFields(analyzerNCA.getValue());
        });
        Thread workThread = new Thread(analyzerNCA);
        workThread.setDaemon(true);
        workThread.start();
    }

    @Override
    public void resetTab() {
        // Header
        rsa2048oneTF.setText("-");
        rsa2048twoTF.setText("-");
        magicnumLbl.setText("-");
        systemOrGcIndLbl.setText("-");
        contentTypeLbl.setText("-");
        cryptoType1Lbl.setText("-");
        keyIndexLbl.setText("-");
        ncaSizeLbl.setText("-");
        titleIdLbl.setText("-");
        sdkVersionLbl.setText("-");
        cryptoType2Lbl.setText("-");
        ticketLbl.setText("-");
        sha256section1TF.setText("-");
        sha256section2TF.setText("-");
        sha256section3TF.setText("-");
        sha256section4TF.setText("-");
        keyAreaEnKey0TF.setText("-");
        keyAreaEnKey1TF.setText("-");
        keyAreaEnKey2TF.setText("-");
        keyAreaEnKey3TF.setText("-");

        keyAreaDecKey0TF.setText("-");
        keyAreaDecKey1TF.setText("-");
        keyAreaDecKey2TF.setText("-");
        keyAreaDecKey3TF.setText("-");
        // Tables
        NCATable1Controller.resetTab();
        NCATable2Controller.resetTab();
        NCATable3Controller.resetTab();
        NCATable4Controller.resetTab();
        // Table blocks
        NCASectionHeaderFirstController.resetTab();
        NCASectionHeaderSecondController.resetTab();
        NCASectionHeaderThirdController.resetTab();
        NCASectionHeaderFourthController.resetTab();
        // Section content blocks
        NCASectionContentFirstController.resetTab();
        NCASectionContentSecondController.resetTab();
        NCASectionContentThirdController.resetTab();
        NCASectionContentFourthController.resetTab();
    }

    private void populateFields(NCAProvider ncaProvider){
        if (ncaProvider != null){
            rsa2048oneTF.setText(byteArrToHexString(ncaProvider.getRsa2048one()));
            rsa2048twoTF.setText(byteArrToHexString(ncaProvider.getRsa2048two()));
            magicnumLbl.setText(ncaProvider.getMagicnum());
            systemOrGcIndLbl.setText(Byte.toString(ncaProvider.getSystemOrGcIndicator()));
            contentTypeLbl.setText(Byte.toString(ncaProvider.getContentType()));
            cryptoType1Lbl.setText(Byte.toString(ncaProvider.getCryptoType1()));
            keyIndexLbl.setText(Byte.toString(ncaProvider.getKeyIndex()));
            ncaSizeLbl.setText(Long.toString(ncaProvider.getNcaSize()));
            titleIdLbl.setText(byteArrToHexString(ncaProvider.getTitleId()));
            sdkVersionLbl.setText(ncaProvider.getSdkVersion()[3]
                    +"."+ncaProvider.getSdkVersion()[2]
                    +"."+ncaProvider.getSdkVersion()[1]
                    +"."+ncaProvider.getSdkVersion()[0]);
            cryptoType2Lbl.setText(Byte.toString(ncaProvider.getCryptoType2()));
            ticketLbl.setText(byteArrToHexString(ncaProvider.getRightsId()));
            sha256section1TF.setText(byteArrToHexString(ncaProvider.getSha256hash0()));
            sha256section2TF.setText(byteArrToHexString(ncaProvider.getSha256hash1()));
            sha256section3TF.setText(byteArrToHexString(ncaProvider.getSha256hash2()));
            sha256section4TF.setText(byteArrToHexString(ncaProvider.getSha256hash3()));
            keyAreaEnKey0TF.setText(byteArrToHexString(ncaProvider.getEncryptedKey0()));
            keyAreaEnKey1TF.setText(byteArrToHexString(ncaProvider.getEncryptedKey1()));
            keyAreaEnKey2TF.setText(byteArrToHexString(ncaProvider.getEncryptedKey2()));
            keyAreaEnKey3TF.setText(byteArrToHexString(ncaProvider.getEncryptedKey3()));

            keyAreaDecKey0TF.setText(byteArrToHexString(ncaProvider.getDecryptedKey0()));
            keyAreaDecKey1TF.setText(byteArrToHexString(ncaProvider.getDecryptedKey1()));
            keyAreaDecKey2TF.setText(byteArrToHexString(ncaProvider.getDecryptedKey2()));
            keyAreaDecKey3TF.setText(byteArrToHexString(ncaProvider.getDecryptedKey3()));
            // Tables
            NCATable1Controller.populateTab(ncaProvider.getTableEntry0());
            NCATable2Controller.populateTab(ncaProvider.getTableEntry1());
            NCATable3Controller.populateTab(ncaProvider.getTableEntry2());
            NCATable4Controller.populateTab(ncaProvider.getTableEntry3());
            // Table blocks
            NCASectionHeaderFirstController.populateTab(ncaProvider.getSectionBlock0());
            NCASectionHeaderSecondController.populateTab(ncaProvider.getSectionBlock1());
            NCASectionHeaderThirdController.populateTab(ncaProvider.getSectionBlock2());
            NCASectionHeaderFourthController.populateTab(ncaProvider.getSectionBlock3());
            // Section content blocks
            // TODO: FIX: This code executes getNCAContentPFS0() method twice
            NCASectionContentFirstController.populateFields(ncaProvider.getNCAContentPFS0(0).getPfs0(), selectedFile, ncaProvider.getNCAContentPFS0(0).getSHA256hashes());
            NCASectionContentSecondController.populateFields(ncaProvider.getNCAContentPFS0(1).getPfs0(), selectedFile, ncaProvider.getNCAContentPFS0(1).getSHA256hashes());
            NCASectionContentThirdController.populateFields(ncaProvider.getNCAContentPFS0(2).getPfs0(), selectedFile, ncaProvider.getNCAContentPFS0(2).getSHA256hashes());
            NCASectionContentFourthController.populateFields(ncaProvider.getNCAContentPFS0(3).getPfs0(), selectedFile, ncaProvider.getNCAContentPFS0(3).getSHA256hashes());
        }
    }
}
